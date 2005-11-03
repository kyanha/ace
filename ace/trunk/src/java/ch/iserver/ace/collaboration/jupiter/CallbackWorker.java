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

package ch.iserver.ace.collaboration.jupiter;

import ch.iserver.ace.collaboration.PublishedSessionCallback;
import ch.iserver.ace.util.ParameterValidator;
import ch.iserver.ace.util.Worker;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;

/**
 * Worker thread that is used to decouple the server part of a published
 * document from the local client part.
 */
class CallbackWorker extends Worker {
	
	/**
	 * The PublishedSessionCallback that receives the result of the executed
	 * Command objects.
	 */
	private final PublishedSessionCallback callback;
	
	/**
	 * The BlockingQueue from which the Command objects are retrieved.
	 */
	private final BlockingQueue queue;
	
	/**
	 * Creates a new CallbackWorker instance that uses the given BlockingQueue
	 * to receive Command objects and the callback to receive the results.
	 * 
	 * @param callback the PublishedSessionCallback to receive the results
	 * @param queue the BlockingQueue from which to get the Command objects
	 */
	CallbackWorker(PublishedSessionCallback callback, BlockingQueue queue) {
		super("CallbackWorker");
		ParameterValidator.notNull("callback", callback);
		ParameterValidator.notNull("queue", queue);
		this.callback = callback;
		this.queue = queue;
	}
	
	/**
	 * @return the PublishedSessionCallback which receives the results
	 */
	private PublishedSessionCallback getCallback() {
		return callback;
	}
	
	/**
	 * @return the BlockingQueue from which to read the Command objects
	 */
	private BlockingQueue getQueue() {
		return queue;
	}
	
	/**
	 * @see ch.iserver.ace.util.Worker#doWork()
	 */
	protected void doWork() throws InterruptedException {
		Command command = (Command) getQueue().take();
		command.execute(getCallback());
	}
	
}
