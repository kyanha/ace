package ch.iserver.ace.net.impl.protocol;

import java.net.InetAddress;

import junit.framework.TestCase;
import ch.iserver.ace.CaretUpdate;
import ch.iserver.ace.Fragment;
import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.ParticipantConnection;
import ch.iserver.ace.net.impl.FragmentImpl;
import ch.iserver.ace.net.impl.MutableUserDetails;
import ch.iserver.ace.net.impl.NetworkProperties;
import ch.iserver.ace.net.impl.NetworkServiceImpl;
import ch.iserver.ace.net.impl.PortableDocumentExt;
import ch.iserver.ace.net.impl.PortableDocumentImpl;
import ch.iserver.ace.net.impl.PublishedDocument;
import ch.iserver.ace.net.impl.RemoteUserProxyFactory;
import ch.iserver.ace.net.impl.SessionConnectionImpl;
import ch.iserver.ace.net.impl.protocol.RequestImpl.DocumentInfo;

public class CollaborationSerializerTest extends TestCase {

	
	public void testCreateNotificationLeave() throws Exception {
		
		CollaborationSerializer serializer = new CollaborationSerializer();
		
		SessionConnectionImpl conn = new SessionConnectionImpl("dic-1231", null, null, null, null);
		conn.setParticipantId(13);
		
		byte[] data = serializer.createNotification(ProtocolConstants.LEAVE, conn);
		String actual = new String(data);
		
		assertEquals(XML_LEAVE, actual);
	}
	
	public void testCreateNotificationKicked() throws Exception {
		CollaborationSerializer serializer = new CollaborationSerializer();
		
		String docId = "sdaf-2";
		
		byte[] data = serializer.createNotification(ProtocolConstants.KICKED, docId);
		String actual = new String(data);
		
		assertEquals(XML_KICKED, actual);
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
		assertEquals(XML_JOIN_DOCUMENT, actual);
	}
	
	public void testCreateResponseJoinRejected() throws Exception {
		String userId = "vnmv-qqw2345";
		NetworkServiceImpl.getInstance().setUserId(userId);
		Serializer serializer = new CollaborationSerializer();
		
		String docId = "doc-id-234b";
		
		byte[] data = serializer.createResponse(ProtocolConstants.JOIN_REJECTED, docId, "501");
		String actual = new String(data, NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));

		assertEquals(XML_JOIN_REJECTED, actual);
	}
	
	private static final String XML_LEAVE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><notification>" +
			"<leave docId=\"dic-1231\" participantId=\"13\"/>" +
			"</notification></ace>";
	
	private static final String XML_KICKED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><notification>" +
	"<kicked docId=\"sdaf-2\"/>" +
	"</notification></ace>";
	
	private static final String XML_JOIN_DOCUMENT = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
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
	"<data>" +
	"<![CDATA[0 11 Los gehts:  1 15 ich habe durst. 2 18  das sagst du mir? 1 20  dir sage ich alles!]]>" +
	"</data>" +
	"</document>" +
	"</response></ace>";	
	
	private static final String XML_JOIN_REJECTED = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
	"<ace><response>" +
	"<joinRejected docId=\"doc-id-234b\" userid=\"vnmv-qqw2345\">" +
	"<reason code=\"501\"/>" +
	"</joinRejected>" +
	"</response></ace>";
}
