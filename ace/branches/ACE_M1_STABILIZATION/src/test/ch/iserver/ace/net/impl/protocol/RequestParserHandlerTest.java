package ch.iserver.ace.net.impl.protocol;

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
	
	private static final String QUERY = "<ace><request><query type=\"docs\"/></request></ace>";
	
	private RequestParserHandler handler;
	private Deserializer deserializer;
	
	public void setUp() {
		handler = new RequestParserHandler();
		deserializer = DeserializerImpl.getInstance();	
	}
	

	public void testPublishDocument() throws Exception {
		deserializer.deserialize(PUBLISH.getBytes(NetworkConstants.DEFAULT_ENCODING), handler);
		
		Request request = (Request) handler.getResult();
		assertEquals(ProtocolConstants.PUBLISH, request.getType());
		DocumentInfo info = (DocumentInfo) request.getPayload();
		assertEquals(info.getDocId(), "WERS24-RE2");
		assertEquals(info.getName(), "collab.txt");
		assertEquals(info.getUserId(), "asdf-w2");
	}
	
	public void testConcealDocument() throws Exception {
		deserializer.deserialize(CONCEAL.getBytes(NetworkConstants.DEFAULT_ENCODING), handler);
		
		Request request = (Request) handler.getResult();
		assertEquals(ProtocolConstants.CONCEAL, request.getType());
		DocumentInfo info = (DocumentInfo) request.getPayload();
		assertEquals(info.getDocId(), "WERS24-RE2");
		assertEquals(info.getUserId(), "asdf-w2");
	}
	
	public void testQueryDocuments() throws Exception {
		deserializer.deserialize(QUERY.getBytes(NetworkConstants.DEFAULT_ENCODING), handler);
		
		Request request = (Request) handler.getResult();
		assertEquals(ProtocolConstants.PUBLISHED_DOCUMENTS, request.getType());
	}
	
}
