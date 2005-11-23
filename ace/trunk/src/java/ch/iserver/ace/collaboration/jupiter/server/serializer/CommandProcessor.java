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

package ch.iserver.ace.collaboration.jupiter.server.serializer;

/**
 * Processor for SerializerCommand objects.
 */
public interface CommandProcessor {
	
	/**
	 * Starts the processor.
	 */
	void startProcessor();
	
	/**
	 * Processes the given command. Note, it is an error to call this
	 * method without prior call to startProcessor.
	 * 
	 * @param command the command to be processed
	 * @throws IllegalStateException if the processor is not started
	 */
	void process(SerializerCommand command);
	
	/**
	 * Stops the processor.
	 */
	void stopProcessor();
	
}
