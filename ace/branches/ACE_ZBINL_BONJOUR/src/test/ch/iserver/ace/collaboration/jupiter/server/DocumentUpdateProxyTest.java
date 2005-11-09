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

package ch.iserver.ace.collaboration.jupiter.server;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.text.NoOperation;
import ch.iserver.ace.text.SplitOperation;

public class DocumentUpdateProxyTest extends TestCase {
	
	private MockControl documentCtrl;
	
	private ServerDocument document;
	
	private Forwarder proxy;
	
	public void setUp() {
		documentCtrl = MockControl.createControl(ServerDocument.class);
		document = (ServerDocument) documentCtrl.getMock();
		proxy = new DocumentUpdateProxy(document);
	}
	
	public void testSendInsertOperation() throws Exception {
		InsertOperation op = new InsertOperation(0, "xyz");
		
		// define mock behavior
		document.insertString(1, 0, "xyz");
		
		// replay
		documentCtrl.replay();
		
		// test
		proxy.sendOperation(1, op);
	}
	
	public void testSendDeleteOperation() throws Exception {
		DeleteOperation op = new DeleteOperation(0, "xyz");
		
		// define mock behavior
		document.removeString(0, 3);
		
		// replay
		documentCtrl.replay();
		
		// test
		proxy.sendOperation(1, op);
	}
	
	public void testSendSplitOperation() throws Exception {
		SplitOperation op = new SplitOperation(new InsertOperation(0, "xyz"), new DeleteOperation(0, "xyz"));
		
		// define mock behavior
		document.insertString(1, 0, "xyz");
		document.removeString(0, 3);
		
		// replay
		documentCtrl.replay();
		
		// test
		proxy.sendOperation(1, op);
	}
	
	public void testSendNoOperation() throws Exception {
		NoOperation op = new NoOperation();
		documentCtrl.replay();
		proxy.sendOperation(1, op);
	}
	
	public void testSendCaretUpdate() throws Exception {
		CaretUpdate update = new CaretUpdate(0, 3);
		
		// define mock behavior
		document.updateCaret(1, 0, 3);
		
		// replay
		documentCtrl.replay();
		
		// test
		proxy.sendCaretUpdate(1, update);
	}
	
	public void tearDown() {
		documentCtrl.verify();
	}
	
}
