/*
 * $Id:QueryListener.java 1095 2005-11-09 13:56:51Z zbinl $
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
import org.beepcore.beep.core.AbortChannelException;
import org.beepcore.beep.core.InputDataStream;
import org.beepcore.beep.core.Message;
import org.beepcore.beep.core.MessageMSG;
import org.beepcore.beep.core.ReplyListener;
import org.beepcore.beep.util.BufferSegment;

import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class ResponseListener implements ReplyListener {

	private static Logger LOG = Logger.getLogger(ResponseListener.class);

//	private DocumentDiscoveryCallback callback;
	private Deserializer deserializer;
	private ResponseParserHandler handler;
	private RequestFilter filter;
	
	private static ResponseListener instance;
	
//	public ResponseListener(DocumentDiscoveryCallback callback, Deserializer deserializer) {
//		ParameterValidator.notNull("callback", callback);
//		ParameterValidator.notNull("deserializer", deserializer);
//		this.callback = callback;
//		this.deserializer = deserializer;
//		handler = new ResponseParserHandler();
//	}
	
	private ResponseListener() {
		handler = new ResponseParserHandler();
	}
	
	public static ResponseListener getInstance() {
		if (instance == null) {
			instance = new ResponseListener();
		}
		return instance;
	}
	
	public void init(Deserializer deserializer, RequestFilter filter) {
		ParameterValidator.notNull("deserializer", deserializer);
		ParameterValidator.notNull("filter", filter);
		this.deserializer = deserializer;
		this.filter = filter;
	}
	
	/**
	 * 
	 * @see org.beepcore.beep.core.ReplyListener#receiveRPY(org.beepcore.beep.core.Message)
	 */
	public void receiveRPY(Message message) throws AbortChannelException {
		byte[] data = read(message);
		try {
			deserializer.deserialize(data, handler);
			Request request = (Request)handler.getResult();
			if (message.getMessageType() == Message.MESSAGE_TYPE_MSG) {
				request.setMessage((MessageMSG)message);
			} else {
				LOG.warn("message not set in request, type is ["+message.getMessageType()+"]");
			}
			filter.process(request);
		} catch (DeserializeException de) {
			//TODO: handling
			LOG.error("could not deserialize ["+de.getMessage()+"]");
		}
//			callback.documentsDiscovered(info.getId(), result);
	}


	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.ReplyListener#receiveERR(org.beepcore.beep.core.Message)
	 */
	public void receiveERR(Message message) throws AbortChannelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.ReplyListener#receiveANS(org.beepcore.beep.core.Message)
	 */
	public void receiveANS(Message message) throws AbortChannelException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.beepcore.beep.core.ReplyListener#receiveNUL(org.beepcore.beep.core.Message)
	 */
	public void receiveNUL(Message message) throws AbortChannelException {
		Object appData = message.getChannel().getAppData();
		LOG.debug("received NUL message for ["+appData+"] from ["+message.toString()+"]");
	}
	
	private byte[] read(Message message) throws AbortChannelException {
		InputDataStream input = message.getDataStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		do {
			try {
//				System.out.println("waitForNextSegment");
				BufferSegment b = input.waitForNextSegment();
//				System.out.println("ok " + b.getLength());
				if (b == null) {
					out.flush();
					break;
				}
				out.write(b.getData());
			} catch (Exception e) {
				//message.getChannel().getSession().terminate(e.getMessage());
				throw new AbortChannelException(e.getMessage());
			}
		} while (!input.isComplete());

//		System.out.println("S: read " + out.size() + " bytes from input.");
		return out.toByteArray();
	}

}
