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

import ch.iserver.ace.collaboration.Participant;
import ch.iserver.ace.collaboration.jupiter.server.FailureHandler;
import ch.iserver.ace.collaboration.jupiter.server.Forwarder;
import ch.iserver.ace.util.Worker;
import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class CommandProcessorImpl extends Worker implements CommandProcessor {
	
	private static final Logger LOG = Logger.getLogger(CommandProcessor.class);

	private final BlockingQueue queue;
	
	private final Forwarder forwarder;
	
	private final FailureHandler failureHandler;

	public CommandProcessorImpl(Forwarder forwarder, FailureHandler failureHandler) {
		super("serializer");
		this.queue = new LinkedBlockingQueue();
		this.forwarder = forwarder;
		this.failureHandler = failureHandler;
	}
	
	protected Forwarder getForwarder() {
		return forwarder;
	}
		
	protected FailureHandler getFailureHandler() {
		return failureHandler;
	}

	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.serializer.CommandProcessor#startProcessor()
	 */
	public void startProcessor() {
		start();
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.serializer.CommandProcessor#process(ch.iserver.ace.collaboration.jupiter.server.serializer.SerializerCommand)
	 */
	public void process(SerializerCommand command) {
		queue.add(command);
	}
	
	/**
	 * @see ch.iserver.ace.collaboration.jupiter.server.serializer.CommandProcessor#stopProcessor()
	 */
	public void stopProcessor() {
		kill();
	}
	
	protected void doWork() throws InterruptedException {
		SerializerCommand cmd = (SerializerCommand) queue.take();
		LOG.info("serializing next command ...");
		try {
			cmd.execute(getForwarder());
			LOG.info("command executed ...");
		} catch (SerializerException e) {
			LOG.warn("command execution failed: " + e.getMessage());
			getFailureHandler().handleFailure(e.getParticipantId(), Participant.RECEPTION_FAILED);
		}
	}

}
