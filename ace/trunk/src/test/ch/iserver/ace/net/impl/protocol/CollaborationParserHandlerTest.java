package ch.iserver.ace.net.impl.protocol;

import org.apache.log4j.Logger;

import junit.framework.TestCase;
import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Operation;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.jupiter.JupiterTimestampFactory;
import ch.iserver.ace.net.impl.NetworkConstants;
import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.PortableDocumentExt;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;
import ch.iserver.ace.text.InsertOperation;
import ch.iserver.ace.util.Base64;

public class CollaborationParserHandlerTest extends TestCase {

	private static Logger LOG = Logger.getLogger(CollaborationParserHandlerTest.class);
	
	public void testParseLeaveMessage() throws Exception {
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		
		deserializer.deserialize(XML_LEAVE.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING)), handler);
		
		Request result = handler.getResult();
		
		assertEquals(ProtocolConstants.LEAVE, result.getType());
		DocumentInfo info = (DocumentInfo) result.getPayload();
		assertEquals("dic-1231", info.getDocId());
		assertEquals(13, info.getParticipantId());
	}
	
	public void testParseKickedMessage() throws Exception {
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		
		deserializer.deserialize(XML_KICKED.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING)), handler);
		
		Request result = handler.getResult();
		
		assertEquals(ProtocolConstants.KICKED, result.getType());
		DocumentInfo info = (DocumentInfo) result.getPayload();
		assertEquals("sdaf-2", info.getDocId());
	}
	
	public void testJoinDocument() throws Exception {
		CollaborationParserHandler handler = new CollaborationParserHandler();
		Deserializer deserializer = DeserializerImpl.getInstance();
		
		String joinDocument = XML_JOIN_DOCUMENT_1;
		String payload = "0 11 Los gehts:  1 15 ich habe durst. 2 18  das sagst du mir? 1 20  dir sage ich alles!";
		joinDocument += Base64.encodeBytes(payload.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING)), Base64.GZIP);
		joinDocument += XML_JOIN_DOCUMENT_2;
		
		byte[] data = joinDocument.getBytes(NetworkConstants.DEFAULT_ENCODING);
		String userId = "sadfasd-24";
		NetworkServiceImpl.getInstance().setUserId(userId);
		
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		
		assertEquals(result.getType(), ProtocolConstants.JOIN_DOCUMENT);
		PortableDocumentExt doc = (PortableDocumentExt) result.getPayload();
		assertEquals(3, doc.getParticipantId());
		assertEquals("ASDF-23", doc.getDocumentId());
		assertEquals("adfasdf-21", doc.getPublisherId());
		int[] ids = doc.getParticipantIds();
		assertEquals(3, ids.length);
		assertEquals(0, ids[0]);
		assertEquals(1, ids[1]);
		assertEquals(2, ids[2]);
		//TODO: write all possible assertions
	}
	
	public void testReceiveInsertRequest() throws Exception {
		LOG.debug("--> testReceiveInsertRequest()");
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();
		
		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {2,3});
		InsertOperation insert = new InsertOperation(23, "test-text.", 12);
		InsertOperation original = new InsertOperation(17, "original text", 10);
		insert.setOriginalOperation(original);
		ch.iserver.ace.algorithm.Request request = new ch.iserver.ace.algorithm.RequestImpl(1, timestamp, insert);
		String participantId = "7";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		assertEquals(ProtocolConstants.REQUEST, result.getType());
		ch.iserver.ace.algorithm.Request algoRequest = (ch.iserver.ace.algorithm.Request) result.getPayload();
		assertEquals(1, algoRequest.getSiteId());
		Operation operation = algoRequest.getOperation();
		assertTrue(operation instanceof InsertOperation);
		assertTrue(operation.getOriginalOperation() instanceof InsertOperation);
		int[] com = algoRequest.getTimestamp().getComponents();
		assertEquals(2, com[0]);
		assertEquals(3, com[1]);
		LOG.debug("<-- testReceiveInsertRequest()");
	}
	
	public void testReceiveCaretUpdateMessage() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();
		
		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {354,678});
		CaretUpdate update = new CaretUpdate(34, 311);
		int siteId = 0;
		CaretUpdateMessage caretMsg = new CaretUpdateMessage(siteId, timestamp, update);
		String participantId = "7";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.CARET_UPDATE, caretMsg, participantId);
		
		assertEquals(XML_CARETUPDATE, (new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING))));
		
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		
		assertEquals(ProtocolConstants.CARET_UPDATE, result.getType());
		assertEquals(participantId, result.getUserId());
		CaretUpdateMessage message = (CaretUpdateMessage) result.getPayload();
		assertEquals(siteId, message.getSiteId());
		int[] coms = message.getTimestamp().getComponents();
		assertEquals(354, coms[0]);
		assertEquals(678, coms[1]);
		assertEquals(34, message.getUpdate().getDot());
		assertEquals(311, message.getUpdate().getMark());
	}

	private static final String XML_CARETUPDATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><session>" +
	"<caretUpdate siteId=\"0\" participantId=\"7\"><timestamp>354 678 </timestamp><caret dot=\"34\" mark=\"311\"/></caretUpdate>" +
	"</session></ace>";
	
	private static final String XML_LEAVE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><notification>" +
			"<leave docId=\"dic-1231\" participantId=\"13\"/>" +
			"</notification></ace>";
	
	private static final String XML_KICKED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<ace><notification>" +
		"<kicked docId=\"sdaf-2\"/>" +
		"</notification></ace>";
	
	public static final String XML_JOIN_DOCUMENT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<ace><response>" +
		"<document id=\"ASDF-23\" userid=\"adfasdf-21\" participantId=\"3\">" +
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
		"<data>";
	
	public static final String XML_JOIN_DOCUMENT_2 = "</data>" +
		"</document>" +
		"</response></ace>";	
}
