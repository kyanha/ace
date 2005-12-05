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

import org.apache.log4j.Logger;
import org.beepcore.beep.core.RequestHandler;

import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.SingleThreadDomain;
import ch.iserver.ace.util.ThreadDomain;

/**
 *
 */
public class SessionRequestHandlerFactory {

	private Logger LOG = Logger.getLogger(SessionRequestHandlerFactory.class);
	
	private Deserializer deserializer;
	private ParserHandler handler;
	
	private ThreadDomain domain = new SingleThreadDomain();
	
	private static SessionRequestHandlerFactory instance;
	
	public static void init(Deserializer deserializer, ParserHandler handler) {
		ParameterValidator.notNull("deserializer", deserializer);
		ParameterValidator.notNull("handler", handler);
		if (instance == null) {
			instance = new SessionRequestHandlerFactory(deserializer, handler);
		}
	}
	
	private SessionRequestHandlerFactory(Deserializer deserializer, ParserHandler handler) {
		this.deserializer = deserializer;
		this.handler = handler;
	}
	
	public static SessionRequestHandlerFactory getInstance() {
		if (instance == null) {
			throw new IllegalStateException("instance has not been initialized");
		}
		return instance;
	}
	
	public RequestHandler createHandler() {
		return (RequestHandler) domain.wrap(new SessionRequestHandler(deserializer, handler), RequestHandler.class);
	}
	
}
