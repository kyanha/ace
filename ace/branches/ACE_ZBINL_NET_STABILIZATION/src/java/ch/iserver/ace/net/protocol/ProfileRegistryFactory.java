/*
 * $Id:ProfileRegistryFactory.java 2413 2005-12-09 13:20:12Z zbinl $
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

package ch.iserver.ace.net.protocol;

import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.core.StartChannelListener;

import ch.iserver.ace.algorithm.TimestampFactory;
import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.core.NetworkServiceExt;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.protocol.filter.RequestFilter;
import ch.iserver.ace.net.protocol.filter.RequestFilterFactory;
import ch.iserver.ace.util.ThreadDomain;

/**
 * The <code>ProfileRegistryFactory</code> creates the
 * <code>ProfileRegistry</code>. The ProfileRegistry is
 * a BEEP Core specific class and is used for session
 * management. The ProfileRegistry must be set a
 * <code>StartChannelListener</code> object which must be
 * implmemented by the application.
 * 
 * <p>The ProfileRegistry is available as singleton instance 
 * since ACE uses only one profile.</p>
 *  
 *  @see ProfileRegistry
 *  @see org.beepcore.beep.core.StartChannelListener
 */
public class ProfileRegistryFactory {

	/**
	 * the ProfileRegistry instance
	 */
	private static ProfileRegistry instance;
	
	/**
	 * the main request handler. This object exists as only once and is thus
	 * used by all main connections as the request handler
	 */
	private static RequestHandler mainHandler;
	
	public static ProfileRegistry getProfileRegistry() {
		if (instance == null) {
			Deserializer deserializer = DeserializerImpl.getInstance();
			RequestFilter filter = RequestFilterFactory.createServerChain();
			RequestParserHandler requestHandler = new RequestParserHandler();
//			SingleThreadDomain domain = new SingleThreadDomain();
			NetworkServiceExt service = NetworkServiceImpl.getInstance();
			ThreadDomain domain = service.getMainThreadDomain();
			mainHandler = (RequestHandler) domain.wrap(new MainRequestHandler(deserializer, filter, requestHandler), 
											RequestHandler.class);
			DefaultRequestHandlerFactory.init(mainHandler, deserializer, requestHandler);
			CollaborationParserHandler handler = new CollaborationParserHandler();
			TimestampFactory factory = NetworkServiceImpl.getInstance().getTimestampFactory();
			handler.setTimestampFactory(factory);
			CollaborationDeserializer collabDeserializer = new CollaborationDeserializer();
			//TODO: now, all sessionrequesthandlers share deserializer and handler, but you could do it
			//the same as with participantrequesthandler: each one has its own deserializer and handler
//			NetworkServiceExt service = NetworkServiceImpl.getInstance();
			SessionRequestHandlerFactory.init(collabDeserializer, handler, service);
			StartChannelListener listener = new StartChannelListenerImpl(DefaultRequestHandlerFactory.getInstance());
			DefaultProfile profile = new DefaultProfile(listener);
			StartChannelListener channelListener = null;
			try {
				channelListener = profile.init(NetworkProperties.get(NetworkProperties.KEY_PROFILE_URI), null);
			} catch (BEEPException be) {}
			instance = new ProfileRegistry();
			instance.addStartChannelListener(NetworkProperties.get(NetworkProperties.KEY_PROFILE_URI), channelListener, null);
		}
		return instance;
	}
	
	/**
	 * Gets the singleton MainRequestHandler instance. It is used
	 * by all <code>MainConnection</code> to handle the received requests.
	 * 
	 * @return 	the MainRequestHandler instance
	 */
	public static RequestHandler getMainRequestHandler() {
		return mainHandler;
	}
	
}
