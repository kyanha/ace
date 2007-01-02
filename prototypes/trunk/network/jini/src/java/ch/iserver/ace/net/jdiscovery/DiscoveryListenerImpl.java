/**
 * 
 */
package ch.iserver.ace.net.jdiscovery;

import java.rmi.RemoteException;

/**
 * @author lukaszbinden
 *
 */
public class DiscoveryListenerImpl implements IDiscoveryListener {

	private String serviceID;
	
	public DiscoveryListenerImpl() throws RemoteException {
		
	}
	
	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.jdiscovery.IDiscoveryListener#checkAlive()
	 */
	public void checkAlive() throws RemoteException {
		LogUtil.print("DiscoveryListenerImpl", "checkAlive() called by client, putting thread to wait state...");
		try {
			//TODO: check if the correct behavior is caused
			synchronized (this) {
				this.wait();
			}
			LogUtil.print("DiscoveryListenerImpl", "<-- checkAlive()");
		} catch (Exception e) {
			e.printStackTrace();
			throw new RemoteException(e.getMessage());
		}

	}

	/**
	 * @see ch.iserver.ace.net.jdiscovery.IDiscoveryListener#serviceLogon(ch.iserver.ace.net.discovery.jini.rmi.ServiceDO)
	 */
	public String serviceLogon(ServiceDO newService) throws RemoteException {
		LogUtil.print("DiscoveryListenerImpl.serviceLogon: " + newService);
		RegistrationLookupMediator.getInstance().serviceLoggedOn(newService);
		//TODO: wait as long as serviceID is null
		return serviceID;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.jdiscovery.IDiscoveryListener#serviceLogout(java.lang.String)
	 */
	public void serviceLogout(String serviceId) throws RemoteException {
		LogUtil.print("DiscoveryListenerImpl.serviceLogout(" + serviceId + ")");
		RegistrationLookupMediator.getInstance().serviceLoggedOut(serviceId);
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.jdiscovery.IDiscoveryListener#serviceNameChanged(java.lang.String, java.lang.String)
	 */
	public void serviceNameChanged(String serviceId, String serviceName)
			throws RemoteException {
		LogUtil.print("DiscoveryListenerImpl.serviceNameChanged(" + serviceId + ", " + serviceName + ")");
		RegistrationLookupMediator.getInstance().serviceNameChanged(serviceId, serviceName);
	}
	
	/**
	 * Non-remote method.
	 * 
	 */
	public void setServiceID(String id) {
		this.serviceID = id;
	}

}
