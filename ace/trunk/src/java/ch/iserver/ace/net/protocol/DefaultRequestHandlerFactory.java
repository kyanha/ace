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

package ch.iserver.ace.net.protocol;

import org.beepcore.beep.core.RequestHandler;

import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.net.core.RemoteUserProxyImpl;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
public class DefaultRequestHandlerFactory {

	private RequestHandler mainHandler;
	private Deserializer deserializer;
	private ParserHandler handler;
	
	private static DefaultRequestHandlerFactory instance;
	
	private DefaultRequestHandlerFactory(RequestHandler mainHandler, Deserializer deserializer, ParserHandler handler) {
		this.mainHandler = mainHandler;
		this.deserializer = deserializer;
		this.handler = handler;
	}
	
	public static void init(RequestHandler mainHandler, Deserializer deserializer, ParserHandler handler) {
		ParameterValidator.notNull("mainHandler", mainHandler);
		ParameterValidator.notNull("deserializer", deserializer);
		ParameterValidator.notNull("ParserHandler", handler);
		instance = new DefaultRequestHandlerFactory(mainHandler, deserializer, handler);
	}
	
	public static DefaultRequestHandlerFactory getInstance() {
		if (instance == null) {
			throw new IllegalStateException("instance has not been initialized");
		}
		return instance;
	}
	
	public DefaultRequestHandler createHandler() {
		return new DefaultRequestHandler(mainHandler, deserializer, handler);
	}
}
