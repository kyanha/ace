package ch.iserver.ace.net.protocol;

import java.net.InetAddress;

import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.XMLTestCase;

import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.algorithm.CaretUpdateMessage;
import ch.iserver.ace.algorithm.Request;
import ch.iserver.ace.algorithm.RequestImpl;
import ch.iserver.ace.algorithm.Timestamp;
import ch.iserver.ace.algorithm.jupiter.JupiterTimestampFactory;
import ch.iserver.ace.algorithm.text.DeleteOperation;
import ch.iserver.ace.algorithm.text.InsertOperation;
import ch.iserver.ace.algorithm.text.NoOperation;
import ch.iserver.ace.algorithm.text.SplitOperation;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.core.FragmentImpl;
import ch.iserver.ace.net.core.MutableUserDetails;
import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.core.PortableDocumentExt;
import ch.iserver.ace.net.core.PortableDocumentImpl;
import ch.iserver.ace.net.core.RemoteUserProxyExt;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.net.core.RemoteUserProxyImpl;
import ch.iserver.ace.net.protocol.RequestImpl.DocumentInfo;

public class CollaborationSerializerTest extends XMLTestCase {

	
	public void testCreateNotificationLeave() throws Exception {
		
		CollaborationSerializer serializer = new CollaborationSerializer();
		
		SessionConnectionImpl conn = new SessionConnectionImpl("dic-1231", null, null, null, "testuser", "testid");
		conn.setParticipantId(13);
		
		byte[] data = serializer.createNotification(ProtocolConstants.LEAVE, conn);
		String actual = new String(data);
		
		assertXMLEqual(XML_LEAVE, actual);
	}
	
	public void testCreateNotificationKicked() throws Exception {
		CollaborationSerializer serializer = new CollaborationSerializer();
		
		String docId = "sdaf-2";
		
		byte[] data = serializer.createNotification(ProtocolConstants.KICKED, docId);
		String actual = new String(data);
		
		assertXMLEqual(XML_KICKED, actual);
	}
	
	public void testCreateResponseForJoin() throws Exception {
		Serializer serializer = new CollaborationSerializer();
		String userId = "adfasdf-21";
		NetworkServiceImpl service = NetworkServiceImpl.getInstance();
		service.setUserId(userId);
		service.setServerInfo(new ServerInfo(InetAddress.getByName("254.23.12.98"), 4123));
		service.setUserDetails(new UserDetails("John Huderi"));
		
		String docId = "ASDF-23";
		
		PortableDocumentExt document = new PortableDocumentImpl();
		document.addParticipant(ParticipantConnection.PUBLISHER_ID, null);
		document.addParticipant(1, RemoteUserProxyFactory.getInstance().createProxy("sadfasd-24", 
				new MutableUserDetails("Jimmy Ritter", InetAddress.getByName("123.43.45.21"), 4123)));
		document.addParticipant(2, RemoteUserProxyFactory.getInstance().createProxy("cbvncvvc-24", 
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
		
		DocumentInfo info = new DocumentInfo(docId, 3);
		byte [] data = serializer.createResponse(ProtocolConstants.JOIN_DOCUMENT, info, document);
		
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		
		assertXMLEqual(XML_JOIN_DOCUMENT_1, actual);
	}
	

	
	public void testCreateSessionMessageOfTypeInsertRequest() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();
		
		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {2,3});
		InsertOperation insert = new InsertOperation(23, "test-text.", 12);
		Request request = new RequestImpl(1, timestamp, insert);
		String participantId = "7";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		
		assertXMLEqual(XML_SESSION_MESSAGE_REQUEST_1, actual);
	}
	
	public void testCreateSessionMessageOfTypeDeleteRequest() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();
		
		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {2,3});
		DeleteOperation delete = new DeleteOperation(123, "test-text.");
		Request request = new RequestImpl(1, timestamp, delete);
		String participantId = "4";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertXMLEqual(XML_SESSION_MESSAGE_REQUEST_2, actual);
	}
	
	public void testCreateSessionMessageOfTypeSplitRequest() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();
		
		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {2,3});
		
		InsertOperation first = new InsertOperation(172, "first text", 56);
		DeleteOperation second = new DeleteOperation(123, "test-text.");
		SplitOperation split = new SplitOperation(first, second);
		Request request = new RequestImpl(1, timestamp, split);
		String participantId = "4";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertXMLEqual(XML_SESSION_MESSAGE_REQUEST_SPLIT, actual);
	}
	
	public void testCreateSessionMessageOfTypeEmptyNoOperationRequest() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();
		
		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {2,3});
		
		NoOperation noop = new NoOperation();
		Request request = new RequestImpl(1, timestamp, noop);
		String participantId = "4";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertXMLEqual(XML_NOOP_EMPTY, actual);
	}
	
	public void testCreateSessionMessageOfTypeNoOperationRequest() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();
		
		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {2,3});
				
		NoOperation noop = new NoOperation();
		Request request = new RequestImpl(1, timestamp, noop);
		String participantId = "4";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.REQUEST, request, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertXMLEqual(XML_NOOP, actual);
	}
	
	public void testCreateSessionMessageOfTypeCaretUpdate() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();
		
		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {5,12});
		String participantId = "4";
		CaretUpdateMessage caretMsg = new CaretUpdateMessage(1, timestamp, new CaretUpdate(3, 9));
		byte[] data = serializer.createSessionMessage(ProtocolConstants.CARET_UPDATE, caretMsg, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertXMLEqual(XML_CARETUPDATE, actual);
	}
	
	public void testCreateSessionMessageOfTypeParticipantJoined() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();

		String participantId = "1234";
		
		RemoteUserProxyExt proxy = new RemoteUserProxyImpl("sadfasd-24", new MutableUserDetails("Jimmy Ritter", InetAddress.getByName("123.43.45.21"), 4123));
		proxy.setDNSSDdiscovered(false);
		byte[] data = serializer.createSessionMessage(ProtocolConstants.PARTICIPANT_JOINED, proxy, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertXMLEqual(XML_PARTICIPANT_JOINED, actual);
	}
	
	public void testCreateSessionMessageOfTypeParticipantLeft() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();

		String participantId = "1234";
		String reason = "2323";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.PARTICIPANT_LEFT, reason, participantId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertXMLEqual(XML_PARTICIPANT_LEFT, actual);
	}

	public void testCreateSessionMessageOfTypeAcknowledge() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();

		Timestamp timestamp = (new JupiterTimestampFactory()).createTimestamp(new int[] {56,23});
		String siteId = "23421234123223";
		byte[] data = serializer.createSessionMessage(ProtocolConstants.ACKNOWLEDGE, timestamp, siteId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertXMLEqual(XML_ACKNOWLEDGE, actual);
	}
	
	public void testCreateSessionMessageOfTypeSessionTerminated() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();

		byte[] data = serializer.createSessionMessage(ProtocolConstants.SESSION_TERMINATED, null, null);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertXMLEqual(XML_SESSION_TERMINATED, actual);
	}
	
	public void testUserDiscarded() throws Exception {
		String userId = "vnmv-qqw2345";
		Serializer serializer = new CollaborationSerializer();

		byte[] data = serializer.createNotification(ProtocolConstants.USER_DISCARDED, userId);
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertXMLEqual(XML_USER_DISCARDED, actual);
	}
	
	private static final String NEWLINE = System.getProperty("line.separator");
	
	private static final String XML_USER_DISCARDED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
			"<ace><notification><userDiscarded id=\"vnmv-qqw2345\"/></notification></ace>";
	
	private static final String XML_SESSION_TERMINATED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +	NEWLINE +
	"<ace><session>" +
	"<sessionTerminated/>" +
	"</session></ace>"; 
	
	private static final String XML_ACKNOWLEDGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<ack siteId=\"23421234123223\">" +
	"<timestamp>56 23 </timestamp>" +
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
	"<user id=\"sadfasd-24\" name=\"Jimmy Ritter\" address=\"123.43.45.21\" port=\"4123\" dnssdDiscovered=\"false\"/>" +
	"</pJoined>" +
	"</session></ace>";	
	
	private static final String XML_CARETUPDATE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
			"<ace><session>" +
			"<caretUpdate siteId=\"1\" participantId=\"4\"><timestamp>5 12 </timestamp><caret dot=\"3\" mark=\"9\"/></caretUpdate>" +
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
	
	private static final String XML_NOOP = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
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
	"<request siteId=\"1\" participantId=\"4\">" +
	"<operation>" +
	"<split><first><insert position=\"172\" origin=\"56\"><text>first text</text></insert></first>" +
	"<second><delete position=\"123\">" +
		"<text>test-text.</text>" +
	"</delete></second>" +
	"</split>" +
	"</operation>" +
	"<timestamp>2 3 </timestamp>" +
	"</request>" +
	"</session></ace>";
	
	private static final String XML_SESSION_MESSAGE_REQUEST_2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<request siteId=\"1\" participantId=\"4\">" +
	"<operation>" +
	"<delete position=\"123\">" +
		"<text>test-text.</text>" +
	"</delete>" +
	"</operation>" +
	"<timestamp>2 3 </timestamp>" +
	"</request>" +
	"</session></ace>";
	
	private static final String XML_LEAVE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
			"<ace><notification>" +
			"<leave docId=\"dic-1231\" participantId=\"13\"/>" +
			"</notification></ace>";
	
	private static final String XML_KICKED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><notification>" +
	"<kicked docId=\"sdaf-2\"/>" +
	"</notification></ace>";
	
	private static final String XML_JOIN_DOCUMENT_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
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
	"<data>H4sIAAAAAAAAAB3LUQqAIBAA0atMFwhXCKKfLtAlrBYVjMC1+2f9PmYcImy3ETU1W0CQiXwkUtiV\n" +
    "86nWRjwywxkMC9FaZ65c1x571z3Xz/XfQilqwwsZEo7gVwAAAA==</data>" +
	"</document>" +
	"</response></ace>";	
	
	private static final String XML_SESSION_MESSAGE_REQUEST_1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + NEWLINE +
	"<ace><session>" +
	"<request siteId=\"1\" participantId=\"7\">" +
	"<operation>" +
	"<insert position=\"23\" origin=\"12\">" +
	"<text>test-text.</text>" +
	"</insert>" +
	"</operation>" +
	"<timestamp>2 3 </timestamp>" +
	"</request>" +
	"</session></ace>";
	
}
