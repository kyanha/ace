package ch.iserver.ace.algorithm;

import ch.iserver.ace.DocumentModel;
import ch.iserver.ace.Operation;

public interface Algorithm {

	public Request undo();
	public Request generateRequest(Operation op);
	public void receiveRequest(Request req);
	
	public void siteAdded(int siteId);
	public void siteRemoved(int siteId);
	
	public void setDocument(DocumentModel doc);
	public DocumentModel getDocument();
	
	public void setInitialTimestamp(Timestamp timestamp);
	
}
