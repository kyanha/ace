package ch.iserver.ace.collaboration.jupiter.server.document;

import java.util.Map;

public interface IDocument {
	
	IPartitioner getPartitioner();
	
	int getLength();
	
	String getText();
	
	void insertString(int offset, String text, Map attributes);
	
	void replaceRange(int offset, int length, String text, Map attributes);
	
	void removeRange(int offset, int length);
	
}
