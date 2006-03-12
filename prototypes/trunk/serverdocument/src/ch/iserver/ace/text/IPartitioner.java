package org.eclipse.jface.text;

public interface IPartitioner {

	void documentUpdated(DocumentEvent e);
	
	IAttributedRegion[] getRegions();
	
}
