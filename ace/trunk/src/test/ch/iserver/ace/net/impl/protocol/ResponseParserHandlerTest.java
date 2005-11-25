package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.PortableDocumentExt;

public class ResponseParserHandlerTest extends TestCase {

	
	public void testPublishedDocuments() throws Exception {
		ResponseParserHandler handler = new ResponseParserHandler();
		Deserializer deserializer = DeserializerImpl.getInstance();
		
		byte[] data = XML_JOIN_DOCUMENT.getBytes(NetworkConstants.DEFAULT_ENCODING);
		String userId = "adfasdf-21";
		QueryInfo qInfo = new QueryInfo(userId, ProtocolConstants.JOIN_DOCUMENT);
		handler.setMetaData(qInfo);
		
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		
		assertEquals(result.getType(), ProtocolConstants.JOIN_DOCUMENT);
		PortableDocumentExt doc = (PortableDocumentExt) result.getPayload();
		assertEquals("ASDF-23", doc.getDocumentId());
		assertEquals("adfasdf-21", doc.getPublisherId());
		int[] ids = doc.getParticipantIds();
		assertEquals(3, ids.length);
		assertEquals(0, ids[0]);
		assertEquals(1, ids[1]);
		assertEquals(2, ids[2]);
		//TODO: write all possible assertions
	}

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

