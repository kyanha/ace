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

import java.beans.PropertyChangeListener;


/**
 * The RemoteDocument interface provides methods applicable to
 * remote documents that were discovered, most notable is the join method
 * which allows to send a join request to the document.
 */
public interface RemoteDocument {
	
	/**
	 * The name of the title property. 
	 */
	String TITLE_PROPERTY = "title";

	/**
	 * Gets the unique identifier of the document.
	 * 
	 * @return the unique identifier of the document
	 */
	String getId();
	
	String getTitle();
	
	/**
	 * Gets the RemoteUser object for the publisher of the document.
	 * 
	 * @return the RemoteUser for the publisher
	 */
	RemoteUser getPublisher();
	
	/**
	 * Joins this remote document.
	 * 
	 * @param callback the JoinCallback for join related callbacks
	 */
	void join(JoinCallback callback);

	/**
	 * @param listener
	 */
	void addPropertyChangeListener(PropertyChangeListener listener);
	
	/**
	 * @param listener
	 */
	void removePropertyChangeListener(PropertyChangeListener listener);

}
