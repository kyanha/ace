package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.NetworkProperties;

public class DeserializerImplTest extends TestCase {
	
	public void testDeserializePublishedDocumentsRequest() throws Exception {
		
		ResponseParserHandler handler = new ResponseParserHandler();
		Deserializer deserializer = DeserializerImpl.getInstance();
		
		byte[] data = ResponseParserHandlerTest.XML_JOIN_DOCUMENT.getBytes(NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		deserializer.deserialize(data, handler);
		Request result = handler.getResult();
		
		//verify that the handler was actually used
		assertNotNull(result);
		assertEquals(ProtocolConstants.JOIN_DOCUMENT, handler.getType());
		assertNotNull(result.getPayload());
	}
	
}
