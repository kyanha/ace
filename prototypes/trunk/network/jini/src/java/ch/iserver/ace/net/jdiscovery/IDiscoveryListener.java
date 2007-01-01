package ch.iserver.ace.net.jdiscovery;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IDiscoveryListener extends Remote {

	/**
	 * Register with the peer. 
	 * 
	 * @param newService
	 * @return	the service ID of the peer
	 * @throws RemoteException
	 */
	String serviceLogon(ServiceDO newService) throws RemoteException;
	
	void serviceLogout(String serviceId) throws RemoteException;
	
	void serviceNameChanged(String serviceId, String serviceName) throws RemoteException;

	void checkAlive() throws RemoteException;
		
}
