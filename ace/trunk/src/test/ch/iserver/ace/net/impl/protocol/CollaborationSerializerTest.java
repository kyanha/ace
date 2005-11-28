package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.SessionConnectionImpl;

public class CollaborationSerializerTest extends TestCase {

	
	public void testCreateNotificationLeave() throws Exception {
		
		CollaborationSerializer serializer = new CollaborationSerializer();
		
		SessionConnectionImpl conn = new SessionConnectionImpl("dic-1231", null, null, null, null);
		conn.setParticipantId(13);
		
		byte[] data = serializer.createNotification(ProtocolConstants.LEAVE, conn);
		String actual = new String(data);
		
		assertEquals(XML_LEAVE, actual);
	}
	
	public void testCreateNotificationKicked() throws Exception {
		CollaborationSerializer serializer = new CollaborationSerializer();
		
		String docId = "sdaf-2";
		
		byte[] data = serializer.createNotification(ProtocolConstants.KICKED, docId);
		String actual = new String(data);
		
		assertEquals(XML_KICKED, actual);
	}
	
	private static final String XML_LEAVE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><notification>" +
			"<leave docId=\"dic-1231\" participantId=\"13\"/>" +
			"</notification></ace>";
	
	private static final String XML_KICKED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><notification>" +
	"<kicked docId=\"sdaf-2\"/>" +
	"</notification></ace>";
	
}
