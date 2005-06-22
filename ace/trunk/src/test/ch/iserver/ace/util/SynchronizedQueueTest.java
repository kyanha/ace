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

import junit.framework.TestCase;

/**
 * This class tests the SynchronizedQueue class.
 * 
 * @see ch.iserver.ace.util.SynchronizedQueue
 */
public class SynchronizedQueueTest extends TestCase {

	/**
	 * @throws Exception
	 */
	public void testEqualityWithoutSynchObj() throws Exception {
		SynchronizedQueue q1 = new SynchronizedQueue();
		SynchronizedQueue q2 = new SynchronizedQueue();
		
		assertEquals( q1.hashCode(), q2.hashCode() );
		assertEquals(q1, q2);
		assertTrue( q1.equals(q2) );
		
		q1.add("Teststring");
		q2.add("Teststring");
		
		assertEquals( q1.hashCode(), q2.hashCode() );
		assertEquals(q1, q2);
		
		Object obj = new Object();
		q1.add(obj);
		q2.add(obj);
		
		assertEquals( q1.hashCode(), q2.hashCode() );
		assertEquals(q1, q2);	
	}
	
	/**
	 * @throws Exception
	 */
	public void testEqualityWithSynchObj() throws Exception {
		Object synchObj = new Object();
		SynchronizedQueue q1 = new SynchronizedQueue(synchObj);
		SynchronizedQueue q2 = new SynchronizedQueue(synchObj);
		
		assertEquals( q1.hashCode(), q2.hashCode() );
		assertEquals(q1, q2);
		
		q1.add("Teststring");
		q2.add("Teststring");
		
		assertEquals( q1.hashCode(), q2.hashCode() );
		assertEquals(q1, q2);
		
		Object obj = new Object();
		q1.add(obj);
		q2.add(obj);
		
		assertEquals( q1.hashCode(), q2.hashCode() );
		assertEquals(q1, q2);	
	}
	
}
