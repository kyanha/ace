/*
 * $Id:RequestImpl.java 1205 2005-11-14 07:57:10Z zbinl $
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

import org.beepcore.beep.core.MessageMSG;

/**
 *
 */
public class RequestImpl implements Request {

	private int type;
	private Object payload;
	private String userid;
	private MessageMSG message;
	
	public RequestImpl(int type, String userid, Object payload) {
		this.type = type;
		this.userid = userid;
		this.payload = payload;
	}

	public int getType() {
		return type;
	}
	
	public Object getPayload() {
		return payload;
	}

	public void setMessage(MessageMSG message) {
		this.message = message;
	}

	public MessageMSG getMessage() {
		return message;
	}
	

	public String getUserId() {
		return userid;
	}
	
	public String toString() {
		return "RequestImpl(" + type + ", " + userid +")";
	}
	
	/**
	 * Helper class to wrap document specific information in the
	 * Request.
	 *
	 * @see Request
	 */
	static class DocumentInfo {
		//TODO: user id can be removed, since we have it in the Request
		private String docId, name, userId;
		private int participantId;
		private String data;
		
		public DocumentInfo(String docId, String name, String userId) {
			this.docId = docId;
			this.name = name;
			this.userId = userId;
			participantId = -1;
		}
		
		public DocumentInfo(String docId, int participantId) {
			this.docId = docId;
			this.participantId = participantId;
		}
		
		public int getParticipantId() {
			return participantId;
		}
		
		public void setParticipantId(int id) {
			this.participantId = id;
		}
		
		public void setData(String data) {
			this.data = data;
		}
		
		public String getData() {
			return data;
		}
		
		public String getDocId() {
			return docId;
		}

		public String getName() {
			return name;
		}

		public String getUserId() {
			return userId;
		}
		
		public String toString() {
			return "DocumentInfo("+docId+", '"+name+"', "+userId+")";
		}
	}
	
}
