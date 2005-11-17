package ch.iserver.ace.application;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ca.odell.glazedlists.EventList;

public interface DocumentManager {

	EventList getDocuments();

	DocumentItem getSelectedDocument();

	List getDirtyDocuments();

	void newDocument();

	void openDocument(File file) throws IOException;

	void saveDocument(DocumentItem item) throws IOException;

	void saveAsDocument(File file, DocumentItem item) throws IOException;

	void closeDocument(DocumentItem item);

	void closeAllDocuments();

	void publishDocument();

	void concealDocument();

	void sessionJoined(DocumentItem item);

	void leaveSession();

	void inviteUser();

	void kickParticipant();

}