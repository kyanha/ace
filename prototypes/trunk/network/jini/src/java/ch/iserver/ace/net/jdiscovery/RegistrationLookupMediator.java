package ch.iserver.ace.net.jdiscovery;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;

import net.jini.core.lookup.ServiceID;

/**
 * @author lukaszbinden
 * @date   Sep 17, 2006 4:33:54 PM
 *
 */
public class RegistrationLookupMediator {

	private ServiceID localID;
	private ServiceDO localService;
	
	//serviceID - serviceDO
	private HashMap peers;
	
	private static RegistrationLookupMediator instance;
	
	public static RegistrationLookupMediator getInstance() {
		if (instance == null) {
			instance = new RegistrationLookupMediator();
		}
		return instance;
	}
	
	private RegistrationLookupMediator() {
		peers = new HashMap();
	}
	
	public ServiceDO getServiceDO() {
		if (localService == null) {
			//initialize ServiceDO which will be sent to all peers
			try {
				localService = new ServiceDO(localID, System.getProperty("user.name"), 
					InetAddress.getLocalHost(), 4123);
			} catch (Exception e) {
				PeerApp.print("Initalization error: " + e.getMessage());
			}
		}
		return localService;
	}
	
	public void setServiceID(ServiceID id) {
		this.localID = id;
	}
	
	public void serviceLoggedOn(ServiceDO info) {
		Peer peer = (Peer) peers.get(info.getID());
		if (peer == null) {
			PeerApp.print("peer not yet discovered by LS");
			peer = new Peer(info.getID());
			peers.put(info.getID(), peer);
		}
		peer.setServiceInfo(info);
	}
	
	public void serviceLoggedOut(ServiceID id) {
		Object peer = peers.remove(id);
		if (peer != null)
			PeerApp.print("peer successfully removed.");
		else
			PeerApp.print("peer could not be removed from list.");
	}
	
	public void serviceNameChanged(ServiceID id, String name) {
		Peer peer = (Peer) peers.get(id);
		peer.getServiceInfo().updateName(name);
		//TODO: send notification to UIConsole
		PeerApp.print("\n***\n Received name update from [" 
				+ id.toString() + "]: " + name + "\n***\n");
	}
	
	public void addPeer(Peer aPeer) {
		peers.put(aPeer.getServiceID(), aPeer);
	}
	
	public void updateMyName(String newName) {
		//for all peers, call serviceNameChanged
		Iterator iter = peers.values().iterator();
		while (iter.hasNext()) {
			Peer peer = (Peer) iter.next();
			try {
				peer.getPeerListener().serviceNameChanged(localID, newName);
			} catch (RemoteException re) {
				PeerApp.print("Connection error: " + re.getMessage()); 
			}
		}
	}
	
	public void logout() {
		//for all peers, call serviceLogout
		Iterator iter = peers.values().iterator();
		while (iter.hasNext()) {
			Peer peer = (Peer) iter.next();
			try {
				peer.getPeerListener().serviceLogout(localID);
			} catch (RemoteException re) {
				PeerApp.print("Connection error: " + re.getMessage()); 
			}
		}
	}
	
	
}
