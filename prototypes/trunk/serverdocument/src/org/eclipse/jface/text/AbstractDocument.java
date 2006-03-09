package org.eclipse.jface.text;

import java.util.Map;

public abstract class AbstractDocument implements IDocument {

	private final ITextStore store;
	
	private IPartitioner partitioner;

	protected AbstractDocument(ITextStore store) {
		this.store = store;
	}
	
	public void setPartitioner(IPartitioner partitioner) {
		if (this.partitioner != null) {
			this.partitioner.disconnect(this);
		}
		this.partitioner = partitioner;
		if (this.partitioner != null) {
			this.partitioner.connect(this);
		}
	}

	public IPartitioner getPartitioner() {
		return partitioner;
	}

	public int getLength() {
		return store.getLength();
	}
	
	public String getText() {
		return store.get(0, getLength());
	}

	public void insertString(int offset, String text, Map attributes) {
		store.replace(offset, 0, text);
		DocumentEvent event = new DocumentEvent(offset, 0, text, attributes);
		partitioner.documentUpdated(event);
	}

	public void replaceRange(int offset, int length, String text, Map attributes) {
		store.replace(offset, length, text);
		DocumentEvent event = new DocumentEvent(offset, length, text, attributes);
		partitioner.documentUpdated(event);
	}

	public void removeRange(int offset, int length) {
		store.replace(0, length, "");
		DocumentEvent event = new DocumentEvent(offset, length, "");
		partitioner.documentUpdated(event);
	}

}
