package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.PortableDocumentExt;
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
	
	public void testJoinDocument() throws Exception {
		CollaborationParserHandler handler = new CollaborationParserHandler();
		Deserializer deserializer = DeserializerImpl.getInstance();
		
		byte[] data = XML_JOIN_DOCUMENT.getBytes(NetworkConstants.DEFAULT_ENCODING);
		String userId = "sadfasd-24";
		NetworkServiceImpl.getInstance().setUserId(userId);
		
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		
		assertEquals(result.getType(), ProtocolConstants.JOIN_DOCUMENT);
		PortableDocumentExt doc = (PortableDocumentExt) result.getPayload();
		assertEquals(1, doc.getParticipantId());
		assertEquals("ASDF-23", doc.getDocumentId());
		assertEquals("adfasdf-21", doc.getPublisherId());
		int[] ids = doc.getParticipantIds();
		assertEquals(3, ids.length);
		assertEquals(0, ids[0]);
		assertEquals(1, ids[1]);
		assertEquals(2, ids[2]);
		//TODO: write all possible assertions
	}
	
	private static final String XML_LEAVE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><notification>" +
			"<leave docId=\"dic-1231\" participantId=\"13\"/>" +
			"</notification></ace>";
	
	private static final String XML_KICKED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><notification>" +
	"<kicked docId=\"sdaf-2\"/>" +
	"</notification></ace>";
	
	public static final String XML_JOIN_DOCUMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><response>" +
	"<document id=\"ASDF-23\" userid=\"adfasdf-21\">" +
	"<participants>" +
	"<participant id=\"0\">" +
	"<user id=\"adfasdf-21\" name=\"John Huderi\" address=\"254.23.12.98\" port=\"4123\" explicitDiscovery=\"false\"/>" +
	"<selection mark=\"0\" dot=\"0\"/>" +
	"</participant>" +
	"<participant id=\"1\">" +
	"<user id=\"sadfasd-24\" name=\"Jimmy Ritter\" address=\"123.43.45.21\" port=\"4123\" explicitDiscovery=\"false\"/>" +
	"<selection mark=\"456\" dot=\"456\"/>" +
	"</participant>" +
	"<participant id=\"2\">" +
	"<user id=\"cbvncvvc-24\" name=\"Samuel Fuchs\" address=\"123.43.12.197\" port=\"4123\" explicitDiscovery=\"false\"/>" +
	"<selection mark=\"7\" dot=\"7\"/>" +
	"</participant>" +
	"</participants>" +
	"<data>" +
	"<![CDATA[0 11 Los gehts:  1 15 ich habe durst. 2 18  das sagst du mir? 1 20  dir sage ich alles!]]>" +
	"</data>" +
	"</document>" +
	"</response></ace>";	
}
