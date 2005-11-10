package ch.iserver.ace.net.impl.protocol;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.NetworkConstants;

public class SerializerImplTest extends TestCase {

	private static final String EXPECTED_DATA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<ace><request><query type=\"doc\"/></request></ace>";
	
	public void testCreateQueryForPublishedDocuments() throws Exception {
		Serializer serializer = new SerializerImpl();
		
		byte[] data = serializer.createQuery(Serializer.PUBLISHED_DOCUMENTS);
		String actual = new String(data, NetworkConstants.DEFAULT_ENCODING);
		
		assertEquals(actual, EXPECTED_DATA);
	}
	
}
