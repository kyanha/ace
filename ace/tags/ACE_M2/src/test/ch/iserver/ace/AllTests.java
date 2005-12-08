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

package ch.iserver.ace;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ch.iserver.ace");
		//$JUnit-BEGIN$
		suite.addTest(ch.iserver.ace.algorithm.jupiter.AllTests.suite());
		suite.addTest(ch.iserver.ace.application.AllTests.suite());
		suite.addTest(ch.iserver.ace.collaboration.jupiter.AllTests.suite());
		suite.addTest(ch.iserver.ace.collaboration.jupiter.server.AllTests.suite());
		suite.addTest(ch.iserver.ace.net.impl.protocol.AllTests.suite());
		suite.addTest(ch.iserver.ace.test.AllTests.suite());
		suite.addTest(ch.iserver.ace.text.AllTests.suite());
		suite.addTest(ch.iserver.ace.util.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
