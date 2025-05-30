/*
 * $Id$
 *
 * ace - a collaborative editor
 * Copyright (C) 2005 Mark Bigler, Simon Raess, Lukas Zbinden
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package ch.iserver.ace.collaboration.jupiter;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.swing.event.EventListenerList;

import org.apache.log4j.Logger;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.algorithm.jupiter.JupiterTimestampFactory;
import ch.iserver.ace.collaboration.CollaborationService;
import ch.iserver.ace.collaboration.DiscoveryCallback;
import ch.iserver.ace.collaboration.DocumentListener;
import ch.iserver.ace.collaboration.InvitationCallback;
import ch.iserver.ace.collaboration.PublishedSession;
import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.collaboration.RemoteDocument;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.ServiceFailureHandler;
import ch.iserver.ace.collaboration.UserListener;
import ch.iserver.ace.collaboration.jupiter.server.PublisherPort;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogic;
import ch.iserver.ace.collaboration.jupiter.server.ServerLogicImpl;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.InvitationProxy;
import ch.iserver.ace.net.NetworkService;
import ch.iserver.ace.net.NetworkServiceCallback;
import ch.iserver.ace.net.RemoteDocumentProxy;
import ch.iserver.ace.net.RemoteUserProxy;
import ch.iserver.ace.util.AopUtil;
import ch.iserver.ace.util.LoggingInterceptor;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SingleThreadDomain;
import ch.iserver.ace.util.ThreadDomain;

/**
 * Default implementation of the CollaborationService interface.
 */
public class CollaborationServiceImpl implements CollaborationService, NetworkServiceCallback {
	
	/**
	 * Logger used by instances of this class.
	 */
	private static final Logger LOG = Logger.getLogger(CollaborationServiceImpl.class);

	/**
	 * Listener list for document and user listeners. 
	 */
	private final EventListenerList listeners = new EventListenerList();

	/**
	 * The network service object from the network layer.
	 */
	private final NetworkService service;
	
	/**
	 * The callback from the application layer for invitations from other users. 
	 */
	private InvitationCallback callback = NullInvitationCallback.getInstance();
	
	/**
	 * The failure handler for failures in this or in the network layer.
	 */
	private ServiceFailureHandler failureHandler;
	
	/**
	 * The user registry holding all currently known users.
	 */
	private UserRegistry userRegistry;
	
	/**
	 * The document registry holding all currently known documents.
	 */
	private DocumentRegistry documentRegistry;
	
	/**
	 * The session factory used to create sessions.
	 */
	private SessionFactory sessionFactory;
		
	/**
	 * The thread domain used for outgoing participant connections.
	 */
	private ThreadDomain publisherThreadDomain;
	
	/**
	 * The factory to create AcknowledgeStrategy objects.
	 */
	private AcknowledgeStrategyFactory acknowledgeStrategyFactory = new NullAcknowledgeStrategyFactory();
	
	/**
	 * Creates a new CollaborationServiceImp instance using the passed in 
	 * network service.
	 * 
	 * @param service the network service object from the lower layer
	 */
	public CollaborationServiceImpl(NetworkService service) {
		ParameterValidator.notNull("service", service);
		this.service = service;
		this.service.setCallback(this);
		this.service.setTimestampFactory(new JupiterTimestampFactory());
	}
		
	/**
	 * The user registry of the application. This object holds all currently 
	 * known users.
	 * 
	 * @return the user registry of the application
	 */
	public UserRegistry getUserRegistry() {
		return userRegistry;
	}
	
	/**
	 * Sets the user registry of the application.
	 * 
	 * @param registry the new registry of the application
	 */
	public void setUserRegistry(UserRegistry registry) {
		ParameterValidator.notNull("registry", registry);
		this.userRegistry = registry;
	}
	
	/**
	 * The document registry of the application. This object holds all currently
	 * known documents.
	 * 
	 * @return the document registry of the application
	 */
	public DocumentRegistry getDocumentRegistry() {
		return documentRegistry;
	}
	
	/**
	 * Sets the document registry of the application.
	 * 
	 * @param registry the new document registry
	 */
	public void setDocumentRegistry(DocumentRegistry registry) {
		ParameterValidator.notNull("registry", registry);
		this.documentRegistry = registry;
	}
		
	/**
	 * Gets the session factory used to create participant session objects.
	 * 
	 * @return the session factory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * Sets the session factory used to create participant sessions.
	 * 
	 * @param sessionFactory the new factory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}
	
	/**
	 * Gets the publisher thread domain of the collaboration service. The
	 * publisher thread domain is used by the publisher to wrap outgoing
	 * participant connections.
	 * 
	 * @return the publisher thread domain
	 */
	public ThreadDomain getPublisherThreadDomain() {
		return publisherThreadDomain;
	}
	
	/**
	 * Sets the publisher thread domain of the collaboration service.
	 * 
	 * @param domain the new publisher thread domain
	 */
	public void setPublisherThreadDomain(ThreadDomain domain) {
		ParameterValidator.notNull("domain", domain);
		this.publisherThreadDomain = domain;
	}
	
	/**
	 * Creates the thread domain for a published session. This should be
	 * a {@link SingleThreadDomain} unless you are sure that only one
	 * thread is accessing the ServerLogic and ParticipantPort objects,
	 * in which case you can use {@link ch.iserver.ace.util.CallerThreadDomain}.
	 * However, this happens mostly in test scenarios where multiple
	 * threads are unwelcome.
	 * 
	 * @return the new single thread domain for the published session
	 */
	public ThreadDomain createIncomingDomain() {
		return new SingleThreadDomain();
	}
	
	/**
	 * Gets the network service object from the network layer.
	 * 
	 * @return the network service
	 */
	protected NetworkService getNetworkService() {
		return service;
	}
	
	/**
	 * Gets the invitation callback from the application layer.
	 * 
	 * @return the invitation callback
	 */
	protected InvitationCallback getInvitationCallback() {
		return callback;
	}
	
	/**
	 * Sets the acknowledge strategy factory of the collaboration service.
	 * 
	 * @param factory the new factory
	 */
	public void setAcknowledgeStrategyFactory(AcknowledgeStrategyFactory factory) {
		this.acknowledgeStrategyFactory = factory;
	}
	
	/**
	 * Gets the acknowledge strategy factory used by the collaboration
	 * service.
	 * 
	 * @return the acknowledge strategy factory
	 */
	public AcknowledgeStrategyFactory getAcknowledgeStrategyFactory() {
		return acknowledgeStrategyFactory;
	}

	// --> CollaborationService interface methods <--
	
	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#getServerInfo()
	 */
	public ServerInfo getServerInfo() {
		return getNetworkService().getServerInfo();
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#start()
	 */
	public void start() {
		getNetworkService().start();
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#stop()
	 */
	public void stop() {
		try {
			getNetworkService().stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#setUserId(java.lang.String)
	 */
	public void setUserId(String id) {
		getNetworkService().setUserId(id);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#setUserDetails(ch.iserver.ace.UserDetails)
	 */
	public void setUserDetails(UserDetails details) {
		getNetworkService().setUserDetails(details);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#addUserListener(ch.iserver.ace.collaboration.UserListener)
	 */
	public void addUserListener(UserListener listener) {
		ParameterValidator.notNull("listener", listener);
		listeners.add(UserListener.class, listener);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#removeUserListener(ch.iserver.ace.collaboration.UserListener)
	 */
	public void removeUserListener(UserListener listener) {
		listeners.remove(UserListener.class, listener);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#addDocumentListener(ch.iserver.ace.collaboration.DocumentListener)
	 */
	public void addDocumentListener(DocumentListener listener) {
		ParameterValidator.notNull("listener", listener);
		listeners.add(DocumentListener.class, listener);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#removeDocumentListener(ch.iserver.ace.collaboration.DocumentListener)
	 */
	public void removeDocumentListener(DocumentListener listener) {
		listeners.remove(DocumentListener.class, listener);
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#setInvitationCallback(ch.iserver.ace.collaboration.InvitationCallback)
	 */
	public void setInvitationCallback(InvitationCallback callback) {
		this.callback = callback == null ? NullInvitationCallback.getInstance() : callback;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#setFailureHandler(ch.iserver.ace.collaboration.ServiceFailureHandler)
	 */
	public void setFailureHandler(ServiceFailureHandler handler) {
		this.failureHandler = handler;
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#publish(ch.iserver.ace.collaboration.PublishedSessionCallback, ch.iserver.ace.DocumentModel)
	 */
	public PublishedSession publish(PublishedSessionCallback callback, DocumentModel document) {
		PublishedSessionImpl session = new PublishedSessionImpl(callback);
		session.setAcknowledgeStrategy(getAcknowledgeStrategyFactory().createStrategy());
		session.setUserRegistry(getUserRegistry());
		
		ThreadDomain threadDomain = createIncomingDomain();
		threadDomain.setName("incoming-session-logic");
		
		ServerLogicImpl target = new ServerLogicImpl(
						threadDomain,
						getPublisherThreadDomain(), 
						document,
						getUserRegistry());
		target.setAcknowledgeStrategyFactory(getAcknowledgeStrategyFactory());
	
		PublisherConnection connection = (PublisherConnection) AopUtil.wrap(
						session,
						PublisherConnection.class,
						new LoggingInterceptor(CollaborationServiceImpl.class)
		);
		PublisherPort port = target.initPublisherConnection(connection);
		session.setPublisherPort(port);
		
		ServerLogic logic = (ServerLogic) threadDomain.wrap(target, ServerLogic.class);
		DocumentServer server = getNetworkService().publish(logic, document.getDetails());
		target.setDocumentServer(server);
		target.start();
		
		return session;
	}

	/**
	 * @see ch.iserver.ace.collaboration.CollaborationService#discoverUser(ch.iserver.ace.collaboration.DiscoveryCallback, java.net.InetAddress, int)
	 */
	public void discoverUser(DiscoveryCallback callback, InetAddress addr,
					int port) {
		getNetworkService().discoverUser(
						new DiscoveryNetworkCallbackImpl(callback), 
						addr, 
						port);
	}
	
	// --> network service callback methods <--
	
	/**
	 * Forwards the service failure to the failure handler of the collaboration 
	 * layer.
	 * 
	 * @see ch.iserver.ace.net.NetworkServiceCallback#serviceFailure(int, java.lang.String, java.lang.Exception)
	 */
	public void serviceFailure(int code, String msg, Exception e) {
		if (failureHandler != null) {
			failureHandler.serviceFailed(code, msg, e);
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#documentDiscovered(ch.iserver.ace.net.RemoteDocumentProxy[])
	 */
	public synchronized void documentDiscovered(RemoteDocumentProxy[] proxies) {
		List tmp = new ArrayList(proxies.length);
		for (int i = 0; i < proxies.length; i++) {
			if (getDocumentRegistry().getDocument(proxies[i].getId()) != null) {
				LOG.warn("document with id " + proxies[i].getId() + " discovered before");
			} else {
				tmp.add(getDocumentRegistry().getDocument(proxies[i]));
			}
		}
		
		if (tmp.size() == 0) {
			LOG.warn("all discovered documents discovered before");
			return;
		}
		
		RemoteDocument[] documents = (RemoteDocument[]) tmp.toArray(new RemoteDocument[tmp.size()]);
		DocumentListener[] list = (DocumentListener[]) listeners.getListeners(DocumentListener.class);
		for (int i = 0; i < list.length; i++) {
			DocumentListener listener = list[i];
			listener.documentsDiscovered(documents);
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#documentDetailsChanged(ch.iserver.ace.net.RemoteDocumentProxy)
	 */
	public synchronized void documentDetailsChanged(RemoteDocumentProxy proxy) {
		MutableRemoteDocument doc = getDocumentRegistry().getDocument(proxy.getId());
		if (doc == null) {
			LOG.error("documentDetailsChanged called with an unknown document id (i.e. documentDiscovered not called)");
		} else {
			doc.setTitle(proxy.getDocumentDetails().getTitle());
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#documentDiscarded(ch.iserver.ace.net.RemoteDocumentProxy[])
	 */
	public synchronized void documentDiscarded(RemoteDocumentProxy[] proxies) {
		List tmp = new ArrayList();
		for (int i = 0; i < proxies.length; i++) {
			RemoteDocumentProxy proxy = proxies[i];
			if (proxy == null) {
				throw new IllegalArgumentException("proxies array cannot contain null elements");
			}
			RemoteDocument doc = getDocumentRegistry().removeDocument(proxy);
			if (doc == null) {
				LOG.error("documentDiscarded called without previous documentDiscovered call");
			}
			tmp.add(doc);
		}
		RemoteDocument[] documents = (RemoteDocument[]) tmp.toArray(new RemoteDocument[tmp.size()]);
		DocumentListener[] list = (DocumentListener[]) listeners.getListeners(DocumentListener.class);
		for (int i = 0; i < list.length; i++) {
			DocumentListener listener = list[i];
			listener.documentsDiscarded(documents);
		}
	}
	
	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#userDiscovered(ch.iserver.ace.net.RemoteUserProxy)
	 */
	public synchronized void userDiscovered(RemoteUserProxy proxy) {
		if (getUserRegistry().getUser(proxy.getId()) != null) {
			LOG.warn("user with id " + proxy.getId() + " discovered before: discarding notification");
			return;
		}
		RemoteUser user = getUserRegistry().getUser(proxy);
		UserListener[] listeners = (UserListener[]) this.listeners.getListeners(UserListener.class);
		for (int i = 0; i < listeners.length; i++) {
			UserListener listener = listeners[i];
			listener.userDiscovered(user);
		}
	}

	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#userDetailsChanged(ch.iserver.ace.net.RemoteUserProxy)
	 */
	public synchronized void userDetailsChanged(RemoteUserProxy proxy) {
		MutableRemoteUser user = getUserRegistry().getUser(proxy.getId());
		if (user == null) {
			// TODO: throw exception
			LOG.error("userDetailsChanged called with an unkown user id (i.e. userDiscovered not called)");
		} else {
			user.setName(proxy.getUserDetails().getUsername());
		}
	}

	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#userDiscarded(ch.iserver.ace.net.RemoteUserProxy)
	 */
	public synchronized void userDiscarded(RemoteUserProxy proxy) {
		RemoteUser user = getUserRegistry().removeUser(proxy);
		if (user == null) {
			// TODO: throw exception
			LOG.error("userDiscarded called without previous userDiscovered call");
		} else {
			UserListener[] listeners = (UserListener[]) this.listeners.getListeners(UserListener.class);
			for (int i = 0; i < listeners.length; i++) {
				UserListener listener = listeners[i];
				listener.userDiscarded(user);
			}
		}
	}

	/**
	 * @see ch.iserver.ace.net.NetworkServiceCallback#invitationReceived(ch.iserver.ace.net.InvitationProxy)
	 */
	public void invitationReceived(InvitationProxy proxy) {
		ParameterValidator.notNull("proxy", proxy);
		RemoteDocument document = getDocumentRegistry().getDocument(proxy.getDocument().getId());
		InvitationImpl invitation = new InvitationImpl(
						proxy,
						document,
						getSessionFactory());
		getInvitationCallback().invitationReceived(invitation);
	}
			
}
