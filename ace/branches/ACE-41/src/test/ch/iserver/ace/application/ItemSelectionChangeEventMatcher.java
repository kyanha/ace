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

package ch.iserver.ace.application;

import org.easymock.AbstractMatcher;

import ch.iserver.ace.util.CompareUtil;

/**
 *
 */
public class ItemSelectionChangeEventMatcher extends AbstractMatcher {

	protected boolean argumentMatches(Object arg0, Object arg1) {
		if (arg0 == arg1) {
			return true;
		} else if (arg0 == null || arg1 == null) {
			return false;
		} else if (ItemSelectionChangeEvent.class.equals(arg0.getClass()) 
						&& ItemSelectionChangeEvent.class.equals(arg1.getClass())) {
			ItemSelectionChangeEvent e0 = (ItemSelectionChangeEvent) arg0;
			ItemSelectionChangeEvent e1 = (ItemSelectionChangeEvent) arg1;
			return e0.getSource() == e1.getSource() 
					&& CompareUtil.nullSafeEquals(e0.getItem(), e1.getItem());	
		} else {
			return arg0.equals(arg1);
		}
	}
	
	protected String argumentToString(Object arg0) {
		return arg0 == null ? "" : arg0.toString();
	}

}
