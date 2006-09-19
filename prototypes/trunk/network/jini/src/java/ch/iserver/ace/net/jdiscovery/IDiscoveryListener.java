package ch.iserver.ace.net.jdiscovery;

import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.core.lookup.ServiceID;

public interface IDiscoveryListener extends Remote {

	/**
	 * Register with the peer. 
	 * 
	 * @param newService
	 * @return	the ServiceID of the peer
	 * @throws RemoteException
	 */
	ServiceID serviceLogon(ServiceDO newService) throws RemoteException;
	
	void serviceLogout(ServiceID serviceId) throws RemoteException;
	
	void serviceNameChanged(ServiceID serviceId, String serviceName) throws RemoteException;

	void checkAlive() throws RemoteException;
		
}
