package ch.iserver.ace.net.discovery;

import java.net.InetAddress;

import org.apache.log4j.Logger;

import ch.iserver.ace.ServerInfo;
import ch.iserver.ace.UserDetails;
import ch.iserver.ace.net.core.NetworkProperties;
import ch.iserver.ace.net.core.NetworkServiceImpl;
import ch.iserver.ace.net.discovery.dnssd.DNSSDUnavailable;
import ch.iserver.ace.net.discovery.dnssd.QueryRecord;
import ch.iserver.ace.net.discovery.dnssd.Register;
import ch.iserver.ace.net.discovery.dnssd.Resolve;
import ch.iserver.ace.net.discovery.dnssd.TXTUpdate;
import ch.iserver.ace.util.ParameterValidator;

import com.apple.dnssd.DNSSD;
import com.apple.dnssd.DNSSDRegistration;
import com.apple.dnssd.DNSSDService;
import com.apple.dnssd.TXTRecord;

/**
 * Default implementation of {@link ch.iserver.ace.net.discovery.UserRegistration}.
 * 
 *  <p>For Bonjour related classes refer to 
 *  {@link http://developer.apple.com/documentation/Java/Reference/DNSServiceDiscovery_JavaRef/index.html}	
 *	</p>
 */
class UserRegistrationImpl implements UserRegistration {
	
	private static Logger LOG = Logger.getLogger(UserRegistrationImpl.class);

	/**
	 * The DNSSDRegistration instance.
	 */
	private DNSSDRegistration registration;
	
	/**
	 * The actual name of the local service. This name is returned
	 * by the DNSSD. Per default, it is the same name as the one passed 
	 * to DNSSD.
	 */
	private String actualServiceName;
	
	/**
	 * Flag to indicate whether the local user is actually registered 
	 * with DNSSD.
	 */
	private boolean isRegistered;
	
	/**
	 * The reference to the TXT record of the local user.
	 */
	private TXTRecord txtRecord;
		
	/**
	 * Creates a new UserRegistrationImpl.
	 */
	public UserRegistrationImpl() {
		isRegistered = false;
	}
	
	/*********************************************/
	/** methods from interface UserRegistration **/
	/*********************************************/
	
	/**
	 * {@inheritDoc}
	 */
	public void register(String username, String userid) {
		String serviceName = System.getProperty("user.name");
		//remove spaces from service name
		serviceName = serviceName.replaceAll(" ", "+");
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
	
	/**
	 * {@inheritDoc}
	 */
	public boolean isRegistered() {
		return isRegistered;
	}
	
	/**
	 * {@inheritDoc}
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
	
	/**
	 * {@inheritDoc}
	 */
	public void stop() {
		if (registration != null) {
			registration.stop();
		}
		isRegistered = false;
	}
	
	/*************************************************/
	/** methods from interface UserRegistrationImpl **/
	/*************************************************/
	
	/**
	 * Gets the actual service name of the local user.
	 * 
	 * @return the actual service name
	 */
	public String getServiceName() {
		return actualServiceName;
	}
	
	/**
	 * Gets the TXT record object for the local user.
	 * 
	 * @return the TXTRecord object
	 */
	public TXTRecord getTXTRecord() {
		return txtRecord;
	}
	
	/**
	 * Resolves the IP address for the local user.
	 * 
	 * @param flags		Possible values are DNSSD.MORE_COMING.
	 * @param ifIndex	The interface on which the query was resolved. 
	 * @param hostName  	The target hostname of the machine providing the service.
	 * @see QueryRecord
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
	 * Gets the protocol port this user listens at.
	 * 
	 * @return the protocol port 
	 */
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
	
	/********************************************/
	/** method from interface RegisterListener **/
	/********************************************/
	
	/**
	 * {@inheritDoc}
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
	
	
	/*******************************************/
	/** method from interface ResolveListener **/
	/*******************************************/
	
	/**
	 * {@inheritDoc}
	 */
	public void serviceResolved(DNSSDService resolver, int flags, int ifIndex,
			String fullName, String hostName, int port, TXTRecord txtRecord) {
		ParameterValidator.notNull("txtrecord for local user", txtRecord);
		this.txtRecord = txtRecord;
		resolver.stop();
		
		String serviceName = Bonjour.getServiceName(fullName);
		resolveIP(flags, ifIndex, hostName, serviceName);
	}
	
	
	/*****************************************/
	/** method from interface QueryListener **/
	/*****************************************/

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
		LOG.info("discovery address ["+address+"]");
		query.stop();
		int port = Integer.parseInt(NetworkProperties.get(NetworkProperties.KEY_PROTOCOL_PORT));
		
		if (address.isLoopbackAddress()) {
			try {
				InetAddress localhost = InetAddress.getLocalHost();
				address = (localhost.isLoopbackAddress() ? address : localhost);
			} catch (Exception e) {}
		}
		LOG.info("local user address resolved to ["+address+"]");
		
		ServerInfo info = new ServerInfo(address, port);
		NetworkServiceImpl.getInstance().setServerInfo(info);
	}
	
	/*****************************************/
	/** method from interface BaseListener **/
	/*****************************************/
	
	/**
	 * {@inheritDoc}
	 */
	public void operationFailed(DNSSDService service, int errorCode) {
		Bonjour.writeErrorLog(new Exception("operationFailed("+service+", "+errorCode+")"));
	}

}
