package ch.iserver.ace.application;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ch.iserver.ace.application");
		//$JUnit-BEGIN$
		suite.addTestSuite(ApplicationControllerImplTest.class);
		suite.addTestSuite(DocumentViewTest.class);
		suite.addTestSuite(ParticipantViewTest.class);
		suite.addTestSuite(PropertyChangeEventMatcherTest.class);
		suite.addTestSuite(UserViewTest.class);
		suite.addTestSuite(BrowseViewTest.class);
		suite.addTestSuite(ItemSelectionChangeEventMatcherTest.class);
		//$JUnit-END$
		return suite;
	}

}
