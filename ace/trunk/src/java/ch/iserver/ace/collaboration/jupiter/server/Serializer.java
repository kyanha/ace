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

import ch.iserver.ace.util.BlockingQueue;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;

/**
 *
 */
class Serializer extends Worker {
	
	private final BlockingQueue queue;
	
	private final Lock lock;
	
	private final Forwarder forwarder;
	
	public Serializer(BlockingQueue queue, Lock lock, Forwarder forwarder) {
		super("Serializer");
		ParameterValidator.notNull("queue", queue);
		ParameterValidator.notNull("lock", lock);
		ParameterValidator.notNull("forwarder", forwarder);
		this.queue = queue;
		this.lock = lock;
		this.forwarder = forwarder;
	}

	protected Forwarder getForwarder() {
		return forwarder;
	}
	
	protected Lock getLock() {
		return lock;
	}
	
	protected void doWork() throws InterruptedException {
		try {
			SerializerCommand cmd = (SerializerCommand) queue.get();
			getLock().lock();
			cmd.execute(getForwarder());
		} finally {
			getLock().unlock();
		}
	}
		
}
