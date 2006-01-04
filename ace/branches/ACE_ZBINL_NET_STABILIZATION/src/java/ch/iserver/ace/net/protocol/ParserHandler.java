/*
 * $Id:ParserHandler.java 1095 2005-11-09 13:56:51Z zbinl $
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

import org.xml.sax.helpers.DefaultHandler;


/**
 * Abstract super class for all request handlers. 
 * Defines one important method {@link #getResult()}
 * to retrieve the Request object after deserialization 
 * stopped.
 *
 */
public abstract class ParserHandler extends DefaultHandler implements ProtocolConstants {

	/**
	 * Gets the result of the last parsing as a <code>Request</code>.
	 * 
	 * @return the parsed Request 
	 */
	public abstract Request getResult();
	
	/**
	 * Sets the meta data to create the result.
	 * 
	 * @param metadata the metadata to create the result
	 */
	public abstract void setMetaData(Object metadata);
	
}
