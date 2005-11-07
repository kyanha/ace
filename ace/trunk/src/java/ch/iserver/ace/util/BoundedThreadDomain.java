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

package ch.iserver.ace.util;

import edu.emory.mathcs.backport.java.util.concurrent.BlockingQueue;
import edu.emory.mathcs.backport.java.util.concurrent.LinkedBlockingQueue;

/**
 *
 */
public class BoundedThreadDomain extends AbstractThreadDomain {
	
	private final int maxWorkers;
	
	private final BlockingQueue[] queues;
	
	private int index;
	
	private int workers;
	
	public BoundedThreadDomain(int maxWorkers) {
		this.maxWorkers = maxWorkers;
		this.queues = new BlockingQueue[maxWorkers];
	}
	
	public Object wrap(Object target, Class clazz) {
		if (queues[index] == null) {
			queues[index] = new LinkedBlockingQueue();
			Worker worker = new AsyncWorker(queues[index]);
			worker.start();
		}
		
		BlockingQueue queue = queues[index];		
		index = (index + 1) % maxWorkers;
		
		return wrap(target, clazz, queue);
	}
	
}
