package org.eclipse.jface.text;

public class SimplePartitioner implements IPartitioner {

	private IDocument document;
	
	public void connect(IDocument document) {
		this.document = document;
	}

	public void disconnect(IDocument document) {
		this.document = null;
	}

	public void documentUpdated(DocumentEvent e) {
		// TODO Auto-generated method stub

	}

	public IAttributedRegion[] getRegions() {
		// TODO Auto-generated method stub
		return null;
	}

}
