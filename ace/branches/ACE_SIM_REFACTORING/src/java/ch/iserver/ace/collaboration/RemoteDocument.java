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

/**
 * The RemoteDocument interface provides methods applicable to
 * remote documents that were discovered, most notable is the join method
 * which allows to send a join request to the document.
 */
public interface RemoteDocument {
	
	/**
	 * Gets the title of the document. The title should be used in the user
	 * interface wherever a title for the document is necessary.
	 */
	String getTitle();
	
	/**
	 * Joins this remote document.
	 * 
	 * @param controller the document controller for the document
	 * @return the Session object
	 */
	Session join(DocumentController controller);

}
