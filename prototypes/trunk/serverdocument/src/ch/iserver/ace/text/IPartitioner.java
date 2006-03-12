package ch.iserver.ace.text;

public interface IPartitioner {

	void documentUpdated(DocumentEvent e);
	
	IAttributedRegion[] getRegions();
	
}
