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
package ch.iserver.ace.algorithm.jupiter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.text.DeleteOperation;
import ch.iserver.ace.text.InsertOperation;

/**
 *
 */
public class UndoManager {

	private List localRequests;
	private List remoteRequests;
	private int nextUndoOperation;
	
	public UndoManager() {
		localRequests = new ArrayList();
		remoteRequests = new ArrayList();
		nextUndoOperation = -1;
	}
	
	public Request nextUndo() {
		if (!canUndo()) throw new CannotUndoException(); 
		//return undo request and move history list pointer
		return (Request)localRequests.get(nextUndoOperation--);
	}
	
	public void addUndo(Request request) {
		localRequests.add(++nextUndoOperation, request);
	}
	
	public Request nextRedo() {
		if (!canRedo()) throw new CannotRedoException();
		return (Request)localRequests.get(++nextUndoOperation);
	}
	
	public void clearRedo() {
		
	}
	
	public void addRemote(Request request) {
		remoteRequests.add(request);
	}
	
	public Operation mirror(Operation op) {
		Operation transformedOp = null;
		if (op instanceof InsertOperation) {
			transformedOp = new DeleteOperation(
					((InsertOperation)op).getPosition(), 
					((InsertOperation)op).getText()
					);
		} else if (op instanceof DeleteOperation) {
			transformedOp = new InsertOperation(
					((DeleteOperation)op).getPosition(), 
					((DeleteOperation)op).getText()
					);
		}
		return transformedOp;
	}
	
	/**
	 * 
	 * @param base
	 * @return all operations with a remote operation count greater
	 * 			 	or equals to <code>base</code>.
	 */
	public List getRemoteRequests(int base) {
		List tmp = new ArrayList();
		Iterator iter = remoteRequests.iterator();
		while (iter.hasNext()) {
			Request req = (Request)iter.next();
			if (((JupiterVectorTime)req.getTimestamp()).getRemoteOperationCount() >= base) {
				tmp.add(req);
			}
		}
		return tmp;
	}
	
	public boolean canUndo() {
		return (nextUndoOperation >= 0);
	}
	
	public boolean canRedo() {
		return (localRequests.size() > 0 && nextUndoOperation < localRequests.size()-1);
	}
	
}
