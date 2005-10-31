package ch.iserver.ace.net.impl.discovery;

import java.util.Properties;

import org.apache.log4j.Logger;

import ch.iserver.ace.UserDetails;

import com.apple.dnssd.DNSRecord;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.RegisterListener;
import com.apple.dnssd.ResolveListener;
import com.apple.dnssd.TXTRecord;

/**
 * 	
 *
 */
class BonjourUserRegistration implements RegisterListener, ResolveListener {
	
	private static Logger LOG = Logger.getLogger(BonjourUserRegistration.class);

	private DNSSDRegistration registration;
	private String actualServiceName;
	private boolean isRegistered;
	
	private TXTRecord txtRecord;
		
	public BonjourUserRegistration() {
		isRegistered = false;
	}
	
	
	/**
	 * Registers this service.
	 */
	public void register(final Properties props) {
		String serviceName = System.getProperty("user.name");
		String username = (String)props.get(Bonjour.KEY_USER);
		username = (username == null) ? serviceName : username;
		
		try {
			registration = DNSSD.register(0, 0, 
				serviceName, 
				(String)props.get(Bonjour.KEY_REGISTRATION_TYPE), 
				"",
				"", 
				((Integer)props.get(Bonjour.KEY_DISCOVERY_PORT)).intValue(), 
				TXTRecordProxy.create(props), 
				this);
		} catch (Exception e) {
			//TODO:
			LOG.error("Registration failed ["+e.getMessage()+"]");
		}
	}	
	
	public void serviceRegistered(DNSSDRegistration registration, int flags,
			String serviceName, String regType, String domain) {
		isRegistered = true;
		this.registration = registration;
		this.actualServiceName = serviceName;
		
		//resolve the service in order to receive the TXT record
		try {
			DNSSD.resolve(flags, 0, serviceName, regType, domain, this);
		} catch (Exception e) {
			//TODO:
			LOG.error("Resolve failed ["+e.getMessage()+"]");
		}
	}
	
	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
		this.txtRecord = txtRecord;
		resolver.stop();
	}
	
	public void operationFailed(DNSSDService service, int errorCode) {
		//TODO: error handling
		LOG.error("operationFailed ["+errorCode+"]");
	}
	
	public String getServiceName() {
		return actualServiceName;
	}
	
	public boolean isRegistered() {
		return isRegistered;
	}
	
	/**
	 * Updates the user's details in the TXT record of this service.
	 * 
	 * @param details
	 */
	public void update(UserDetails details) {
		try {
			TXTRecordProxy.set(Bonjour.KEY_USER, details.getUsername(), txtRecord);
			DNSRecord record = null;
			record = registration.getTXTRecord();
			record.update(0, txtRecord.getRawBytes(), 0);
		} catch (Exception e) {
			LOG.warn("could not modify TXT record: "+e);
		}
	}
	
	public void stop() {
		registration.stop();
	}

}
