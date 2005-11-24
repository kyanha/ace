package ch.iserver.ace.net.impl.protocol;

import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;
import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.impl.FragmentImpl;
import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.PortableDocumentExt;
import ch.iserver.ace.net.impl.PortableDocumentImpl;
import ch.iserver.ace.net.impl.PublishedDocument;
import ch.iserver.ace.net.impl.RemoteUserProxyImpl;

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
		
		byte[] data = serializer.createResponse(ProtocolConstants.PUBLISHED_DOCUMENTS, docs, null);
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
	
	public void testCreateRequestForJoin() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		String userId = "asdfadsf-23";
		NetworkServiceImpl.getInstance().setUserId(userId);
		
		String docId = "doc-id-234b";
		
		byte[] data = serializer.createRequest(ProtocolConstants.JOIN, docId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertEquals(XML_JOIN, actual);
	}
	
	public void testCreateResponseForJoin() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		String userId = "adfasdf-21";
		NetworkServiceImpl service = NetworkServiceImpl.getInstance();
		service.setUserId(userId);
		service.setServerInfo(new ServerInfo(InetAddress.getByName("254.23.12.98"), 4123));
		service.setUserDetails(new UserDetails("John Huderi"));
		
		String docId = "ASDF-23";
		
		PortableDocumentExt document = new PortableDocumentImpl();
		document.addParticipant(ParticipantConnection.PUBLISHER_ID, null);
		document.addParticipant(1, new RemoteUserProxyImpl("sadfasd-24", 
				new MutableUserDetails("Jimmy Ritter", InetAddress.getByName("123.43.45.21"), 4123)));
		document.addParticipant(2, new RemoteUserProxyImpl("cbvncvvc-24", 
				new MutableUserDetails("Samuel Fuchs", InetAddress.getByName("123.43.12.197"), 4123)));
		document.setSelection(0, new CaretUpdate(0, 0));
		document.setSelection(1, new CaretUpdate(456, 456));
		document.setSelection(2, new CaretUpdate(7, 7));
		Fragment fragment = new FragmentImpl(ParticipantConnection.PUBLISHER_ID, "Los gehts: ");
		document.addFragment(fragment);
		fragment = new FragmentImpl(1, "ich habe durst.");
		document.addFragment(fragment);
		fragment = new FragmentImpl(2, " das sagst du mir?");
		document.addFragment(fragment);
		fragment = new FragmentImpl(1, " dir sage ich alles!");
		document.addFragment(fragment);
		
		PublishedDocument doc = new PublishedDocument(docId, null, null, null, null);
		
		byte [] data = serializer.createResponse(ProtocolConstants.JOIN_DOCUMENT, doc, document);
		
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		assertEquals(XML_JOIN_DOCUMENT, actual);
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
	
	private static final String XML_JOIN = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><request>" +
			"<join userid=\"asdfadsf-23\">" +
			"<doc id=\"doc-id-234b\"/>" +
			"</join>" +
			"</request></ace>";
	
	private static final String XML_JOIN_DOCUMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
