/**
 * 
 */
package ch.iserver.ace.net.jdiscovery;

import java.rmi.RemoteException;

import net.jini.core.lookup.ServiceID;

/**
 * @author lukaszbinden
 *
 */
public class DiscoveryListenerImpl implements IDiscoveryListener {

	private ServiceID serviceID;
	
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

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.jdiscovery.IDiscoveryListener#serviceLogon(ch.iserver.ace.net.discovery.jini.rmi.ServiceDO)
	 */
	public ServiceID serviceLogon(ServiceDO newService) throws RemoteException {
		RegistrationLookupMediator.getInstance().serviceLoggedOn(newService);
		//TODO: wait as long as serviceID is null
		return serviceID;
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.jdiscovery.IDiscoveryListener#serviceLogout(java.lang.String)
	 */
	public void serviceLogout(ServiceID serviceId) throws RemoteException {
		RegistrationLookupMediator.getInstance().serviceLoggedOut(serviceId);
	}

	/* (non-Javadoc)
	 * @see ch.iserver.ace.net.jdiscovery.IDiscoveryListener#serviceNameChanged(java.lang.String, java.lang.String)
	 */
	public void serviceNameChanged(ServiceID serviceId, String serviceName)
			throws RemoteException {
		RegistrationLookupMediator.getInstance().serviceNameChanged(serviceId, serviceName);
	}
	
	/**
	 * Non-remote method.
	 * 
	 */
	public void setServiceID(ServiceID id) {
		this.serviceID = id;
	}

}
