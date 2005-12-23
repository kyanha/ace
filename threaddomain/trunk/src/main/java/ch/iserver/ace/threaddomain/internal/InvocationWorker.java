/*
 * $Id$
 *
 * threaddomain
 * Copyright (C) 2005 Simon Raess
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

package ch.iserver.ace.threaddomain.internal;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 *
 */
public class InvocationWorker implements Runnable {
	
	private final List<BlockingQueue<Invocation>> queues;
	
	private boolean running;
	
	public InvocationWorker(List<BlockingQueue<Invocation>> queues) {
		this.queues = queues;
	}
	
	public void start() {
		running = true;
		new Thread(this).start();
	}
	
	public void stop() {
		running = false;
	}
	
	public void run() {
		try {
			while (running) {
				for (BlockingQueue<Invocation> queue : queues) {
					Invocation invocation = (Invocation) queue.poll(1, TimeUnit.MICROSECONDS);
					if (invocation != null) {
						invocation.proceed();
					}
				}
				Thread.yield();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
}
