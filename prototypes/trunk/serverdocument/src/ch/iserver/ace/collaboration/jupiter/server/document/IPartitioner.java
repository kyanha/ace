package ch.iserver.ace.collaboration.jupiter.server.document;

public interface IPartitioner {

	void documentUpdated(DocumentEvent e);
	
	IAttributedRegion[] getRegions();
	
}
