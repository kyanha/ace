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

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.collaboration.Invitation;
import ch.iserver.ace.collaboration.JoinCallback;
import ch.iserver.ace.collaboration.RemoteDocument;
import ch.iserver.ace.collaboration.RemoteDocumentStub;
import ch.iserver.ace.collaboration.RemoteUser;
import ch.iserver.ace.collaboration.RemoteUserStub;
import ch.iserver.ace.net.InvitationProxy;
import ch.iserver.ace.net.JoinNetworkCallback;

/**
 *
 */
public class InvitationImplTest extends TestCase {
	
	public void testReject() throws Exception {
		MockControl proxyCtrl = MockControl.createControl(InvitationProxy.class);
		InvitationProxy proxy = (InvitationProxy) proxyCtrl.getMock();
		MockControl factoryCtrl = MockControl.createControl(SessionFactory.class);
		SessionFactory factory = (SessionFactory) factoryCtrl.getMock();
		
		RemoteUser inviter = new RemoteUserStub("X");
		RemoteDocument document = new RemoteDocumentStub("XDOC", "collab.txt", inviter);
		Invitation invitation = new InvitationImpl(proxy, document, factory);
		
		// define mock behavior
		proxy.reject();
		
		// replay
		proxyCtrl.replay();
		factoryCtrl.replay();
		
		// test
		invitation.reject();
		
		// verify
		proxyCtrl.verify();
		factoryCtrl.verify();
	}
	
	public void testGetInviter() throws Exception {
		MockControl proxyCtrl = MockControl.createControl(InvitationProxy.class);
		InvitationProxy proxy = (InvitationProxy) proxyCtrl.getMock();
		MockControl factoryCtrl = MockControl.createControl(SessionFactory.class);
		SessionFactory factory = (SessionFactory) factoryCtrl.getMock();
			
		RemoteUser inviter = new RemoteUserStub("X");
		RemoteDocument document = new RemoteDocumentStub("XDOC", "collab.txt", inviter);
		Invitation invitation = new InvitationImpl(proxy, document, factory);
			
		// replay
		proxyCtrl.replay();
		factoryCtrl.replay();
			
		// test
		assertSame(inviter, invitation.getInviter());
		
		// verify
		proxyCtrl.verify();
		factoryCtrl.verify();
	}
	
	public void testAccept() throws Exception {
		MockControl proxyCtrl = MockControl.createControl(InvitationProxy.class);
		InvitationProxy proxy = (InvitationProxy) proxyCtrl.getMock();
		MockControl factoryCtrl = MockControl.createControl(SessionFactory.class);
		SessionFactory factory = (SessionFactory) factoryCtrl.getMock();
		MockControl callbackCtrl = MockControl.createControl(JoinNetworkCallback.class);
		final JoinNetworkCallback callback = (JoinNetworkCallback) callbackCtrl.getMock();
		MockControl cbkCtrl = MockControl.createControl(JoinCallback.class);
		final JoinCallback cbk = (JoinCallback) cbkCtrl.getMock();
		
		RemoteUser inviter = new RemoteUserStub("X");
		RemoteDocument document = new RemoteDocumentStub("XDOC", "collab.txt", inviter);
		Invitation invitation = new InvitationImpl(proxy, document, factory) {
			protected JoinNetworkCallback createJoinNetworkCallback(JoinCallback jc, SessionFactory factory) {
				assertTrue(cbk == jc);
				return callback;
			}
		};
		
		// define mock behavior
		proxy.accept(callback);
		
		// replay
		proxyCtrl.replay();
		factoryCtrl.replay();
		
		// test
		invitation.accept(cbk);
		
		// verify
		proxyCtrl.verify();
		factoryCtrl.verify();
	}

}
