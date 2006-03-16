package ch.iserver.ace.collaboration.jupiter.server.document;

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
