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

/**
 *
 */
public interface ProtocolConstants {

	public static final String PROFILE_URI = "http://ace.iserver.ch/profiles/ACE";
	//TODO: get port from config file
	public static final int LISTENING_PORT = 41234;
	
	/** constants for serialization **/
	public static final int PUBLISHED_DOCUMENTS = 0;
	public static final int PUBLISH = 1;
	public static final int CONCEAL = 2;
	
	public static final String[] NAMES = new String[] { "docs" };
	
	
	/** constants for deserialization **/
	public static final String QUERY_TYPE= "type";
	public static final String QUERY_TYPE_PUBLISHED_DOCUMENTS = "docs";
	
	public static final String RESPONSE_PUBLISHED_DOCUMENTS = "publishedDocs";
	public static final String DOCUMENT_NAME = "name";
	public static final String DOCUMENT_ID = "id";
	public static final String USER_ID = "userid";
	
	/** Top level tags **/
	public static final String TAG_QUERY = "query";
	public static final String TAG_PUBLISH = "publishDocs";
	public static final String TAG_CONCEAL = "concealDocs";
	public static final String TAG_DOC = "doc";
	
}
