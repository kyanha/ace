package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.NetworkConstants;

public class DeserializerImplTest extends TestCase {

	private static final String PUBLISHED_DOCUMENTS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><response>" +
	"<publishedDocs>" +
	"<doc name=\"testfile.txt\" id=\"WERS24-RE2\" />" +
	"<doc name=\"meeting2.txt\" id=\"ADSFBW-45S\" />" +
	"<doc name=\"notes232.txt\" id=\"23SSWD-3ED\" />" +
	"</publishedDocs>" +
	"</response></ace>";

	
	public void testDeserializePublishedDocumentsRequest() throws Exception {
		
		ResponseParserHandler handler = new ResponseParserHandler();
		Deserializer deserializer = DeserializerImpl.getInstance();
		
		byte[] data = PUBLISHED_DOCUMENTS.getBytes(NetworkConstants.DEFAULT_ENCODING);
		deserializer.deserialize(data, handler);
		Request result = (Request) handler.getResult();
		
		//verify that the handler was actually used
		assertNotNull(result);
		assertEquals(ProtocolConstants.PUBLISHED_DOCUMENTS, handler.getType());
		assertEquals(ProtocolConstants.PUBLISHED_DOCUMENTS, result.getType());
		assertNotNull(result.getPayload());
	}
	
}
