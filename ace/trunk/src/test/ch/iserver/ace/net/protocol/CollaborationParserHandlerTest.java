package ch.iserver.ace.net.protocol;

import java.net.InetAddress;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Operation;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.jupiter.JupiterTimestampFactory;
import ch.iserver.ace.algorithm.text.DeleteOperation;
import ch.iserver.ace.algorithm.text.InsertOperation;
import ch.iserver.ace.algorithm.text.NoOperation;
import ch.iserver.ace.algorithm.text.SplitOperation;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.PortableDocumentExt;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyImpl;
import ch.iserver.ace.net.protocol.RequestImpl.DocumentInfo;
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
		
		byte[] data = joinDocument.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
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
		ch.iserver.ace.algorithm.Request request = new ch.iserver.ace.algorithm.RequestImpl(1, timestamp, insert);
		String participantId = "7";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		
		final int TIMES = 10;
		for (int i = 0; i < TIMES; i++) {
			deserializer.deserialize(data, handler);
			Request result = handler.getResult();
			assertEquals(ProtocolConstants.REQUEST, result.getType());
			ch.iserver.ace.algorithm.Request algoRequest = (ch.iserver.ace.algorithm.Request) result.getPayload();
			assertEquals(1, algoRequest.getSiteId());
			Operation operation = algoRequest.getOperation();
			assertTrue(operation instanceof InsertOperation);
			int[] com = algoRequest.getTimestamp().getComponents();
			assertEquals(2, com[0]);
			assertEquals(3, com[1]);
		}
		
		LOG.debug("<-- testReceiveInsertRequest()");
	}
	
	public void testReceiveDeleteRequest() throws Exception {
		LOG.debug("--> testReceiveDeleteRequest()");
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();
		
		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {2,3});
		DeleteOperation delete = new DeleteOperation(23, "test-text.");

		ch.iserver.ace.algorithm.Request request = new ch.iserver.ace.algorithm.RequestImpl(1, timestamp, delete);
		String participantId = "7";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		assertEquals(XML_REQUEST_DELETE, actual);
		
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		
		final int TIMES = 1;
		for (int i = 0; i < TIMES; i++) {
			deserializer.deserialize(data, handler);
			Request result = handler.getResult();
			assertEquals(ProtocolConstants.REQUEST, result.getType());
			ch.iserver.ace.algorithm.Request algoRequest = (ch.iserver.ace.algorithm.Request) result.getPayload();
			assertEquals(1, algoRequest.getSiteId());
			Operation operation = algoRequest.getOperation();
			assertTrue(operation instanceof DeleteOperation);
			int[] com = algoRequest.getTimestamp().getComponents();
			assertEquals(2, com[0]);
			assertEquals(3, com[1]);
		}
		LOG.debug("<-- testReceiveDeleteRequest()");
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
	
	public void testReceiveParticipantJoinedMessage() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();

		String participantId = "1234";
		
		RemoteUserProxyExt origProxy = new RemoteUserProxyImpl("sadfasd-24", new MutableUserDetails("Jimmy Ritter", InetAddress.getByName("123.43.45.21"), 4123));
		origProxy.setDNSSDdiscovered(true);
		byte[] data = serializer.createSessionMessage(ProtocolConstants.PARTICIPANT_JOINED, origProxy, participantId);
		
		assertEquals(XML_PARTICIPANT_JOINED, (new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING))));
		
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		
		assertEquals(ProtocolConstants.PARTICIPANT_JOINED, result.getType());
		assertEquals(participantId, result.getUserId());
		RemoteUserProxyExt proxy = (RemoteUserProxyExt) result.getPayload();
		
		assertEquals("sadfasd-24", proxy.getId());
		MutableUserDetails details = proxy.getMutableUserDetails();
		assertEquals("Jimmy Ritter", details.getUsername());
		assertEquals("123.43.45.21", details.getAddress().getHostAddress());
		assertEquals(4123, details.getPort());
		assertFalse(proxy.isDNSSDdiscovered());
	}
	
	public void testReceiveParticipantLeftMessage() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();

		String participantId = "1234";
		String reason = "2323";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.PARTICIPANT_LEFT, reason, participantId);
		
		assertEquals(XML_PARTICIPANT_LEFT, (new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING))));
		
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		
		assertEquals(ProtocolConstants.PARTICIPANT_LEFT, result.getType());
		assertEquals(participantId, result.getUserId());
		String finalReason = (String) result.getPayload();
		assertEquals("2323", finalReason);
	}
	
	public void testReceiveAcknowledgeMessage() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();

		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {354,678});
		String siteId = "23421234123223";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.ACKNOWLEDGE, timestamp, siteId);
		
		assertEquals(XML_ACKNOWLEDGE, (new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING))));
		
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		deserializer.deserialize(data, handler);
		
		Request result = handler.getResult();
		assertEquals(ProtocolConstants.ACKNOWLEDGE, result.getType());
		assertEquals(siteId, result.getUserId());
		Timestamp finalTimestamp = (Timestamp) result.getPayload();
		assertEquals(timestamp, finalTimestamp);
	}
	
	public void testReceiveSplitOperationRequest() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();

		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {4,5});
		
		InsertOperation first = new InsertOperation(172, "first text", 56);
		DeleteOperation second = new DeleteOperation(123, "test-text.");
		SplitOperation split = new SplitOperation(first, second);
		ch.iserver.ace.algorithm.Request request = new ch.iserver.ace.algorithm.RequestImpl(0, timestamp, split);
		String participantId = "4";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		assertEquals(XML_SESSION_MESSAGE_REQUEST_SPLIT, actual);
		
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		
		final int TIMES = 1;
		for (int i = 0; i < TIMES; i++) {
			deserializer.deserialize(data, handler);
			Request result = handler.getResult();
			assertEquals(ProtocolConstants.REQUEST, result.getType());
			ch.iserver.ace.algorithm.Request algoRequest = (ch.iserver.ace.algorithm.Request) result.getPayload();
			assertEquals(0, algoRequest.getSiteId());
			Operation operation = algoRequest.getOperation();
			assertTrue(operation instanceof SplitOperation);
			SplitOperation finalSplit = (SplitOperation) operation;
			assertTrue(finalSplit.getFirst() instanceof InsertOperation);
			assertTrue(finalSplit.getSecond() instanceof DeleteOperation);
			int[] com = algoRequest.getTimestamp().getComponents();
			assertEquals(4, com[0]);
			assertEquals(5, com[1]);
		}
		
	}
	
	public void testReceiveNOOPMessage() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();

		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {2,3});
		NoOperation noop = new NoOperation();
		ch.iserver.ace.algorithm.Request request = new ch.iserver.ace.algorithm.RequestImpl(1, timestamp, noop);
		String participantId = "4";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertEquals(XML_NOOP_EMPTY, actual);
		
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		
		final int TIMES = 1;
		for (int i = 0; i < TIMES; i++) {
			deserializer.deserialize(data, handler);
			Request result = handler.getResult();
			assertEquals(ProtocolConstants.REQUEST, result.getType());
			ch.iserver.ace.algorithm.Request algoRequest = (ch.iserver.ace.algorithm.Request) result.getPayload();
			assertEquals(1, algoRequest.getSiteId());
			int[] com = algoRequest.getTimestamp().getComponents();
			assertEquals(2, com[0]);
			assertEquals(3, com[1]);
			assertTrue(algoRequest.getOperation() instanceof NoOperation);
		}
	}
	
	public void testReceiveNOOPComplexMessage() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();

		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {2,3});
				
		NoOperation noop = new NoOperation();
		ch.iserver.ace.algorithm.Request request = new ch.iserver.ace.algorithm.RequestImpl(1, timestamp, noop);
		String participantId = "4";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertEquals(XML_NOOP_COMPLEX, actual);
		
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		
		final int TIMES = 1;
		for (int i = 0; i < TIMES; i++) {
			deserializer.deserialize(data, handler);
			Request result = handler.getResult();
			assertEquals(ProtocolConstants.REQUEST, result.getType());
			ch.iserver.ace.algorithm.Request algoRequest = (ch.iserver.ace.algorithm.Request) result.getPayload();
			assertEquals(1, algoRequest.getSiteId());
			int[] com = algoRequest.getTimestamp().getComponents();
			assertEquals(2, com[0]);
			assertEquals(3, com[1]);
			assertTrue(algoRequest.getOperation() instanceof NoOperation);
		}
	}
	
	public void testReceiveSessionTermiantedMessage() throws Exception {
		CollaborationDeserializer deserializer = new CollaborationDeserializer();
		CollaborationParserHandler handler = new CollaborationParserHandler();
		handler.setTimestampFactory(new JupiterTimestampFactory());
		
		byte[] data = XML_SESSION_TERMINATED.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		assertEquals(ProtocolConstants.SESSION_TERMINATED, result.getType());
	}
	
	private static final String NEWLINE = System.getProperty("line.separator");

	private static final String XML_SESSION_TERMINATED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<sessionTerminated/>" +
	"</session></ace>"; 
	
	private static final String XML_NOOP_COMPLEX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<request siteId=\"1\" participantId=\"4\">" +
	"<operation>" +
	"<noop/>" +
	"</operation>" +
	"<timestamp>2 3 </timestamp>" +
	"</request>" +
	"</session></ace>";
	
	private static final String XML_NOOP_EMPTY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<request siteId=\"1\" participantId=\"4\">" +
	"<operation>" +
	"<noop/>" +
	"</operation>" +
	"<timestamp>2 3 </timestamp>" +
	"</request>" +
	"</session></ace>";
	
	private static final String XML_SESSION_MESSAGE_REQUEST_SPLIT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<request siteId=\"0\" participantId=\"4\">" +
	"<operation>" +
	"<split><first><insert position=\"172\" origin=\"56\"><text>first text</text></insert></first>" +
	"<second><delete position=\"123\">" +
		"<text>test-text.</text>" +
	"</delete></second>" +
	"</split>" +
	"</operation>" +
	"<timestamp>4 5 </timestamp>" +
	"</request>" +
	"</session></ace>";
	
	private static final String XML_ACKNOWLEDGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<ack siteId=\"23421234123223\">" +
	"<timestamp>354 678 </timestamp>" +
	"</ack>" +
	"</session></ace>";
	
	private static final String XML_PARTICIPANT_LEFT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<pLeft id=\"1234\">" +
	"<reason code=\"2323\"/>" +
	"</pLeft>" +
	"</session></ace>";	
	
	private static final String XML_PARTICIPANT_JOINED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<pJoined id=\"1234\">" +
	"<user id=\"sadfasd-24\" name=\"Jimmy Ritter\" address=\"123.43.45.21\" port=\"4123\" dnssdDiscovered=\"true\"/>" +
	"</pJoined>" +
	"</session></ace>";	
	
	private static final String XML_REQUEST_DELETE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<request siteId=\"1\" participantId=\"7\">" +
	"<operation>" +
	"<delete position=\"23\">" +
		"<text>test-text.</text>" +
	"</delete>" +
	"</operation>" +
	"<timestamp>2 3 </timestamp>" +
	"</request>" +
	"</session></ace>";

	private static final String XML_CARETUPDATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<caretUpdate siteId=\"0\" participantId=\"7\"><timestamp>354 678 </timestamp><caret dot=\"34\" mark=\"311\"/></caretUpdate>" +
	"</session></ace>";
	
	private static final String XML_LEAVE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
			"<ace><notification>" +
			"<leave docId=\"dic-1231\" participantId=\"13\"/>" +
			"</notification></ace>";
	
	private static final String XML_KICKED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
		"<ace><notification>" +
		"<kicked docId=\"sdaf-2\"/>" +
		"</notification></ace>";
	
	public static final String XML_JOIN_DOCUMENT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
		"<ace><response>" +
		"<document id=\"ASDF-23\" userid=\"adfasdf-21\" participantId=\"3\">" +
		"<participants>" +
		"<participant id=\"0\">" +
		"<user id=\"adfasdf-21\" name=\"John Huderi\" address=\"254.23.12.98\" port=\"4123\" dnssdDiscovered=\"true\"/>" +
		"<selection mark=\"0\" dot=\"0\"/>" +
		"</participant>" +
		"<participant id=\"1\">" +
		"<user id=\"sadfasd-24\" name=\"Jimmy Ritter\" address=\"123.43.45.21\" port=\"4123\" dnssdDiscovered=\"true\"/>" +
		"<selection mark=\"456\" dot=\"456\"/>" +
		"</participant>" +
		"<participant id=\"2\">" +
		"<user id=\"cbvncvvc-24\" name=\"Samuel Fuchs\" address=\"123.43.12.197\" port=\"4123\" dnssdDiscovered=\"true\"/>" +
		"<selection mark=\"7\" dot=\"7\"/>" +
		"</participant>" +
		"</participants>" +
		"<data>";
	
	public static final String XML_JOIN_DOCUMENT_2 = "</data>" +
		"</document>" +
		"</response></ace>";	
}
