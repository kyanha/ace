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

package ch.iserver.ace.net.impl.protocol;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.transport.tcp.TCPSession;

import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.discovery.DiscoveryManagerFactory;

/**
 * Determines the correct RequestHandler a Channel, i.e. it sets
 * the correct request handler to a newly initiated channel.
 */
public class DefaultRequestHandler extends AbstractRequestHandler {

	private static Logger LOG = Logger.getLogger(DefaultRequestHandler.class);
	
	private static Object MUTEX = new Object();
	
	private RequestHandler mainHandler;
	private Deserializer deserializer;
	private ParserHandler handler;
	
	public DefaultRequestHandler(RequestHandler mainHandler, Deserializer deserializer, ParserHandler handler) {
		this.mainHandler = mainHandler;
		this.deserializer = deserializer;
		this.handler = handler;
	}
	
	
	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.RequestHandler#receiveMSG(org.beepcore.beep.core.MessageMSG)
	 */
	public void receiveMSG(MessageMSG message) {
		LOG.debug("--> receiveMSG()");
		try {
			boolean isDiscovery = false;
			RequestHandler requestHandler = null;
			RemoteUserProxyExt proxy = null;
			Channel channel = null;
			//TODO: NetworkService instance could be passed as a constructor argument
			if (!NetworkServiceImpl.getInstance().isStopped()) { 
				InputDataStream input = message.getDataStream();
				byte[] rawData = DataStreamHelper.read(input);
				Request response = null;
				synchronized(MUTEX) {
					deserializer.deserialize(rawData, handler);
					response = handler.getResult();
				}
				channel = message.getChannel();
				int type = response.getType();
				if (type == ProtocolConstants.CHANNEL_MAIN) {
					requestHandler = mainHandler;
					proxy = (RemoteUserProxyExt) response.getPayload();
					isDiscovery = (proxy != null);
				} else if (type == ProtocolConstants.CHANNEL_SESSION) {
					requestHandler = SessionRequestHandlerFactory.getInstance().createHandler();
				} else { //TODO: if channel is for outoging messages, set to SessionConnectionImpl
					LOG.warn("unkown channel type, use main as default");
					requestHandler = mainHandler;
				}
				channel.setRequestHandler(requestHandler);
			}
			
			try {
				if (isDiscovery) {
					LOG.debug("discovered new user: "+proxy);
					processDiscoveredUser(message, channel, proxy);
				} else { //send NUL confirmation
					OutputDataStream os = new OutputDataStream();
					os.setComplete();
					message.sendRPY(os);
//					message.sendNUL();
				}
			} catch (Exception e) {
				LOG.error("could not send confirmation ["+e+", "+e.getMessage()+"]");
			}
			
			cleanup();
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("could not process request ["+e+"]");
		}
		LOG.debug("<-- receiveMSG()");
	}

	/**
	 * Sends a reply with this user's coordinates and adds the user to this ACE editor instance.
	 * 
	 * @param message
	 * @param channel
	 * @param proxy
	 * @throws Exception
	 */
	private void processDiscoveredUser(MessageMSG message, Channel channel, RemoteUserProxyExt proxy) throws Exception {
		String id = NetworkServiceImpl.getInstance().getUserId();
		String name = NetworkServiceImpl.getInstance().getUserDetails().getUsername();
		String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ace><response><user id=\"" + id + "\" name=\"" + name + "\"/></response></ace>";
		byte[] data = xml.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		OutputDataStream output = DataStreamHelper.prepare(data);
		message.sendRPY(output);
		proxy.setDNSSDdiscovered(false);
		DiscoveryManagerFactory.getDiscoveryManager(null).addUser(proxy);
		SessionManager.getInstance().createSession(proxy, (TCPSession) channel.getSession(), channel);
	}


	public void cleanup() {
		mainHandler = null;
		deserializer = null;
		handler = null;
	}


	protected Logger getLogger() {
		return LOG;
	}

}
