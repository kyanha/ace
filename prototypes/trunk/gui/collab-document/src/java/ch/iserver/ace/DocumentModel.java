package ch.iserver.ace;

public interface DocumentModel {
	
	void updateCaret(int participantId, int dot, int mark);
	
	void insertString(int offset, String text, int participantId);
	
	void removeString(int offset, int length);
	
	String getText();
	
}
