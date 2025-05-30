package ch.iserver.ace;

public interface DocumentModel {
	
	void participantJoined(int participantId);
	
	void participantLeft(int participantId);
	
	void updateCaret(int participantId, int dot, int mark);
	
	void insertString(int offset, String text, int participantId);
	
	void removeString(int offset, int length);
	
	String getText();
	
	PortableDocument toPortableDocument();
	
}
