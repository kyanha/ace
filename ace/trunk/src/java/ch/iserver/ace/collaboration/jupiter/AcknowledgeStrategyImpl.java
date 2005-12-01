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

import org.apache.log4j.Logger;

import ch.iserver.ace.util.ParameterValidator;
import edu.emory.mathcs.backport.java.util.concurrent.Future;
import edu.emory.mathcs.backport.java.util.concurrent.ScheduledExecutorService;
import edu.emory.mathcs.backport.java.util.concurrent.TimeUnit;

/**
 * Default implementation of the AcknowledgeStrategy interface.
 * Uses a scheduled executor service to periodically schedule
 * the acknowledge action's execution. Calling the resetTimer method results
 * in a cancelling of the currently scheduled runnable and reschedules
 * it again.
 */
class AcknowledgeStrategyImpl implements AcknowledgeStrategy {

	/**
	 * Logger used by this class.
	 */
	private static final Logger LOG = Logger.getLogger(AcknowledgeStrategyImpl.class);
	
	/**
	 * The executor service used to schedule the execution of the action.
	 */
	private final ScheduledExecutorService executorService;
	
	/**
	 * The delay after which to fire the action.
	 */
	private final int delay;
	
	private int messages = 0;
	
	private AcknowledgeAction action;
	
	/**
	 * The runnable that is scheduled.
	 */
	private Runnable runnable;
	
	/**
	 * Future object of the currently scheduled execution. This is uesd to
	 * cancel an execution.
	 */
	private Future future;
	
	/**
	 * Creates a new AcknowledgeStrategyImpl class.
	 * 
	 * @param executorService the executor service used to schedule objects
	 * @param delay the delay in seconds from the last reset to the firing of the action
	 */
	AcknowledgeStrategyImpl(ScheduledExecutorService executorService, int delay) {
		ParameterValidator.notNull("executorService", executorService);
		this.executorService = executorService;
		this.delay = delay;
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.AcknowledgeStrategy#messageReceived()
	 */
	public synchronized void messageReceived() {
		messages++;
		if (messages == 10) {
			action.execute();
			reset();
			messages = 0;
		}
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.AcknowledgeStrategy#reset()
	 */
	public void reset() {
		if (future == null) {
			throw new IllegalStateException("cannot reset unscheduled AcknowledgeManager");
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug("reset timer of AcknowledgeStrategy " + this);
		}
		future.cancel(false);
		schedule();
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.AcknowledgeStrategy#init(ch.iserver.ace.collaboration.jupiter.AcknowledgeAction)
	 */
	public void init(final AcknowledgeAction action) {
		ParameterValidator.notNull("action", action);
		if (LOG.isDebugEnabled()) {
			LOG.debug("initialize AcknowledgeStrategy " + this);
		}
		this.action = action;
		this.runnable= new Runnable() {
			public void run() {
				LOG.debug("executing acknowledge action ...");
				action.execute();
			}
		};
		schedule();
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.AcknowledgeStrategy#destroy()
	 */
	public void destroy() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("destroy AcknowledgeStrategy " + this);
		}
		future.cancel(false);
	}
	
	/**
	 * Schedules the execution of the action with the executor service.
	 */
	protected void schedule() {
		if (LOG.isDebugEnabled()) {
			LOG.debug("reschedule execution of AcknowledgeStrategy " + this);
		}
		this.future = executorService.scheduleWithFixedDelay(runnable, delay, delay, TimeUnit.SECONDS);
	}
	
}
