package org.eclipse.jface.text;

public interface IPartitioner {

	void connect(IDocument document);
	
	void disconnect(IDocument document);
	
	void documentUpdated(DocumentEvent e);
	
	IAttributedRegion[] getRegions();
	
	IAttributedRegion getRegion(int offset);
	
}
