package ch.iserver.ace.net.impl.protocol;

import java.util.LinkedHashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.easymock.MockControl;

import ch.iserver.ace.DocumentDetails;
import ch.iserver.ace.net.DocumentServerLogic;
import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.PublishedDocument;

public class SerializerImplTest extends TestCase {
	
	private Logger LOG = Logger.getLogger(SerializerImplTest.class);
	private DocumentServerLogic logic;
	
	protected void setUp() throws Exception {
		MockControl docServerLogicCtrl = MockControl.createControl(DocumentServerLogic.class);
		logic = (DocumentServerLogic) docServerLogicCtrl.getMock();
		docServerLogicCtrl.replay();
	}
	
	//PUBLISHED_DOCUMENT response discarded
//	public void testCreateResponseForPublishedDocuments() throws Exception {
//		Serializer serializer = SerializerImpl.getInstance();
//		
//		Map docs = new LinkedHashMap();
//		PublishedDocument doc = new PublishedDocument("WERS24-RE2", logic, new DocumentDetails("testfile.txt"), null, null);
//		docs.put("WERS24-RE2", doc);
//		doc = new PublishedDocument("ADSFBW-45S", logic, new DocumentDetails("meeting2.txt"), null, null);
//		docs.put("ADSFBW-45S",doc);
//		doc = new PublishedDocument("23SSWD-3ED", logic, new DocumentDetails("notes232.txt"), null, null);
//		docs.put("23SSWD-3ED", doc);
//		
//		byte[] data = serializer.createResponse(ProtocolConstants.PUBLISHED_DOCUMENTS, docs, null);
//		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
//		
//		assertEquals(EXPECTED_RESPONSE, actual);
//	}
	
	public void testCreateNotificationPublish() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		String userId = "asdfadsf-23";
		NetworkServiceImpl.getInstance().setUserId(userId);
		PublishedDocument doc = new PublishedDocument("WERS24-RE2", logic, new DocumentDetails("testfile.txt"), null, null);
		
		byte[] data = serializer.createNotification(ProtocolConstants.PUBLISH, doc);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		String xmlPublish = XML_PUBLISH_1+userId+XML_PUBLISH_2;
		
		assertEquals(xmlPublish, actual);
		
	}
	
	public void testCreateNotificationConceal() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		String userId = "asdfadsf-23";
		NetworkServiceImpl.getInstance().setUserId(userId);
		PublishedDocument doc = new PublishedDocument("WERS24-RE2", logic, new DocumentDetails("testfile.txt"), null, null);
		
		byte[] data = serializer.createNotification(ProtocolConstants.CONCEAL, doc);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		String xmlPublish = XML_CONCEAL_1+userId+XML_CONCEAL_2;
		
		assertEquals(xmlPublish, actual);
	}
	
	public void testCreateNotificationSendDocuments() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		String userId = "asdfadsf-23";
		NetworkServiceImpl.getInstance().setUserId(userId);
		
		Map docs = new LinkedHashMap();
		PublishedDocument doc = new PublishedDocument("WERS24-RE2", logic, new DocumentDetails("testfile.txt"), null, null);
		docs.put("WERS24-RE2", doc);
		doc = new PublishedDocument("ADSFBW-45S", logic, new DocumentDetails("meeting2.txt"), null, null);
		docs.put("ADSFBW-45S",doc);
		doc = new PublishedDocument("23SSWD-3ED", logic, new DocumentDetails("notes232.txt"), null, null);
		docs.put("23SSWD-3ED", doc);
		
		byte[] data = serializer.createNotification(ProtocolConstants.SEND_DOCUMENTS, docs);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertEquals(XML_SEND_DOCUMENTS, actual);
	}
	
	public void testCreateRequestForJoin() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		String userId = "asdfadsf-23";
		NetworkServiceImpl.getInstance().setUserId(userId);
		
		String docId = "doc-id-234b";
		
//		LOG.debug("--> testCreateRequestForJoin()");
		byte[] data = serializer.createRequest(ProtocolConstants.JOIN, docId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertEquals(XML_JOIN, actual);
	}
	
	public void testCreateRequestInvite() throws Exception {
		Serializer serializer = SerializerImpl.getInstance();
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		
		String docId = "doc-id-234b";
		
//		LOG.debug("--> testCreateRequestInvite()");
		MockControl serverCtrl = MockControl.createControl(DocumentServerLogic.class);
		DocumentServerLogic logic = (DocumentServerLogic) serverCtrl.getMock();
		PublishedDocument doc = new PublishedDocument(docId, logic, null, null, null);
		byte[] data = serializer.createRequest(ProtocolConstants.INVITE, docId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertEquals(XML_INVITE, actual);
	}
	
	public void testCreateResponseInviteRejected() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = SerializerImpl.getInstance();
		
		String docId = "doc-id-234b";
		
		byte[] data = serializer.createResponse(ProtocolConstants.INVITE_REJECTED, docId, null);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertEquals(XML_INVITE_REJECTED, actual);
	}
	
	public void testCreateResponseJoinRejected() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = SerializerImpl.getInstance();
		
		String docId = "doc-id-234b";
		
		byte[] data = serializer.createResponse(ProtocolConstants.JOIN_REJECTED, docId, "501");
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertEquals(XML_JOIN_REJECTED, actual);
	}
	
	
	private static final String XML_JOIN_REJECTED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><response>" +
	"<joinRejected docId=\"doc-id-234b\" userid=\"vnmv-qqw2345\">" +
	"<reason code=\"501\"/>" +
	"</joinRejected>" +
	"</response></ace>";
	
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
	
	private static final String XML_INVITE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><request>" +
	"<invite userid=\"vnmv-qqw2345\">" +
	"<doc id=\"doc-id-234b\"/>" +
	"</invite>" +
	"</request></ace>";	
	
	private static final String XML_INVITE_REJECTED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><response>" +
	"<inviteRejected docId=\"doc-id-234b\" userid=\"vnmv-qqw2345\"/>" +
	"</response></ace>";
	

}
