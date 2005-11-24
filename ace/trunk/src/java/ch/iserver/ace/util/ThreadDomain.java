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

/**
 * ThreadDomain allow to wrap objects so that they are executed on different
 * threads. By using thread domains, it is possible to easily determine
 * the number of threads in a system. A thread domain could create a worker
 * thread for each call to {@link #wrap(Object, Class)} or could use
 * a more conservative approach. It is even possible to choose an 
 * implementation that returns the target object unmodified, thus executing
 * the methods in the thread of the caller.
 */
public interface ThreadDomain {
	
	/**
	 * @param name
	 */
	void setName(String name);
	
	/**
	 * @return
	 */
	String getName();
	
	/**
	 * Wraps the <var>target</var> object so that calls to it are executed
	 * in this ThreadDomain.
	 * 
	 * @param target the target object to be wrapped
	 * @param clazz the interface implemented by the target object
	 * @return the proxied target object
	 */
	Object wrap(Object target, Class clazz);
	
}
