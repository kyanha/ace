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
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.iserver.ace.algorithm.Request;

/**
 *
 */
public class UndoManager {

	private List localRequests;
	private List remoteRequests;
	private int nextUndoOperation;
	
	public UndoManager() {
		localRequests = Collections.synchronizedList(new ArrayList());
		remoteRequests = Collections.synchronizedList(new ArrayList());
		nextUndoOperation = -1;
	}
	
	public Request nextUndo() {
		if (!canUndo()) throw new CannotUndoException(); 
		//return undo request and move history list pointer
		return (Request)localRequests.get(nextUndoOperation--);
	}
	
	public void addUndo(Request request) {
		clearRedo();
		localRequests.add(++nextUndoOperation, request);
	}
	
	/**
	 * Remove the redo requests that cannot be reached any more.
	 */
	private void clearRedo() {
		localRequests.subList(nextUndoOperation+1, localRequests.size()).clear();
	}
	
	public Request nextRedo() {
		if (!canRedo()) throw new CannotRedoException();
		return (Request)localRequests.get(++nextUndoOperation);
	}
	
	public void addRemote(Request request) {
		remoteRequests.add(request);
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
