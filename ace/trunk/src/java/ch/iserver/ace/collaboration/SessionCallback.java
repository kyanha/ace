package ch.iserver.ace.collaboration;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;

/**
 *
 */
public interface SessionCallback {

	/**
	 * Notifies the session callback that a new user joined the Session.
	 * 
	 * @param participant the participant that joined
	 */
	void participantJoined(Participant participant);

	/**
	 * Notifies the session callback that a user left the Session.
	 * 
	 * @param participant the participant that left the Session
	 * @param code the reason why the participant left
	 */
	void participantLeft(Participant participant, int code);

	/**
	 * Receives an operation from the given participant.
	 * 
	 * @param participant the participant that sent the operation
	 * @param operation the operation to be applied to the document
	 */
	void receiveOperation(Participant participant, Operation operation);

	/**
	 * Receives a caret update from the given participant.
	 * 
	 * @param participant the participant that sent the CaretUpdate
	 * @param update the caret update specification
	 */
	void receiveCaretUpdate(Participant participant, CaretUpdate update);

	/**
	 * @param reason
	 * @param e
	 */
	void sessionFailed(int reason, Exception e);

}