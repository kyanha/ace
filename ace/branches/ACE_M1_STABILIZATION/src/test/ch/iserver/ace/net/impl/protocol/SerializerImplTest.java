package ch.iserver.ace.net.impl.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.PublishedDocument;

public class SerializerImplTest extends TestCase {
	
	public void testCreateQueryForPublishedDocuments() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		
		byte[] data = serializer.createQuery(ProtocolConstants.PUBLISHED_DOCUMENTS);
		String actual = new String(data, NetworkConstants.DEFAULT_ENCODING);
		
		assertEquals(EXPECTED_QUERY, actual);
	}
	
	public void testCreateResponseForPublishedDocuments() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		
		Map docs = new LinkedHashMap();
		PublishedDocument doc = new PublishedDocument("WERS24-RE2", null, new DocumentDetails("testfile.txt"), null, null);
		docs.put("WERS24-RE2", doc);
		doc = new PublishedDocument("ADSFBW-45S", null, new DocumentDetails("meeting2.txt"), null, null);
		docs.put("ADSFBW-45S",doc);
		doc = new PublishedDocument("23SSWD-3ED", null, new DocumentDetails("notes232.txt"), null, null);
		docs.put("23SSWD-3ED", doc);
		
		byte[] data = serializer.createResponse(ProtocolConstants.PUBLISHED_DOCUMENTS, docs);
		String actual = new String(data, NetworkConstants.DEFAULT_ENCODING);
		
		assertEquals(EXPECTED_RESPONSE, actual);
	}
	
	public void testCreateNotificationPublish() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		String userId = "asdfadsf-23";
		NetworkServiceImpl.getInstance().setUserId(userId);
		PublishedDocument doc = new PublishedDocument("WERS24-RE2", null, new DocumentDetails("testfile.txt"), null, null);
		
		byte[] data = serializer.createNotification(ProtocolConstants.PUBLISH, doc);
		String actual = new String(data, NetworkConstants.DEFAULT_ENCODING);

		String xmlPublish = XML_PUBLISH_1+userId+XML_PUBLISH_2;
		
		assertEquals(xmlPublish, actual);
		
	}
	
	public void testCreateNotificationConceal() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		String userId = "asdfadsf-23";
		NetworkServiceImpl.getInstance().setUserId(userId);
		PublishedDocument doc = new PublishedDocument("WERS24-RE2", null, new DocumentDetails("testfile.txt"), null, null);
		
		byte[] data = serializer.createNotification(ProtocolConstants.CONCEAL, doc);
		String actual = new String(data, NetworkConstants.DEFAULT_ENCODING);

		String xmlPublish = XML_CONCEAL_1+userId+XML_CONCEAL_2;
		
		assertEquals(xmlPublish, actual);
	}
	
	public void testCreateNotificationSendDocuments() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		String userId = "asdfadsf-23";
		NetworkServiceImpl.getInstance().setUserId(userId);
		
		Map docs = new LinkedHashMap();
		PublishedDocument doc = new PublishedDocument("WERS24-RE2", null, new DocumentDetails("testfile.txt"), null, null);
		docs.put("WERS24-RE2", doc);
		doc = new PublishedDocument("ADSFBW-45S", null, new DocumentDetails("meeting2.txt"), null, null);
		docs.put("ADSFBW-45S",doc);
		doc = new PublishedDocument("23SSWD-3ED", null, new DocumentDetails("notes232.txt"), null, null);
		docs.put("23SSWD-3ED", doc);
		
		byte[] data = serializer.createNotification(ProtocolConstants.SEND_DOCUMENTS, docs);
		String actual = new String(data, NetworkConstants.DEFAULT_ENCODING);

		assertEquals(XML_SEND_DOCUMENTS, actual);
	}
	
	private static final String EXPECTED_QUERY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><request>" +
			"<query type=\"docs\"/>" +
			"</request></ace>";
	
	private static final String EXPECTED_RESPONSE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><response>" +
			"<publishedDocs>" +
			"<doc id=\"WERS24-RE2\" name=\"testfile.txt\"/>" +
			"<doc id=\"ADSFBW-45S\" name=\"meeting2.txt\"/>" +
			"<doc id=\"23SSWD-3ED\" name=\"notes232.txt\"/>" +
			"</publishedDocs>" +
			"</response></ace>";
	
	private static final String XML_PUBLISH_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><notification>" +
			"<publishDocs userid=\"";
	
	private static final String XML_PUBLISH_2 = "\"><doc id=\"WERS24-RE2\" name=\"testfile.txt\"/>" +
			"</publishDocs>" +
			"</notification></ace>";
	
	private static final String XML_CONCEAL_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><notification>" +
			"<concealDocs userid=\"";
	
	private static final String XML_CONCEAL_2 = "\"><doc id=\"WERS24-RE2\"/>" +
			"</concealDocs>" +
			"</notification></ace>";
	
	private static final String XML_SEND_DOCUMENTS = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><notification>" +
			"<publishedDocs userid=\"asdfadsf-23\">" +
			"<doc id=\"WERS24-RE2\" name=\"testfile.txt\"/>" +
			"<doc id=\"ADSFBW-45S\" name=\"meeting2.txt\"/>" +
			"<doc id=\"23SSWD-3ED\" name=\"notes232.txt\"/>" +
			"</publishedDocs>" +
			"</notification></ace>";
}
