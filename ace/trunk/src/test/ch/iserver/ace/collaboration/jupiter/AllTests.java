package ch.iserver.ace.collaboration.jupiter;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
						"Test for ch.iserver.ace.collaboration.jupiter");
		//$JUnit-BEGIN$
		suite.addTestSuite(AlgorithmWrapperImplTest.class);
		//$JUnit-END$
		return suite;
	}

}
