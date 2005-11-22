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

import ch.iserver.ace.collaboration.jupiter.server.Forwarder;
import ch.iserver.ace.collaboration.jupiter.server.serializer.CommandProcessor;
import ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand;
import ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerException;

/**
 *
 */
public class SimpleCommandProcessor implements CommandProcessor {
	
	private final Forwarder forwarder;
	
	public SimpleCommandProcessor(Forwarder forwarder) {
		this.forwarder = forwarder;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.serializer.CommandProcessor#startProcessor()
	 */
	public void startProcessor() {
		
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.serializer.CommandProcessor#process(ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand)
	 */
	public void process(SerializerCommand command) {
		try {
			command.execute(forwarder);
		} catch (SerializerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.serializer.CommandProcessor#stopProcessor()
	 */
	public void stopProcessor() {
		
	}

}
