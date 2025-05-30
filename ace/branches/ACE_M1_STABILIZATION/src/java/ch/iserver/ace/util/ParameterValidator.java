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
 *
 */
public final class ParameterValidator {
	
	private ParameterValidator() {
		// do nothing
	}
	
	public static void notNull(String name, Object value) {
		if (value == null) {
			throw new IllegalArgumentException(name + " cannot be null");
		}
	}
	
	public static void notNegative(String name, int value) {
		if (value < 0) {
			throw new IllegalArgumentException(name + " cannot be negative");
		}
	}
	
	public static void inRange(String name, int value, int min, int max) {
		if ( !(min <= value && value <= max) ) {
			throw new IllegalArgumentException(name + " is not in range.");
		}
	}
	
}
