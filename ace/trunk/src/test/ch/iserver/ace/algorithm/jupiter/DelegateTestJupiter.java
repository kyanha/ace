package ch.iserver.ace.algorithm.jupiter;

import java.util.ArrayList;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.AwarenessInformation;
import ch.iserver.ace.algorithm.InclusionTransformation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;

public class DelegateTestJupiter implements Algorithm {

	private Jupiter jupiter;
	
	private static Object synchObj = new Object(); 
	private int expectedOps;
	private int opCounter;
	
	public DelegateTestJupiter(InclusionTransformation it, DocumentModel document,
			int siteId, boolean isClientSide) {
		jupiter = new Jupiter(it, document, siteId, isClientSide);
	}
	
	public DelegateTestJupiter(int siteId, boolean isClientSide) {
		jupiter = new Jupiter(siteId, isClientSide);
	}
	
	public void setExpectedOperations(int num) {
		expectedOps = num;
		opCounter = 0;
	}
	
	public int getExpectedOperations() {
		return expectedOps;
	}
	
	
	public boolean canUndo() {
		return jupiter.canUndo();
	}

	public boolean canRedo() {
		return jupiter.canRedo();
	}

	public Request undo() throws CannotUndoException {
		return jupiter.undo();
	}

	public Request redo() throws CannotRedoException {
		return jupiter.redo();
	}

	public Request generateRequest(Operation op) {
		Request r = jupiter.generateRequest(op);
		++opCounter;
		if (opCounter >= expectedOps) {
			synchronized(synchObj) {
				synchObj.notify();
			}
		}
		return r;
	}

	public void receiveRequest(Request req) {
		jupiter.receiveRequest(req);
		++opCounter;
		if (opCounter >= expectedOps) {
			synchronized(synchObj) {
				synchObj.notify();
			}
		}
	}
	
	/**
	 * Returns document not before <code>expectedOps</code> number of operations
	 * have been processed by the algorithm. This guarantees that we have a 
	 * comparable document state throughout several algorithm instances after
	 * a number of processed operations.
	 */
	public synchronized DocumentModel getDocument() {
		if (opCounter < expectedOps) {
			//go to sleep
			try {
				System.out.println("DelegateTestJupiter.sleep: "+opCounter+" < "+expectedOps);
				synchronized(synchObj) {
					synchObj.wait();
				}
				System.out.println("DelegateTestJupiter.awakened: "+opCounter+" >= "+expectedOps);
			} catch (InterruptedException ie) {}
		}
		return jupiter.getDocument();
	}

	public AwarenessInformation receiveAwarenessInformation(AwarenessInformation info) {
		return jupiter.receiveAwarenessInformation(info);
	}

	public void init(DocumentModel doc, Timestamp timestamp) {
		jupiter.init(doc, timestamp);		
	}



}
