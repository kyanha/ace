package ch.iserver.ace.collaboration.jupiter.server;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.net.RemoteUserProxy;

public interface Forwarder {

	void sendCaretUpdate(int participantId, CaretUpdate update);

	void sendOperation(int participantId, Operation op);

	void sendParticipantLeft(int participantId, int reason);

	void sendParticipantJoined(int participantId, RemoteUserProxy user);

	void close();

}