/*
 * $Id:Connection.java 1095 2005-11-09 13:56:51Z zbinl $
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

import org.beepcore.beep.core.BEEPException;
import org.beepcore.beep.core.Channel;
import org.beepcore.beep.core.OutputDataStream;
import org.beepcore.beep.core.ReplyListener;
import org.beepcore.beep.util.BufferSegment;

import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.net.PortableDocument;
import ch.iserver.ace.net.RemoteUserProxy;

/**
 * Wrapper/Decorator class around a <code>Channel</code>.
 * 
 * @see org.beepcore.beep.core.Channel
 */
public class ParticipantConnectionImpl implements ParticipantConnectionExt {

	private Channel channel;
	
	public ParticipantConnectionImpl(Channel channel) {
		this.channel = channel;
	}
	
	public void send(byte[] message, Object data, ReplyListener listener) throws ProtocolException {
		try {
			OutputDataStream output = prepare(message);
			//AppData is only kept in-process
			channel.setAppData(data);
			//TODO: make shure that sendMSG does not block
			channel.sendMSG(output, listener);
		} catch (BEEPException be) {
			throw new ProtocolException(be.getMessage());
		}
	}
	
	private OutputDataStream prepare(byte[] data) {
		BufferSegment buffer = new BufferSegment(data);
		OutputDataStream output = new OutputDataStream();
		output.add(buffer);
		output.setComplete();
		return output;
	}

	
	/***************************************************/
	/** methods from interface Participantconnection  **/
	/***************************************************/
	public void setParticipantId(int participantId) {
		// TODO Auto-generated method stub
		
	}

	public RemoteUserProxy getUser() {
		// TODO Auto-generated method stub
		return null;
	}

	public void sendDocument(PortableDocument document) {
		// TODO Auto-generated method stub
		
	}

	public void sendRequest(int participantId, Request request) {
		// TODO Auto-generated method stub
		
	}

	public void sendCaretUpdateMessage(int participantId, CaretUpdateMessage message) {
		// TODO Auto-generated method stub
		
	}

	public void sendParticipantJoined(int participantId, RemoteUserProxy proxy) {
		// TODO Auto-generated method stub
		
	}

	public void sendParticipantLeft(int participantId, int reason) {
		// TODO Auto-generated method stub
		
	}

	public void sendKicked() {
		// TODO Auto-generated method stub
		
	}

	public void close() {
		//TODO: consider if on session shutdown it is more appropriate to 
		//notify the participant on close() invocation or on DocumentServer.shutdown()
		//invocation
	}
	
}
