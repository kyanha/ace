package ch.iserver.ace.algorithm;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ch.iserver.ace.algorithm");
		//$JUnit-BEGIN$
		suite.addTest(ch.iserver.ace.algorithm.jupiter.AllTests.suite());
		suite.addTest(ch.iserver.ace.algorithm.test.AllTests.suite());
		suite.addTest(ch.iserver.ace.algorithm.test.jupiter.AllTests.suite());
		suite.addTest(ch.iserver.ace.algorithm.text.AllTests.suite());
		//$JUnit-END$
		return suite;
	}

}
