package ch.iserver.ace;

import junit.framework.Test;
import junit.framework.TestSuite;
import ch.iserver.ace.collaboration.CollaborationServiceTest;
import ch.iserver.ace.collaboration.server.ServerTest;
import ch.iserver.ace.net.impl.discovery.PeerDiscoveryTest;
import ch.iserver.ace.net.impl.discovery.UserRegistrationTest;
import ch.iserver.ace.net.impl.discovery.dnssd.RetryStrategyTest;
import ch.iserver.ace.net.impl.protocol.ConcealDocumentReceiveFilterTest;
import ch.iserver.ace.net.impl.protocol.ConcealDocumentTest;
import ch.iserver.ace.net.impl.protocol.DocumentDiscoveryTest;
import ch.iserver.ace.net.impl.protocol.PublishDocumentReceiveFilterTest;
import ch.iserver.ace.net.impl.protocol.PublishDocumentTest;
import ch.iserver.ace.net.impl.protocol.RequestHandlerImplTest;

public class AllIntegrationTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ch.iserver.ace");
		//$JUnit-BEGIN$
		suite.addTestSuite(CollaborationServiceTest.class);
		suite.addTestSuite(ServerTest.class);
		suite.addTestSuite(PeerDiscoveryTest.class);
		suite.addTestSuite(UserRegistrationTest.class);
		suite.addTestSuite(RetryStrategyTest.class);
		suite.addTestSuite(ConcealDocumentTest.class);
		suite.addTestSuite(DocumentDiscoveryTest.class);
		suite.addTestSuite(PublishDocumentReceiveFilterTest.class);
		suite.addTestSuite(PublishDocumentTest.class);
		suite.addTestSuite(RequestHandlerImplTest.class);
		//$JUnit-END$
		return suite;
	}

}
