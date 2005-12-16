/*
 * $Id:SessionRequestHandlerFactory.java 2413 2005-12-09 13:20:12Z zbinl $
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

import org.apache.log4j.Logger;
import org.beepcore.beep.core.RequestHandler;

import ch.iserver.ace.net.core.NetworkServiceExt;
import ch.iserver.ace.net.protocol.RequestImpl.DocumentInfo;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SingleThreadDomain;
import ch.iserver.ace.util.ThreadDomain;

/**
 * Factory class to create instances of 
 * {@link ch.iserver.ace.net.protocol.SessionRequestHandler}.
 */
public class SessionRequestHandlerFactory {

	private Logger LOG = Logger.getLogger(SessionRequestHandlerFactory.class);
	
	private Deserializer deserializer;
	private ParserHandler handler;
	private NetworkServiceExt service;
	
	private ThreadDomain domain = new SingleThreadDomain();
	
	private static SessionRequestHandlerFactory instance;
	
	/**
	 * Initializes this factory.
	 *  
	 * @param deserializer
	 * @param handler
	 * @param service
	 */
	public static void init(Deserializer deserializer, ParserHandler handler, NetworkServiceExt service) {
		ParameterValidator.notNull("deserializer", deserializer);
		ParameterValidator.notNull("handler", handler);
		if (instance == null) {
			instance = new SessionRequestHandlerFactory(deserializer, handler, service);
		}
	}
	
	/**
	 * Private constructor.
	 * 
	 * @param deserializer
	 * @param handler
	 * @param service
	 */
	private SessionRequestHandlerFactory(Deserializer deserializer, ParserHandler handler, NetworkServiceExt service) {
		this.deserializer = deserializer;
		this.handler = handler;
		this.service = service;
	}
	
	/**
	 * Gets the SessionRequestHandlerFactory instance.
	 * 
	 * @return the SessionRequestHandlerFactory instance
	 */
	public static SessionRequestHandlerFactory getInstance() {
		if (instance == null) {
			throw new IllegalStateException("instance has not been initialized");
		}
		return instance;
	}
	
	/**
	 * Creates a new SessionRequestHandler.
	 * 
	 * @param info
	 * @return	the created SessionRequestHandler
	 */
	public RequestHandler createHandler(DocumentInfo info) {
		return (RequestHandler) domain.wrap(new SessionRequestHandler(deserializer, handler, service, info), RequestHandler.class);
	}
	
}
