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
 *
 */
abstract class LockingCommand implements Command {

	private final AlgorithmWrapper algorithm;
	
	private final Lock lock;
	
	protected LockingCommand(Lock lock, AlgorithmWrapper algorithm) {
		ParameterValidator.notNull("lock", lock);
		ParameterValidator.notNull("algorithm", algorithm);
		this.lock = lock;
		this.algorithm = algorithm;
	}
	
	protected AlgorithmWrapper getAlgorithm() {
		return algorithm;
	}
	
	protected Lock getLock() {
		return lock;
	}
	
	public void execute(PublishedSessionCallback callback) {
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
	
	protected abstract void doWork(PublishedSessionCallback callback);

}