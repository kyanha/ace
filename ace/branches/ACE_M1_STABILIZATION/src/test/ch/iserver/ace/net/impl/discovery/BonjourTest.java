package ch.iserver.ace.net.impl.discovery;

import java.util.Properties;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.impl.DiscoveryCallback;

public class BonjourTest extends TestCase {
	
	private Bonjour bonjour;
	private MockControl registrationCtrl, discoveryCtrl;
	private UserRegistration registration;
	private PeerDiscovery discovery;
	private Properties props;
	
	public void setUp() {
		registrationCtrl = MockControl.createControl(UserRegistration.class);
		registration = (UserRegistration)registrationCtrl.getMock();
		discoveryCtrl = MockControl.createControl(PeerDiscovery.class);
		discovery = (PeerDiscovery)discoveryCtrl.getMock();
		props = loadProperties();
		bonjour = new Bonjour(registration, discovery, props);
	}
	
	/**
	 * Test method for 'ch.iserver.ace.net.impl.discovery.Bonjour.execute()'
	 */
	public void testExecute() {
		//define mock behavior
		registration.register(props);
		discovery.browse(props);
		
		// replay
		registrationCtrl.replay();
		discoveryCtrl.replay();
		
		// test
		bonjour.execute();
		
		// verify
		registrationCtrl.verify();
		discoveryCtrl.verify();
	}
	
	/**
	 * Test method for 'ch.iserver.ace.net.impl.discovery.Bonjour.abort()'
	 */
	public void testAbort() {
		//define mock behavior
		registration.stop();
		discovery.stop();
		
		// replay
		registrationCtrl.replay();
		discoveryCtrl.replay();
		
		// test
		bonjour.abort();
		
		// verify
		registrationCtrl.verify();
		discoveryCtrl.verify();
	}
	
	/**
	 * Test method for 'ch.iserver.ace.net.impl.discovery.Bonjour.setUserDetails(UserDetails details)'
	 */
	public void testSetUserDetails() {
		UserDetails details = new UserDetails("testuser");
		
		registration.isRegistered();
		registrationCtrl.setReturnValue(true);
		registration.updateUserDetails(details);
		
		registrationCtrl.replay();
				
		bonjour.setUserDetails(details);
		
		registrationCtrl.verify();
	}
	
	private Properties loadProperties() {
	    Properties properties = new Properties();
	    //properties not needed for tests
		return properties;
	}

}
