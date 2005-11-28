package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

public class CollaborationParserHandlerTest extends TestCase {

	
	public void testParseLeaveMessage() throws Exception {
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		
		deserializer.deserialize(XML_LEAVE.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING)), handler);
		
		Request result = handler.getResult();
		
		assertEquals(ProtocolConstants.LEAVE, result.getType());
		DocumentInfo info = (DocumentInfo) result.getPayload();
		assertEquals("dic-1231", info.getDocId());
		assertEquals(13, info.getParticipantId());
	}
	
	public void testParseKickedMessage() throws Exception {
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		
		deserializer.deserialize(XML_KICKED.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING)), handler);
		
		Request result = handler.getResult();
		
		assertEquals(ProtocolConstants.KICKED, result.getType());
		DocumentInfo info = (DocumentInfo) result.getPayload();
		assertEquals("sdaf-2", info.getDocId());
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
