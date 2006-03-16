package ch.iserver.ace.collaboration.jupiter.server.document;

import java.util.Map;

import ch.iserver.ace.util.ParameterValidator;

public class SimpleDocument implements IDocument {

	private final ITextStore store;
	
	private final IPartitioner partitioner;

	public SimpleDocument(ITextStore store, IPartitioner partitioner) {
		ParameterValidator.notNull("store", store);
		ParameterValidator.notNull("partitioner", partitioner);
		this.store = store;
		this.partitioner = partitioner;
	}

	public ITextStore getStore() {
		return store;
	}
	
	public IPartitioner getPartitioner() {
		return partitioner;
	}

	public int getLength() {
		return store.getLength();
	}
	
	public String getText() {
		return store.getText(0, getLength());
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
		store.replace(offset, length, "");
		DocumentEvent event = new DocumentEvent(offset, length, "");
		partitioner.documentUpdated(event);
	}
	
}
