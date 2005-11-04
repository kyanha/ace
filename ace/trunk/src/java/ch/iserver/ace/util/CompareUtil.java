/*
 * $Id: CountingSemaphore.java 914 2005-11-03 08:42:52 +0100 (Thu, 03 Nov 2005) sim $
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

import java.util.Iterator;

public final class CompareUtil {

	private CompareUtil() {
		// ignore
	}
	
	public static boolean arrayEquals(int[] a1, int[] a2) {
		if (a1 == a2) {
			return true;
		} else if (a1 == null || a2 == null) {
			return false;
		} else if (a1.length != a2.length) {
			return false;
		} else {
			for (int i = 0; i < a1.length; i++) {
				if (a1[i] != a2[i]) {
					return false;
				}
			}
			return true;
		}
	}
	
	public static boolean iteratorEquals(Iterator i1, Iterator i2) {
		if (i1 == i2) {
			return true;
		} else if (i1 == null || i2 == null) {
			return false;
		} else if ((i1.hasNext() && !i2.hasNext()) || (!i1.hasNext() && i2.hasNext())) {
			return false;
		} else {
			while (i1.hasNext() && i2.hasNext()) {
				if (!nullSafeEquals(i1.next(), i2.next())) {
					return false;
				}
			}
			return !(i1.hasNext() && i2.hasNext());
		}
	}

	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		} else if (o1 == null || o2 == null) {
			return false;
		} else {
			return o1.equals(o2);
		}
	}
	
}
