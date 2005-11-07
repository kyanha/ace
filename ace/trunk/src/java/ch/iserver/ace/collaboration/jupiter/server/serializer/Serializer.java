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

import org.apache.log4j.Logger;

import ch.iserver.ace.collaboration.jupiter.server.FailureHandler;
import ch.iserver.ace.collaboration.jupiter.server.Forwarder;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.Worker;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;

/**
 * Worker thread that serializes processing of incoming requests. This is
 * a fundamental requirement of the Jupiter algorithm server-side
 * component.
 */
public class Serializer extends Worker {
	
	private static final Logger LOG = Logger.getLogger(Serializer.class);
	
	/**
	 * The BlockingQueue from which to retrieve commands.
	 */
	private final BlockingQueue queue;
	
	/**
	 * The Lock that guards the transformation on the server.
	 */
	private final Lock lock;
	
	/**
	 * The Forwarder that forwards the results of the transformation to
	 * all other participants.
	 */
	private final Forwarder forwarder;
	
	/**
	 * 
	 */
	private final FailureHandler failureHandler;
	
	/**
	 * Creates a new Serializer worker thread.
	 * 
	 * @param queue the queue from which to retrieve commands
	 * @param lock the lock that guards the critical sections
	 * @param forwarder the forwarder that forwards the results
	 * @param failureHandler the FailureHandler to be notified about failures
	 */
	public Serializer(BlockingQueue queue, Lock lock, Forwarder forwarder, FailureHandler failureHandler) {
		super("Serializer");
		ParameterValidator.notNull("queue", queue);
		ParameterValidator.notNull("lock", lock);
		ParameterValidator.notNull("forwarder", forwarder);
		this.queue = queue;
		this.lock = lock;
		this.forwarder = forwarder;
		this.failureHandler = failureHandler;
	}

	/**
	 * @return the forwarder that forwards the results
	 */
	protected Forwarder getForwarder() {
		return forwarder;
	}
	
	/**
	 * @return the lock that guards the critical sections
	 */
	protected Lock getLock() {
		return lock;
	}
	
	protected FailureHandler getFailureHandler() {
		return failureHandler;
	}
	
	/**
	 * @see ch.iserver.ace.util.Worker#doWork()
	 */
	protected void doWork() throws InterruptedException {
		SerializerCommand cmd = (SerializerCommand) queue.take();
		LOG.info("SERIALIZER: serializing next command ...");
		getLock().lock();
		try {
			cmd.execute(getForwarder());
			LOG.info("SERIALIZER: command executed ...");
		} catch (SerializerException e) {
			getFailureHandler().handleFailure(e.getParticipantId());
		} finally {
			getLock().unlock();
		}
	}
		
}
