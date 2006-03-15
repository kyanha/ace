package ch.iserver.ace.text;

import ch.iserver.ace.text.server.SimpleServerDocumentTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ch.iserver.ace.text");
		//$JUnit-BEGIN$
		suite.addTestSuite(SimplePartitionerTest.class);
		suite.addTestSuite(SimpleDocumentTest.class);
		suite.addTestSuite(SimpleServerDocumentTest.class);
		//$JUnit-END$
		return suite;
	}

}
