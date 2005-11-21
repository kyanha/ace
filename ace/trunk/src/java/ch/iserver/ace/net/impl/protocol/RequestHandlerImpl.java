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

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.RequestHandler;
import org.beepcore.beep.transport.tcp.TCPSession;
import org.beepcore.beep.util.BufferSegment;

import ch.iserver.ace.net.impl.RemoteUserProxyExt;
import ch.iserver.ace.net.impl.discovery.DiscoveryManager;
import ch.iserver.ace.net.impl.discovery.DiscoveryManagerFactory;

/**
 *
 */
public class RequestHandlerImpl implements RequestHandler {

	private static Logger LOG = Logger.getLogger(RequestHandlerImpl.class);
	
	private static Object MUTEX = new Object();
	
	private RequestFilter filter;
	private Deserializer deserializer;
	private RequestParserHandler handler;	
	
	
	//TODO: write integration-test for this class
	public RequestHandlerImpl(Deserializer deserializer, RequestFilter filter) {
		this.deserializer = deserializer;
		this.filter = filter;
		handler = new RequestParserHandler();
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
			Request request = null;
			//TODO: use SingleThreadedDomain instead of synchronized
			synchronized (MUTEX) {
				deserializer.deserialize(rawData, handler);
				request = handler.getResult();
				String userid = request.getUserId();
				DiscoveryManager discoveryManager = DiscoveryManagerFactory.getDiscoveryManager(null);
				if (!discoveryManager.hasSessionEstablished(userid)) {
					RemoteUserProxyExt user = discoveryManager.getUser(userid);
					LOG.debug("create new session for ["+user.getMutableUserDetails().getUsername()+"]");
					SessionManager manager = SessionManager.getInstance();
					manager.createSession(user, (TCPSession) message.getChannel().getSession());
				}
			}
			request.setMessage(message);
			filter.process(request);
		} catch (Exception e) {
			LOG.error("could not process request ["+e+"]");
		}
		LOG.debug("<-- receiveMSG");
	}

	private byte[] readData(InputDataStream stream) throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		do {
            	BufferSegment b = stream.waitForNextSegment();
             if (b == null) {
             	out.flush();
                 break;
             }
             out.write(b.getData());
        } while (!stream.isComplete());
		
		return out.toByteArray();
	}
	
}
