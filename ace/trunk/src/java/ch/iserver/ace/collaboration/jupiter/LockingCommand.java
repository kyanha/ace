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
import ch.iserver.ace.util.InterruptedRuntimeException;
import ch.iserver.ace.util.Lock;
import ch.iserver.ace.util.ParameterValidator;

/**
 * Abstract base class for all command implementations that must lock a passed
 * in lock before the real work is done. Subclasses must implement the abstract
 * {@link #doWork(PublishedSessionCallback)} method. The 
 * {@link #execute(PublishedSessionCallback)} method takes care of proper
 * locking/unlocking of the lock even in case of exceptions.
 */
abstract class LockingCommand implements Command {
	
	/**
	 * The algorithm wrapper to be used inside the doWork method.
	 */
	private final AlgorithmWrapper algorithm;
	
	/**
	 * The lock used by the LockingCommand to lock/unlock.
	 */
	private final Lock lock;
	
	/**
	 * Creates a new LockingCommand using the given lock and
	 * algorithm wrapper.
	 * 
	 * @param lock the Lock object
	 * @param algorithm the AlgorithmWrapper instance
	 */
	protected LockingCommand(Lock lock, AlgorithmWrapper algorithm) {
		ParameterValidator.notNull("lock", lock);
		ParameterValidator.notNull("algorithm", algorithm);
		this.lock = lock;
		this.algorithm = algorithm;
	}
	
	/**
	 * @return the AlgorithmWrapper to be used inside the locked section
	 */
	protected AlgorithmWrapper getAlgorithm() {
		return algorithm;
	}
		
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.Command#execute(ch.iserver.ace.collaboration.PublishedSessionCallback)
	 */
	public final void execute(PublishedSessionCallback callback) {
		try {
			lock.lock();
			try {
				doWork(callback);
			} finally {
				lock.unlock();
			}
		} catch (InterruptedException e) {
			throw new InterruptedRuntimeException("locking interrupted", e);
		}
	}
	
	/**
	 * Does the real work of the command. Subclasses implement this method and
	 * are assured that this method executes within the safety of a lock.
	 * As long as this method executes, the lock is locked. It is also
	 * properly unlocked, even in case of exceptions thrown from this
	 * method.
	 * 
	 * @param callback the PublishedSessionCallback to receive the result of 
	 *                 this command
	 */
	protected abstract void doWork(PublishedSessionCallback callback);

}
