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

package ch.iserver.ace.collaboration.jupiter.server;

/**
 * SerializerCommands are executed by the Serializer. The Serializer takes
 * care of proper locking/unlocking of the transformation engine. The 
 * commands are responsible to forward the result of their execution to
 * the Forwarder.
 */
interface SerializerCommand {
	
	/**
	 * Executes the command. The passed in forwarder is used to forward
	 * the result to all the other participants.
	 * 
	 * @param forwarder the Forwarder to receive the results
	 */
	void execute(ParticipantProxy forwarder);
	
}
