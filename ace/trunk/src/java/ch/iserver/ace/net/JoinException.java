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

package ch.iserver.ace.net;

/**
 *
 */
public class JoinException extends Exception {
	
	public static final int BLACKLISTED = 1;
	
	public static final int REJECTED = 2;
	
	public static final int UNKNOWN_USER = 3;
	
	private int code;
	
	/**
	 * Constructor.
	 * 
	 * @param message an error message
	 */
	public JoinException(String message) {
		super(message);
	}
	
	public JoinException(int code) {
		this.code = code;
	}
	
	public JoinException(int code, String message) {
		super(message);
		this.code = code;
	}
	
	/**
	 * Default constructor.
	 * 
	 */
	public JoinException() {
	}
	
	public int getCode() {
		return code;
	}
	
}
