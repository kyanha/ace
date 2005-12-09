package ch.iserver.ace;

import junit.framework.Test;
import junit.framework.TestSuite;
import ch.iserver.ace.collaboration.CollaborationServiceTest;
import ch.iserver.ace.collaboration.server.ServerTest;
import ch.iserver.ace.net.core.DiscoveryTest;
import ch.iserver.ace.net.discovery.PeerDiscoveryTest;
import ch.iserver.ace.net.discovery.UserRegistrationTest;

public class AllIntegrationTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ch.iserver.ace");
		//$JUnit-BEGIN$
		suite.addTestSuite(CollaborationServiceTest.class);
		suite.addTestSuite(ServerTest.class);
		suite.addTestSuite(PeerDiscoveryTest.class);
		suite.addTestSuite(UserRegistrationTest.class);
		suite.addTestSuite(DiscoveryTest.class);
		//$JUnit-END$
		return suite;
	}

}
