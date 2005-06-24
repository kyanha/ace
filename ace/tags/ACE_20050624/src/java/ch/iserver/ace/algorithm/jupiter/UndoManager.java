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

import org.apache.log4j.Logger;

import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Request;

/**
 *
 */
public class UndoManager {

	private static Logger LOG = Logger.getLogger(UndoManager.class);
	
	//TODO: limit size of undo buffer
	private List localUndoCandidates;
	//this list contains all local requests (incl. undo's, redo's)
	private List localRequests;
	private List remoteRequests;
	private int nextUndoOperation;
	
	/**
	 * Class constructor.
	 */
	public UndoManager() {
		localUndoCandidates = Collections.synchronizedList(new ArrayList());
		localRequests = Collections.synchronizedList(new ArrayList());
		remoteRequests = Collections.synchronizedList(new ArrayList());
		nextUndoOperation = -1;
	}
	
	/**
	 * Returns the request which can be undone next.
	 * 
	 * @return the request that can be undone next
	 * @throws CannotUndoException
	 */
	public Request nextUndo() {
		if (!canUndo()) throw new CannotUndoException(); 
		//return undo request and move history list pointer
		return (Request)localUndoCandidates.get(nextUndoOperation--);
	}
	
	/**
	 * Adds an undo request.
	 *
	 * @param request the request to be added
	 */
	public void addUndo(Request request) {
		clearRedo();
		localUndoCandidates.add(++nextUndoOperation, request);
	}
	
	/**
	 * Remove the redo requests that cannot be reached any more.
	 */
	private void clearRedo() {
		//TODO: test this method thorougly
		if (!localUndoCandidates.isEmpty() && nextUndoOperation < localUndoCandidates.size()-1) {
			localUndoCandidates.subList(nextUndoOperation+1, localUndoCandidates.size()).clear();
		}
	}
	
	/**
	 * Returns the next redo request.
	 * 
	 * @return the next request that can be redone
	 * @throws CannotRedoException
	 */
	public Request nextRedo() {
		if (!canRedo()) throw new CannotRedoException();
		return (Request)localUndoCandidates.get(++nextUndoOperation);
	}
	
	/**
	 * Adds a remote request, e.g. a request that has been received
	 * over the network.
	 * 
	 * @param request the remote request
	 */
	public void addRemote(Request request) {
		remoteRequests.add(request);
	}
	
	/**
	 * Adds a locally generated request.
	 * 
	 * @param request the local request to be added
	 */
	public void addLocal(Request request) {
		localRequests.add(request);
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
	
	/**
	 * Returns a list of Object[]{Request, index} which all have
	 * a <code>localOperationCount</code> greater or equals to <code>base</code>.
	 * 
	 * @param base the base count
	 * @return a list of object arrays
	 */
	public List getLocalTransformationSet(int base) {
		LOG.info("localRequest: "+localRequests);
		//TODO: this is a code duplication of OutgoingQueue.getTransformationSet()...
		//only consider operations with localOperationCount count >= base
		List reqs = new ArrayList();
		Iterator iter = localRequests.iterator();
		while (iter.hasNext()) {
			Request req = (Request)iter.next();
			if (((JupiterVectorTime)req.getTimestamp()).getLocalOperationCount() >= base) {
				reqs.add(req);
			}			
		}

		List result;
		if (reqs.size() <= 1) {
			result = new ArrayList();
			if (!reqs.isEmpty()) {
				result.add(new Object[]{(Request)reqs.get(0),new Integer(0)});
			}
		} else {
			int undos = 0, pairs = 0;
			List tmp = new ArrayList(reqs);
			//reverse list so that undo pairs can be recognized
			Collections.reverse(tmp);
			iter = tmp.iterator();
			//count the number of undo pairs
			//operations list examples: {d,u,u,d,d} {d,d,u,u} {u,u,d}; u=undo, d=do 
			while (iter.hasNext()) {
				Request r = (Request)iter.next();
				Operation op = r.getOperation();
				if (op.isUndo()) {
					undos++;
				} else if (undos > 0) {
					pairs++;
					undos--;
				}
			}
			iter = tmp.iterator();
			result = new ArrayList();
			int undoCnt = 0, opIndex = 0;
			while (iter.hasNext()) {
				Request r = (Request)iter.next();
				Operation op = r.getOperation();
				if (op.isUndo() && pairs > 0 && undos == 0) { 
					undoCnt++;
					pairs--;
				} else if (undoCnt == 0) {
					//TODO: this is can be improved, but we have to remember the index of this wrap 
					//into the operation list, so that it can be updated later.
					//the list we iterate in is in reverse order, hence the position 
					//is calculated as follows: operations.size()-1-opIndex
					result.add(new Object[]{r, new Integer(localRequests.size()-1-opIndex)});
					if (op.isUndo()) undos--;
				} else {
					undoCnt--;
				}
				++opIndex;
			}
		}
		Collections.reverse(result);
		LOG.info("set size: "+result.size());
		return result;
	}
	
	/**
	 * Returns true if an undo can be performed.
	 * 
	 * @return true if an undo can be performed
	 */
	public boolean canUndo() {
		return (nextUndoOperation >= 0);
	}
	
	/**
	 * Returns true if a redo can be performed.
	 * 
	 * @return true if a redo can be performed
	 */
	public boolean canRedo() {
		return (localUndoCandidates.size() > 0 && nextUndoOperation < localUndoCandidates.size()-1);
	}
	
}
