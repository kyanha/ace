package ch.iserver.ace.net.impl.protocol;

import java.util.Map;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.NetworkConstants;

public class DeserializerImplTest extends TestCase {

	private static final String MESSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><response>" +
	"<publishedDocs>" +
	"<doc name=\"testfile.txt\" id=\"WERS24-RE2\" />" +
	"<doc name=\"meeting2.txt\" id=\"ADSFBW-45S\" />" +
	"<doc name=\"notes232.txt\" id=\"23SSWD-3ED\" />" +
	"</publishedDocs>" +
	"</response></ace>";
	
	public void testDeserializeDocuments() throws Exception {
		
		ResponseParserHandler handler = new ResponseParserHandler();
		Deserializer deserializer = DeserializerImpl.getInstance();
		
		byte[] data = MESSAGE.getBytes(NetworkConstants.DEFAULT_ENCODING);
		deserializer.deserialize(data, handler);
		Map result = (Map)handler.getResult();
		
		assertEquals(result.size(), 3);
		assertTrue(result.containsKey("WERS24-RE2"));
		assertTrue(result.containsKey("23SSWD-3ED"));
		assertTrue(result.containsKey("ADSFBW-45S"));
		assertTrue(result.containsValue("testfile.txt"));
		assertTrue(result.containsValue("meeting2.txt"));
		assertTrue(result.containsValue("notes232.txt"));
		
	}
	
}
