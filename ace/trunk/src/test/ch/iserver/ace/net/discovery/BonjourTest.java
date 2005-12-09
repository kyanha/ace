package ch.iserver.ace.net.discovery;

import junit.framework.TestCase;

import org.easymock.MockControl;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.discovery.Bonjour;
import ch.iserver.ace.net.discovery.PeerDiscovery;
import ch.iserver.ace.net.discovery.UserRegistration;

public class BonjourTest extends TestCase {
	
	private Bonjour bonjour;
	private MockControl registrationCtrl, discoveryCtrl;
	private UserRegistration registration;
	private PeerDiscovery discovery;
	
	public void setUp() {
		registrationCtrl = MockControl.createControl(UserRegistration.class);
		registration = (UserRegistration)registrationCtrl.getMock();
		discoveryCtrl = MockControl.createControl(PeerDiscovery.class);
		discovery = (PeerDiscovery)discoveryCtrl.getMock();
		bonjour = new Bonjour(registration, discovery);
	}
	
	/**
	 * Test method for 'ch.iserver.ace.net.impl.discovery.Bonjour.execute()'
	 */
	public void testExecute() {
		//define mock behavior
		registration.isRegistered();
		registrationCtrl.setDefaultReturnValue(false);
		registration.register("username", "userid");
		discovery.browse();
		
		// replay
		registrationCtrl.replay();
		discoveryCtrl.replay();
		
		// test
		bonjour.setUserId("userid");
		bonjour.setUserDetails(new UserDetails("username"));
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

}
