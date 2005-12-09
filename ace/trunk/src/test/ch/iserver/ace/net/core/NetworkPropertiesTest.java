package ch.iserver.ace.net.core;

import ch.iserver.ace.net.core.NetworkProperties;
import junit.framework.TestCase;

public class NetworkPropertiesTest extends TestCase {

	
	public void testGet() {
		assertEquals("UTF-8", NetworkProperties.get(NetworkProperties.KEY_DEFAULT_ENCODING));
		assertEquals("http://ace.iserver.ch/profiles/ACE", NetworkProperties.get(NetworkProperties.KEY_PROFILE_URI));
		assertEquals("4123", NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_PORT));
		assertEquals("0.1", NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_VERSION));
		assertEquals("_ace._tcp", NetworkProperties.get(NetworkProperties.KEY_REGISTRATION_TYPE));
		assertEquals("1", NetworkProperties.get(NetworkProperties.KEY_TXT_VERSION));
	}
	
}
