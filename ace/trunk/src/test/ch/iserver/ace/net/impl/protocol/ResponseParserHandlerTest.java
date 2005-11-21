package ch.iserver.ace.net.impl.protocol;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

public class ResponseParserHandlerTest extends TestCase {

	
	public void testPublishedDocuments() throws Exception {
		ResponseParserHandler handler = new ResponseParserHandler();
		Deserializer deserializer = DeserializerImpl.getInstance();
		
		byte[] data = PUBLISHED_DOCUMENTS.getBytes(NetworkConstants.DEFAULT_ENCODING);
		String userId = "asdf-aa234";
		QueryInfo qInfo = new QueryInfo(userId, ProtocolConstants.PUBLISHED_DOCUMENTS);
		handler.setMetaData(qInfo);
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		
		assertEquals(result.getType(), ProtocolConstants.PUBLISHED_DOCUMENTS);
		
		Map docs = new HashMap();
		docs.put("WERS24-RE2", "testfile.txt");
		docs.put("23SSWD-3ED", "meeting2.txt");
		docs.put("ADSFBW-45S", "notes232.txt");
		List payload = (List) result.getPayload();
		Iterator iter = payload.iterator();
		while (iter.hasNext()) {
			DocumentInfo info = (DocumentInfo) iter.next();
			assertTrue(docs.containsKey(info.getDocId()));
			assertTrue(docs.containsValue(info.getName()));
			assertEquals(userId, info.getUserId());
			
		}
	}

	private static final String PUBLISHED_DOCUMENTS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><response>" +
	"<publishedDocs>" +
	"<doc name=\"testfile.txt\" id=\"WERS24-RE2\" />" +
	"<doc name=\"meeting2.txt\" id=\"ADSFBW-45S\" />" +
	"<doc name=\"notes232.txt\" id=\"23SSWD-3ED\" />" +
	"</publishedDocs>" +
	"</response></ace>";
	
}

