package ch.iserver.ace.net.impl.discovery;

import java.util.Properties;

import org.apache.log4j.Logger;

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
	
	private static Logger LOG = Logger.getLogger(BonjourUserRegistration.class);

	private DNSSDRegistration registration;
	private String actualServiceName;
		
	public BonjourUserRegistration() {}
	
	
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
		this.registration = registration;
		this.actualServiceName = serviceName;
	}
	
	public void operationFailed(DNSSDService service, int errorCode) {
		//TODO: error handling
		LOG.error("operationFailed ["+errorCode+"]");
	}
	
	public String getServiceName() {
		return actualServiceName;
	}
	
	public void stop() {
		registration.stop();
	}

}
