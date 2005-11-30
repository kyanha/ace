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

package ch.iserver.ace.collaboration;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.DocumentServer;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.InvitationPort;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class DocumentServerStub implements DocumentServer {
	
	private int shutdownCnt;
	
	private int prepareShutdownCnt;
	
	private int setDocumentDetailsCnt;
	
	private DocumentServerLogic logic;
	
	public DocumentServerStub(DocumentServerLogic logic) {
		ParameterValidator.notNull("logic", logic);
		this.logic = logic;
	}
	
	public DocumentServerLogic getLogic() {
		return logic;
	}
	
	public int getPrepareShutdownCnt() {
		return prepareShutdownCnt;
	}
	
	public int getShutdownCnt() {
		return shutdownCnt;
	}
	
	public int getSetDocumentDetailsCnt() {
		return setDocumentDetailsCnt;
	}
	
	public void invite(InvitationPort port) {
		
	}
	
	public void prepareShutdown() {
		prepareShutdownCnt++;
	}
	
	public void shutdown() {
		shutdownCnt++;
	}
	
	public void setDocumentDetails(DocumentDetails details) {
		ParameterValidator.notNull("details", details);
		setDocumentDetailsCnt++;
	}
	
}
