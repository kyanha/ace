package ch.iserver.ace.net.impl.discovery;

import java.util.Properties;

import org.apache.log4j.Logger;

import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.impl.discovery.dnssd.DNSSDUnavailable;
import ch.iserver.ace.net.impl.discovery.dnssd.Register;
import ch.iserver.ace.net.impl.discovery.dnssd.Resolve;
import ch.iserver.ace.net.impl.discovery.dnssd.TXTUpdate;

import com.apple.dnssd.DNSRecord;
import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.TXTRecord;

/**
 * 	
 *
 */
class UserRegistrationImpl implements UserRegistration {
	
	private static Logger LOG = Logger.getLogger(UserRegistrationImpl.class);

	private DNSSDRegistration registration;
	private String actualServiceName;
	private boolean isRegistered;
	
	private TXTRecord txtRecord;
		
	/**
	 * Creates a UserRegistrationImpl.
	 */
	public UserRegistrationImpl() {
		isRegistered = false;
	}
	
	
	/**
	 * @inheritDoc
	 */
	public void register(final Properties props) {
		String serviceName = System.getProperty("user.name");
		String username = (String)props.get(Bonjour.KEY_USER);
		username = (username == null) ? serviceName : username;
		
		Register call = new Register(serviceName, 
				(String)props.get(Bonjour.KEY_REGISTRATION_TYPE), 
				getPort(props),
				TXTRecordProxy.create(props), 
				this);
		try {
			registration = (DNSSDRegistration)call.execute();
		} catch (DNSSDUnavailable du) {
			Bonjour.writeErrorLog(du);
		}
		
//		try {
//			registration = DNSSD.register(0, 0, 
//				serviceName, 
//				(String)props.get(Bonjour.KEY_REGISTRATION_TYPE), 
//				"",
//				"", 
//				getPort(props),
//				TXTRecordProxy.create(props), 
//				this);
//		} catch (Exception e) {
//			//TODO: retry strategy
//			LOG.error("Registration failed ["+e.getMessage()+"]");
//		}
	}	
	
	private int getPort(Properties props) {
		String portStr = (String)props.get(Bonjour.KEY_DISCOVERY_PORT);
		int port = 0;
		try {
			port = Integer.parseInt(portStr);
		} catch (NumberFormatException nfe) {
			LOG.error("port is not a number.");
		}
		return port;
	}

	/**
	 * @inheritDoc
	 */
	public void serviceRegistered(DNSSDRegistration registration, int flags,
			String serviceName, String regType, String domain) {
		isRegistered = true;
		this.registration = registration;
		this.actualServiceName = serviceName;
		
		//resolve the service in order to receive the TXT record
		Resolve call = new Resolve(flags, DNSSD.ALL_INTERFACES, serviceName, regType, domain, this);
		try {
			call.execute();
		} catch (DNSSDUnavailable du) {
			Bonjour.writeErrorLog(du);
		}

//		try {
//			DNSSD.resolve(flags, 0, serviceName, regType, domain, this);
//		} catch (Exception e) {
//			//TODO: retry strategy
//			LOG.error("QueryRecord failed ["+e.getMessage()+"]");
//		}
	}
	
	/**
	 * @inheritDoc
	 */
	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
		this.txtRecord = txtRecord;
		resolver.stop();
	}
	
	/**
	 * @inheritDoc
	 */
	public void operationFailed(DNSSDService service, int errorCode) {
		//TODO: error handling
		LOG.error("operationFailed ["+errorCode+"]");
	}
	
	public String getServiceName() {
		return actualServiceName;
	}
	
	public TXTRecord getTXTRecord() {
		return txtRecord;
	}
	
	public boolean isRegistered() {
		return isRegistered;
	}
	
	/**
	 * Updates the user's details in the TXT record of this service.
	 * 
	 * @param details
	 */
	public void updateUserDetails(UserDetails details) {
		TXTRecordProxy.set(TXTRecordProxy.TXT_USER, details.getUsername(), txtRecord);
		
		TXTUpdate call = new TXTUpdate(registration, txtRecord.getRawBytes());
		try {
			call.execute();
		} catch (DNSSDUnavailable du) {
			Bonjour.writeErrorLog(du);
		}
		
//		try {
//			TXTRecordProxy.set(TXTRecordProxy.TXT_USER, details.getUsername(), txtRecord);
//			DNSRecord record = registration.getTXTRecord();
//			record.update(0, txtRecord.getRawBytes(), 0);
//		} catch (Exception e) {
//			LOG.error("could not modify TXT record: "+e);
//		}
	}
	
	public void stop() {
		registration.stop();
		isRegistered = false;
	}

}
