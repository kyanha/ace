package ch.iserver.ace.collaboration.jupiter.server.document;


public interface TextStore {

	String getText(int offset, int length);

	int getLength();

	void replace(int offset, int length, String text);

}
