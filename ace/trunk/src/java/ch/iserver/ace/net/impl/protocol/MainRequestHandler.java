/*
 * $Id:RequestHandlerImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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
import org.beepcore.beep.transport.tcp.TCPSession;

import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.discovery.DiscoveryManager;
import ch.iserver.ace.net.impl.discovery.DiscoveryManagerFactory;

/**
 *
 */
public class MainRequestHandler extends AbstractRequestHandler {
	
	private static Logger LOG = Logger.getLogger(MainRequestHandler.class);
	
	private RequestFilter filter;
	private Deserializer deserializer;
	private RequestParserHandler handler;	
	
	
	//TODO: write integration-test for this class
	public MainRequestHandler(Deserializer deserializer, RequestFilter filter, RequestParserHandler handler) {
		this.deserializer = deserializer;
		this.filter = filter;
		this.handler = handler;
	}
	
	/**
	 * @see org.beepcore.beep.core.RequestHandler#receiveMSG(org.beepcore.beep.core.MessageMSG)
	 */
	public void receiveMSG(MessageMSG message) {
		LOG.debug("--> receiveMSG");

		InputDataStream input = message.getDataStream();
		
		try {
			byte[] rawData = readData(input);
			LOG.debug("received "+rawData.length+" bytes. ["+(new String(rawData))+"]");
			if (rawData.length == PIGGYBACKED_MESSAGE_LENGTH) {
				handlePiggybackedMessage(message);
			} else {
				Request request = null;
				//TODO: use SingleThreadedDomain instead of synchronized
				synchronized (this) {
					deserializer.deserialize(rawData, handler);
					request = handler.getResult();
				}
				//TODO: the following code must not be synchronized right?
				String userid = request.getUserId();
				DiscoveryManager discoveryManager = DiscoveryManagerFactory.getDiscoveryManager(null);
				if (!discoveryManager.hasSessionEstablished(userid)) {
					RemoteUserProxyExt user = discoveryManager.getUser(userid);
					LOG.debug("create RemoteUserSession for ["+user.getMutableUserDetails().getUsername()+"]");
					SessionManager manager = SessionManager.getInstance();
					Channel mainChannel = message.getChannel();
					manager.createSession(user, (TCPSession) mainChannel.getSession(), mainChannel);
				}
				
				request.setMessage(message);
				filter.process(request);
			}
		} catch (Exception e) {
			e.printStackTrace();
			LOG.error("could not process request ["+e+"]");
		}
		LOG.debug("<-- receiveMSG");
	}
	

	public void cleanup() {
		//TODO: consider a thorough and meaningful cleanup
		throw new UnsupportedOperationException();
	}
	
	protected Logger getLogger() {
		return LOG;
	}
}
