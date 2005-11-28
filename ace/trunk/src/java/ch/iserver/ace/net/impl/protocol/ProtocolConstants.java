/*
 * $Id:ProtocolConstants.java 1205 2005-11-14 07:57:10Z zbinl $
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
	
	/*********************************/
	/** constants for serialization **/
	/*********************************/
	public static final int PUBLISHED_DOCUMENTS = 0;
	public static final int PUBLISH = 1;
	public static final int CONCEAL = 2;
	public static final int SEND_DOCUMENTS = 3;
	public static final int DOCUMENT_DETAILS_CHANGED = 4;
	public static final int JOIN = 5;
	public static final int JOIN_DOCUMENT = 6;
	public static final int LEAVE = 7;
	public static final int REQUEST = 8;
	public static final int KICKED = 9;
	public static final int INVITE = 10;
	
	/********************/
	/** Top level tags **/
	/********************/
	public static final String TAG_QUERY = "query";
	public static final String TAG_PUBLISHED_DOCS = "publishedDocs";
	public static final String TAG_PUBLISH = "publishDocs";
	public static final String TAG_CONCEAL = "concealDocs";
	public static final String TAG_JOIN = "join";
	public static final String TAG_JOIN_DOCUMENT = "document";
	public static final String TAG_DOCUMENT_DETAILS_CHANGED = "docDetailsChanged";
	public static final String TAG_DOC = "doc";
	public static final String TAG_LEAVE = "leave";
	public static final String TAG_KICKED = "kicked";
	public static final String TAG_INVITE = "invite";
	
	/***********************************/
	/** Sub-level tags 				**/
	/***********************************/
	public static final String QUERY_TYPE= "type";
	public static final String QUERY_TYPE_PUBLISHED_DOCUMENTS = "docs";
	
	public static final String NAME = "name";
	public static final String DOCUMENT_ID = "id";
	public static final String USER_ID = "userid";
	
	public static final String PARTICIPANTS = "participants";
	public static final String PARTICIPANT = "participant";
	public static final String PARTICIPANT_ID = "participantId";
	public static final String DOC_ID = "docId";
	public static final String ID = "id";
	public static final String USER = "user";
	public static final String ADDRESS = "address";
	public static final String PORT = "port";
	public static final String EXPLICIT_DISCOVERY = "explicitDiscovery";
	public static final String DATA = "data";
	public static final String SELECTION = "selection";
	public static final String MARK = "mark";
	public static final String DOT = "dot";

	
}
