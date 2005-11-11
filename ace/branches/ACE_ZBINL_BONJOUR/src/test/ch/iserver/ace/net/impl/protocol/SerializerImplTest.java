package ch.iserver.ace.net.impl.protocol;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;
import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.PublishedDocument;

public class SerializerImplTest extends TestCase {

	private static final String EXPECTED_QUERY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ace><request><query type=\"docs\"/></request></ace>";
	
	private static final String EXPECTED_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><response>" +
	"<publishedDocs>" +
	"<doc id=\"WERS24-RE2\" name=\"testfile.txt\"/>" +
	"<doc id=\"ADSFBW-45S\" name=\"meeting2.txt\"/>" +
	"<doc id=\"23SSWD-3ED\" name=\"notes232.txt\"/>" +
	"</publishedDocs>" +
	"</response></ace>";
	
	public void testCreateQueryForPublishedDocuments() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		
		byte[] data = serializer.createQuery(ProtocolConstants.PUBLISHED_DOCUMENTS);
		String actual = new String(data, NetworkConstants.DEFAULT_ENCODING);
		
		assertEquals(EXPECTED_QUERY, actual);
	}
	
	public void testCreateResponseForPublishedDocuments() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		
		List docs = new ArrayList();
		PublishedDocument doc = new PublishedDocument("WERS24-RE2", null, new DocumentDetails("testfile.txt"));
		docs.add(doc);
		doc = new PublishedDocument("ADSFBW-45S", null, new DocumentDetails("meeting2.txt"));
		docs.add(doc);
		doc = new PublishedDocument("23SSWD-3ED", null, new DocumentDetails("notes232.txt"));
		docs.add(doc);
		
		byte[] data = serializer.createResponse(ProtocolConstants.PUBLISHED_DOCUMENTS, docs);
		String actual = new String(data, NetworkConstants.DEFAULT_ENCODING);
		
		assertEquals(EXPECTED_RESPONSE, actual);
	}
	
	public void testCreateNotification() throws Exception {
		fail("to be implemented");
	}
	
}
