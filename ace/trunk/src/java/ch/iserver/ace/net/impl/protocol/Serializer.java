/*
 * $Id:Serializer.java 1095 2005-11-09 13:56:51Z zbinl $
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

/**
 *
 */
public interface Serializer { 
	
	public byte[] createQuery(int type) throws SerializeException;
	
	public byte[] createRequest(int type, Object data) throws SerializeException;
	
	public byte[] createResponse(int type, Object data1, Object data2) throws SerializeException;
	
	public byte[] createNotification(int type, Object data) throws SerializeException;
	
	public byte[] createSessionMessage(int type, Object data1, Object data2) throws SerializeException;
	
}
