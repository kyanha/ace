package ch.iserver.ace.net.impl.protocol;

import java.io.ByteArrayInputStream;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import junit.framework.TestCase;
import ch.iserver.ace.net.impl.NetworkConstants;

public class DocumentParserHandlerTest extends TestCase {

	private static final String DATA = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
			"<ace><response>" +
			"<publishedDocs>" +
			"<doc name=\"testfile.txt\" id=\"WERS24-RE2\" />" +
			"<doc name=\"meeting2.txt\" id=\"ADSFBW-45S\" />" +
			"<doc name=\"notes232.txt\" id=\"23SSWD-3ED\" />" +
			"</publishedDocs>" +
			"</response></ace>";
	
	public void testDocumentParserHandler() throws Exception {
		
		byte[] data = DATA.getBytes(NetworkConstants.DEFAULT_ENCODING);
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		SAXParserFactory factory;
		factory = SAXParserFactory.newInstance();
		ResponseParserHandler docHandler = new ResponseParserHandler();
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse( input, docHandler );
		
		Map result = (Map)docHandler.getResult();
		
		assertEquals(result.size(), 3);
		assertTrue(result.containsKey("WERS24-RE2"));
		assertTrue(result.containsKey("23SSWD-3ED"));
		assertTrue(result.containsKey("ADSFBW-45S"));
		assertTrue(result.containsValue("testfile.txt"));
		assertTrue(result.containsValue("meeting2.txt"));
		assertTrue(result.containsValue("notes232.txt"));
		
	}
	
}
