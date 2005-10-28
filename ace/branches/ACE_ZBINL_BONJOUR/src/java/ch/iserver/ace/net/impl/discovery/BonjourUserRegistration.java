package ch.iserver.ace.net.impl.discovery;

import java.util.Properties;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.TXTRecord;

/**
 * 	
 *
 */
public class BonjourUserRegistration implements RegisterListener {

	private DNSSDRegistration registration;
		
	public BonjourUserRegistration() {
	}
	
	
	/**
	 * Registers this service.
	 */
	public void register(final Properties props) {
		String systemUsername = System.getProperty("user.name");
		String username = (String)props.get(Bonjour.USER_KEY);
		username = (username == null) ? systemUsername : username;
		
		try {
			registration = DNSSD.register(0, 0, 
				systemUsername, 
				(String)props.get(Bonjour.REGISTRATION_TYPE_KEY), 
				"",
				"", 
				((Integer)props.get(Bonjour.DISCOVERY_PORT_KEY)).intValue(), 
				createTXTRecord(props), 
				this);
		} catch (Exception e) {
			//TODO:
		}
	}	
	
	private TXTRecord createTXTRecord(final Properties props) {
		TXTRecord r = new TXTRecord();
		r.set(Bonjour.TXT_VERSION_KEY, (String)props.get(Bonjour.TXT_VERSION_KEY));
		r.set(Bonjour.USER_KEY, (String)props.get(Bonjour.USER_KEY));
		r.set(Bonjour.USERID_KEY, (String)props.get(Bonjour.USERID_KEY));
		r.set(Bonjour.PROTOCOL_VERSION_KEY, (String)props.get(Bonjour.PROTOCOL_VERSION_KEY));
		return r;
	}
	
	public void serviceRegistered(DNSSDRegistration registration, int flags,
			String serviceName, String regType, String domain) {
		// TODO Auto-generated method stub
		
	}
	
	public void operationFailed(DNSSDService service, int errorCode) {
		// TODO Auto-generated method stub
		
	}
	
	public void stop() {
		registration.stop();
	}

}
