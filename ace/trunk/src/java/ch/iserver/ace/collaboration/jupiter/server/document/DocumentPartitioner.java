package ch.iserver.ace.collaboration.jupiter.server.document;

public interface DocumentPartitioner {

	void documentUpdated(DocumentEvent e);
	
	AttributedRegion[] getRegions();
	
}
