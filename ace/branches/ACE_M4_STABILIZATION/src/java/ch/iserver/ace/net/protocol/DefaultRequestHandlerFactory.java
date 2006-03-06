/*
 * $Id:DefaultRequestHandlerFactory.java 2413 2005-12-09 13:20:12Z zbinl $
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

import org.beepcore.beep.core.RequestHandler;

import ch.iserver.ace.util.ParameterValidator;

/**
 * Factory to create instances of <code>DefaultRequestHandler</code>.
 * 
 * @see DefaultRequestHandler
 */
public class DefaultRequestHandlerFactory {

	/**
	 * the main handler
	 */
	private RequestHandler mainHandler;
	
	/**
	 * the deserializer
	 */
	private Deserializer deserializer;
	
	/**
	 * the parser handler
	 */
	private ParserHandler handler;
	
	
	/**
	 * the singleton factory instance
	 */
	private static DefaultRequestHandlerFactory instance;
	
	
	/**
	 * Private constructor.
	 * 
	 * @param mainHandler		the main handler
	 * @param deserializer	the deserializer
	 * @param handler		the parser handler
	 */
	private DefaultRequestHandlerFactory(RequestHandler mainHandler, Deserializer deserializer, ParserHandler handler) {
		this.mainHandler = mainHandler;
		this.deserializer = deserializer;
		this.handler = handler;
	}
	
	/**
	 * Initializes this factory. 
	 * This method must be called prior to method {@link #getInstance()}.
	 * 
	 * @param mainHandler		the main handler
	 * @param deserializer	the deserializer implementation
	 * @param handler		the parser handler
	 */
	public static void init(RequestHandler mainHandler, Deserializer deserializer, ParserHandler handler) {
		ParameterValidator.notNull("mainHandler", mainHandler);
		ParameterValidator.notNull("deserializer", deserializer);
		ParameterValidator.notNull("ParserHandler", handler);
		instance = new DefaultRequestHandlerFactory(mainHandler, deserializer, handler);
	}
	
	/**
	 * Gets the DefaultRequestHandlerFactory object.
	 * If the factory was not initialized properly, 
	 * an <code>IllegalStateException</code> is thrown.
	 *  
	 * @return the DefaultRequestHandlerFactory instance
	 * @throws IllegalStateException 	if the factory was not initialized properly
	 */
	public static DefaultRequestHandlerFactory getInstance() {
		if (instance == null) {
			throw new IllegalStateException("instance has not been initialized");
		}
		return instance;
	}
	
	/**
	 * Creates a new <code>DefaultRequestHandler</code>.
	 * 
	 * @return <code>a new DefaultRequestHandler</code>
	 */
	public DefaultRequestHandler createHandler() {
		return new DefaultRequestHandler(mainHandler, deserializer, handler);
	}
}
