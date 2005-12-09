package ch.iserver.ace.net.impl.discovery;

import java.net.InetAddress;

import org.apache.log4j.Logger;

import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.impl.discovery.dnssd.DNSSDUnavailable;
import ch.iserver.ace.net.impl.discovery.dnssd.QueryRecord;
import ch.iserver.ace.net.impl.discovery.dnssd.Register;
import ch.iserver.ace.net.impl.discovery.dnssd.Resolve;
import ch.iserver.ace.net.impl.discovery.dnssd.TXTUpdate;
import ch.iserver.ace.util.ParameterValidator;

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
	 * Properties must include USER_NAME and USER_ID.
	 * 
	 * @param props the properties to register
	 */
	public void register(String username, String userid) {
		String serviceName = System.getProperty("user.name");
		if (username == null) {
			username = serviceName;
		}
		
		Register call = new Register(serviceName, 
				NetworkProperties.get(NetworkProperties.KEY_REGISTRATION_TYPE), 
				getPort(),
				TXTRecordProxy.create(username, userid), 
				this);
		try {
			registration = (DNSSDRegistration) call.execute();
		} catch (DNSSDUnavailable du) {
			Bonjour.writeErrorLog(du);
		}
	}	
	
	private int getPort() {
		String portStr = NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_PORT);
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
	}
	
	/**
	 * @inheritDoc
	 */
	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
		ParameterValidator.notNull("txtrecord for local user", txtRecord);
		this.txtRecord = txtRecord;
		resolver.stop();
		
		String serviceName = Bonjour.getServiceName(fullName);
		resolveIP(flags, ifIndex, hostName, serviceName);
	}
	
	/**
	 * 
	 * @param flags
	 * @param ifIndex
	 * @param hostName
	 */
	private void resolveIP(int flags, int ifIndex, String hostName, String serviceName) {
		QueryRecord call = new QueryRecord(ifIndex, hostName, Bonjour.T_HOST_ADDRESS, this);
		try {
			call.execute();
		} catch (DNSSDUnavailable du) {
			Bonjour.writeErrorLog(du);
		}
	}
	
	/**
	 * @inheritDoc
	 */
	public void operationFailed(DNSSDService service, int errorCode) {
		Bonjour.writeErrorLog(new Exception("operationFailed("+service+", "+errorCode+")"));
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
	}
	
	public void stop() {
		registration.stop();
		isRegistered = false;
	}


	/**
	 * @see com.apple.dnssd.QueryListener#queryAnswered(com.apple.dnssd.DNSSDService, int, int, java.lang.String, int, int, byte[], int)
	 */
	public void queryAnswered(DNSSDService query, int flags, int ifIndex,
			String fullName, int rrtype, int rrclass, byte[] rdata, int ttl) {

		InetAddress address = null;
		try {
			address = InetAddress.getByAddress(rdata);
		} catch (Exception e) {
			LOG.error("Could not resolve address ["+e.getMessage()+"]");
		}
		LOG.info("local user address resolved to ["+address+"]");
		query.stop();
		int port = Integer.parseInt(NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_PORT));
		ServerInfo info = new ServerInfo(address, port);
		NetworkServiceImpl.getInstance().setServerInfo(info);
	}

}
