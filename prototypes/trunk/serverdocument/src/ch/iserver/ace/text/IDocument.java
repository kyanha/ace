package ch.iserver.ace.text;

import java.util.Map;

public interface IDocument {
	
	void setPartitioner(IPartitioner partitioner);
	
	IPartitioner getPartitioner();
	
	int getLength();
	
	String getText();
	
	void insertString(int offset, String text, Map attributes);
	
	void replaceRange(int offset, int length, String text, Map attributes);
	
	void removeRange(int offset, int length);
	
}
