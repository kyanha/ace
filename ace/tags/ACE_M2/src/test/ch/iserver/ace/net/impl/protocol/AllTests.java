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

package ch.iserver.ace.net.impl.protocol;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 *
 */
public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ch.iserver.ace.net.impl.protocol");
		//$JUnit-BEGIN$
		suite.addTestSuite(CollaborationParserHandlerTest.class);
		suite.addTestSuite(CollaborationSerializerTest.class);
		suite.addTestSuite(ConcealDocumentPrepareFilterTest.class);
		suite.addTestSuite(ConcealDocumentReceiveFilterTest.class);
		suite.addTestSuite(DeserializerImplTest.class);
		suite.addTestSuite(ParticipantConnectionImplTest.class);
		suite.addTestSuite(RequestParserHandlerTest.class);
		suite.addTestSuite(ResponseParserHandlerTest.class);
		suite.addTestSuite(SendDocumentsReceiveFilterTest.class);
		suite.addTestSuite(SerializerImplTest.class);
		suite.addTestSuite(SessionManagerTest.class);
		suite.addTestSuite(StartChannelListenerImplTest.class);
		suite.addTestSuite(TLVHandlerTest.class);
		//$JUnit-END$
		return suite;
	}
	
}
