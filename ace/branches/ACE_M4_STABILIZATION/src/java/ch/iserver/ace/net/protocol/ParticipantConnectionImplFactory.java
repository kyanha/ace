/*
 * $Id:ParticipantConnectionImplFactory.java 2413 2005-12-09 13:20:12Z zbinl $
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

import ch.iserver.ace.net.protocol.filter.RequestFilter;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Factory class to create instances of <code>ParticipantConnectionImpl</code>.
 * The factory must be initialized with a call to {@link #init(RequestFilter)}
 * before it can be used.
 *
 */
public class ParticipantConnectionImplFactory {

	/**
	 * the filter chain to be given to the factory
	 * in order to use it
	 */
	private RequestFilter filter;
	
	/**
	 * the singleton instance
	 */
	private static ParticipantConnectionImplFactory instance;
	
	/**
	 * Private constructor.
	 * 
	 * @param filter		the request filter chain
	 */
	private ParticipantConnectionImplFactory(RequestFilter filter) {
		this.filter = filter;
	}
	
	/**
	 * Initializes this factory. 
	 * This method must be called prior to method {@link #getInstance()}.
	 * 
	 * @param mainHandler		the main handler
	 * @param deserializer	the deserializer implementation
	 * @param handler		the parser handler
	 */
	public static void init(RequestFilter filter) {
		ParameterValidator.notNull("RequestFilter", filter);
		instance = new ParticipantConnectionImplFactory(filter);
	}
	
	/**
	 * Gets the ParticipantConnectionImplFactory object.
	 * If the factory was not initialized properly, 
	 * an <code>IllegalStateException</code> is thrown.
	 *  
	 * @return the ParticipantConnectionImplFactory instance
	 * @throws IllegalStateException 	if the factory was not initialized properly
	 */
	public static ParticipantConnectionImplFactory getInstance() {
		if (instance == null) {
			throw new IllegalStateException("instance has not been initialized");
		}
		return instance;
	}
	
	/**
	 * Creates a new ParticipantConnectionImpl.
	 * 
	 * @param docId			the document id
	 * @param session		the remote user session
	 * @param listener		the response listener
	 * @param serializer		the serializer
	 * @return	a new ParticipantConnectionImpl
	 */
	public ParticipantConnectionImpl createConnection(String docId, 
			RemoteUserSession session, ResponseListener listener, Serializer serializer) {
		return new ParticipantConnectionImpl(docId, session, listener, serializer, filter);
	}
	
	
}
