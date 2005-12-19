/*
 * $Id:Request.java 1205 2005-11-14 07:57:10Z zbinl $
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

import org.beepcore.beep.core.MessageMSG;

/**
 * The interface <code>Request</code> is used for message
 * processing, i.e. incoming and outgoing messages. A Request
 * is created by the client and then passed to the <code>RequestFilter</code>
 * chain which does the actual processing of the Request.
 * 
 * @see ch.iserver.ace.net.protocol.filter.RequestFilter
 */
public interface Request {

	/**
	 * Gets the type of this Request.
	 * 
	 * @return the type of this request
	 * @see ProtocolConstants
	 */
	public int getType();
	
	/**
	 * Gets the userid of the user which is either 
	 * the sender or the recipient.
	 * 
	 * @return the userid
	 */
	public String getUserId();
	
	/**
	 * Gets the payload of the Request.
	 * 
	 * @return the payload
	 */
	public Object getPayload();
	
	/**
	 * Sets the BEEP Core specific MessageMSG.
	 * 
	 * @param message	the MessageMSG to set
	 */
	public void setMessage(MessageMSG message);
	
	/**
	 * Gets the MessageMSG from which this Request was retrieved.
	 * Only available if the request is an incoming request from 
	 * the network.
	 * 
	 * @return the MessageMSG or null if the Request is outgoing to the network
	 */
	public MessageMSG getMessage();
	
}
