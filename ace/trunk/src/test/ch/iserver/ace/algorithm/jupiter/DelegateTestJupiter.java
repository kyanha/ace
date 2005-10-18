package ch.iserver.ace.algorithm.jupiter;

import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.Algorithm;
import ch.iserver.ace.algorithm.InclusionTransformation;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.Timestamp;

public class DelegateTestJupiter implements Algorithm {

	private Jupiter jupiter;
	
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

	public synchronized Request generateRequest(Operation op) {
		Request r = jupiter.generateRequest(op);
		++opCounter;
		if (opCounter >= expectedOps) {
			notify();
		}
		return r;
	}

	public synchronized void receiveRequest(Request req) {
		jupiter.receiveRequest(req);
		++opCounter;
		if (opCounter >= expectedOps) {
			notify();
		}
	}
	
	/**
	 * Returns document not before <code>expectedOps</code> number of operations
	 * have been processed by the algorithm. This guarantees that we have a 
	 * comparable document state throughout several algorithm instances after
	 * a number of processed operations.
	 */
	//kommt ein thread bei einer synchronized methode, die er noch nicht aufrufen kann,
	//auch in denselben wait state dieses objekts wie ein thread welcher wait() auf 
	//dem objekt aufruft?
	public synchronized DocumentModel getDocument() {
		while (opCounter < expectedOps) {
			//go to sleep
			try {
				System.out.println("DelegateTestJupiter.sleep: "+opCounter+" < "+expectedOps);
				wait();
				System.out.println("DelegateTestJupiter.awakened: "+opCounter+" >= "+expectedOps);
			} catch (InterruptedException ie) {}
		}
		return jupiter.getDocument();
	}

	public void init(DocumentModel doc, Timestamp timestamp) {
		jupiter.init(doc, timestamp);		
	}

	public int[] transformIndices(Timestamp timestamp, int[] indices) {
		return jupiter.transformIndices(timestamp, indices);
	}



}
