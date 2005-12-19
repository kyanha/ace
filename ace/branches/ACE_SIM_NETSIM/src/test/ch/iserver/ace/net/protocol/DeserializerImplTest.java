package ch.iserver.ace.net.protocol;

import junit.framework.TestCase;
import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.core.RemoteUserProxyFactory;
import ch.iserver.ace.util.Base64;

public class DeserializerImplTest extends TestCase {
	
	public void testDeserializePublishedDocumentsRequest() throws Exception {
		
		CollaborationParserHandler handler = new CollaborationParserHandler();
		Deserializer deserializer = DeserializerImpl.getInstance();
		
		String joinDocument = CollaborationParserHandlerTest.XML_JOIN_DOCUMENT_1;
		String payload = "0 11 Los gehts:  1 15 ich habe durst. 2 18  das sagst du mir? 1 20  dir sage ich alles!";
		joinDocument += Base64.encodeBytes(payload.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING)), Base64.GZIP);
		joinDocument += CollaborationParserHandlerTest.XML_JOIN_DOCUMENT_2;
		byte[] data = joinDocument.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		
		//verify that the handler was actually used
		assertNotNull(result);
		assertEquals(ProtocolConstants.JOIN_DOCUMENT, handler.getType());
		assertNotNull(result.getPayload());
	}
	
}
