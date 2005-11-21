package ch.iserver.ace.net.impl.protocol;

import java.util.List;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

public class RequestParserHandlerTest extends TestCase {

	
	private static final String PUBLISH = "<ace><notification>" +
			"<publishDocs userid=\"asdf-w2\">" +
			"<doc id=\"WERS24-RE2\" name=\"collab.txt\" />" +
			"</publishDocs>" +
			"</notification></ace>";
	
	private static final String CONCEAL = "<ace><notification>" +
	"<concealDocs userid=\"asdf-w2\">" +
	"<doc id=\"WERS24-RE2\" />" +
	"</concealDocs>" +
	"</notification></ace>";
	
	private static final String SEND_DOCUMENTS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><notification>" +
	"<publishedDocs userid=\"asdfadsf-23\">" +
	"<doc id=\"WERS24-RE2\" name=\"testfile.txt\"/>" +
	"<doc id=\"ADSFBW-45S\" name=\"meeting2.txt\"/>" +
	"<doc id=\"23SSWD-3ED\" name=\"notes232.txt\"/>" +
	"</publishedDocs>" +
	"</notification></ace>";
	
	private static final String QUERY = "<ace><request><query type=\"docs\"/></request></ace>";
	
	private RequestParserHandler handler;
	private Deserializer deserializer;
	
	public void setUp() {
		handler = new RequestParserHandler();
		deserializer = DeserializerImpl.getInstance();	
	}
	

	public void testPublishDocument() throws Exception {
		deserializer.deserialize(PUBLISH.getBytes(NetworkConstants.DEFAULT_ENCODING), handler);
		
		Request request = handler.getResult();
		assertEquals(ProtocolConstants.PUBLISH, request.getType());
		DocumentInfo info = (DocumentInfo) request.getPayload();
		assertEquals(info.getDocId(), "WERS24-RE2");
		assertEquals(info.getName(), "collab.txt");
		assertEquals(info.getUserId(), "asdf-w2");
	}
	
	public void testConcealDocument() throws Exception {
		deserializer.deserialize(CONCEAL.getBytes(NetworkConstants.DEFAULT_ENCODING), handler);
		
		Request request = handler.getResult();
		assertEquals(ProtocolConstants.CONCEAL, request.getType());
		DocumentInfo info = (DocumentInfo) request.getPayload();
		assertEquals(info.getDocId(), "WERS24-RE2");
		assertEquals(info.getUserId(), "asdf-w2");
	}
	
	public void testQueryDocuments() throws Exception {
		deserializer.deserialize(QUERY.getBytes(NetworkConstants.DEFAULT_ENCODING), handler);
		
		Request request =  handler.getResult();
		assertEquals(ProtocolConstants.PUBLISHED_DOCUMENTS, request.getType());
	}
	
	public void testSendDocuments() throws Exception {
		deserializer.deserialize(SEND_DOCUMENTS.getBytes(NetworkConstants.DEFAULT_ENCODING), handler);
		
		Request request = handler.getResult();
		assertEquals(ProtocolConstants.SEND_DOCUMENTS, request.getType());
		List docs = (List) request.getPayload();
		assertEquals(3, docs.size());
		DocumentInfo info = (DocumentInfo) docs.get(0);
		assertEquals("WERS24-RE2", info.getDocId());
		assertEquals("testfile.txt", info.getName());
		assertEquals("asdfadsf-23", info.getUserId());
		info = (DocumentInfo) docs.get(1);
		assertEquals("ADSFBW-45S", info.getDocId());
		assertEquals("meeting2.txt", info.getName());
		assertEquals("asdfadsf-23", info.getUserId());
		info = (DocumentInfo) docs.get(2);
		assertEquals("23SSWD-3ED", info.getDocId());
		assertEquals("notes232.txt", info.getName());
		assertEquals("asdfadsf-23", info.getUserId());
	}
	
}
