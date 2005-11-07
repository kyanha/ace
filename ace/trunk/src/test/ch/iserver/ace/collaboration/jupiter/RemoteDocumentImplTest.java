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

package ch.iserver.ace.collaboration.jupiter;

import org.easymock.MockControl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.RemoteDocumentProxy;

import junit.framework.TestCase;

public class RemoteDocumentImplTest extends TestCase {
		
	public void testEquals() throws Exception {
		MockControl registryCtrl = MockControl.createControl(UserRegistry.class);
		UserRegistry registry = (UserRegistry) registryCtrl.getMock();
		
		MockControl proxy1Ctrl = MockControl.createControl(RemoteDocumentProxy.class);
		RemoteDocumentProxy proxy1 = (RemoteDocumentProxy) proxy1Ctrl.getMock();
		MockControl proxy2Ctrl = MockControl.createControl(RemoteDocumentProxy.class);
		RemoteDocumentProxy proxy2 = (RemoteDocumentProxy) proxy2Ctrl.getMock();
		MockControl proxy3Ctrl = MockControl.createControl(RemoteDocumentProxy.class);
		RemoteDocumentProxy proxy3 = (RemoteDocumentProxy) proxy3Ctrl.getMock();
		
		// define mock behavior
		registry.getUser("ABCDEFG");
		registryCtrl.setDefaultReturnValue(new RemoteUserStub("ABCDEFG"));
		registry.getUser("ABCDE");
		registryCtrl.setDefaultReturnValue(new RemoteUserStub("ABCDE"));
		registry.getUser("ABCDE");
		registryCtrl.setDefaultReturnValue(new RemoteUserStub("ABCDE"));
		
		proxy1.getId();
		proxy1Ctrl.setDefaultReturnValue("ABCDEFG");
		proxy1.getPublisher();
		proxy1Ctrl.setReturnValue(new RemoteUserProxyStub("ABCDEFG"));
		proxy1.getDocumentDetails();
		proxy1Ctrl.setDefaultReturnValue(new DocumentDetails("X"));
		proxy2.getId();
		proxy2Ctrl.setDefaultReturnValue("ABCDE");
		proxy2.getPublisher();
		proxy2Ctrl.setReturnValue(new RemoteUserProxyStub("ABCDE"));
		proxy2.getDocumentDetails();
		proxy2Ctrl.setDefaultReturnValue(new DocumentDetails("Y"));
		proxy3.getId();
		proxy3Ctrl.setDefaultReturnValue("ABCDE");
		proxy3.getPublisher();
		proxy3Ctrl.setReturnValue(new RemoteUserProxyStub("ABCDE"));
		proxy3.getDocumentDetails();
		proxy3Ctrl.setDefaultReturnValue(new DocumentDetails("Z"));
		
		// replay
		registryCtrl.replay();
		proxy1Ctrl.replay();
		proxy2Ctrl.replay();
		proxy3Ctrl.replay();
		
		// test
		RemoteDocumentImpl doc1 = new RemoteDocumentImpl(NullSessionConnectionDecorator.getInstance(), proxy1, registry);
		RemoteDocumentImpl doc2 = new RemoteDocumentImpl(NullSessionConnectionDecorator.getInstance(), proxy2, registry);
		RemoteDocumentImpl doc3 = new RemoteDocumentImpl(NullSessionConnectionDecorator.getInstance(), proxy3, registry);

		assertFalse(doc1.equals(doc2));
		assertFalse(doc3.equals(doc1));
		assertTrue(doc2.equals(doc3));
		assertTrue(doc3.equals(doc2));
		assertFalse(doc1.equals(doc3));
		assertFalse(doc3.equals(doc1));
		
		// verify
		registryCtrl.verify();
		proxy1Ctrl.verify();
		proxy2Ctrl.verify();
		proxy3Ctrl.verify();
	}
	
}
