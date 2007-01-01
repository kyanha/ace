package ch.iserver.ace.net.jdiscovery;

import java.net.InetAddress;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceID;

/**
 * @author lukaszbinden
 * @date   Sep 17, 2006 4:33:54 PM
 *
 */
public class RegistrationLookupMediator {

	private ServiceID localID;
	private ServiceDO localService;
	private LookupLocator locator;
	
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
	
	private static void print(String msg) {
		LogUtil.print("RegistrationLookupMediator", msg);
	}
	
	public ServiceDO getServiceDO() {
		if (localService == null) {
			//initialize ServiceDO which will be sent to all peers
			try {
				localService = new ServiceDO(localID, System.getProperty("user.name"), 
					InetAddress.getLocalHost(), 4123);
			} catch (Exception e) {
				print("Initalization error: " + e.getMessage());
			}
		}
		return localService;
	}
	
	public void setRegistered(ServiceID id, LookupLocator locator) {
		this.localID = id;
		this.locator = locator;
	}
	
	public LookupLocator getLocalLookupLocator() {
		return locator;
	}
	
	public void serviceLoggedOn(ServiceDO info) {
		Peer peer = (Peer) peers.get(info.getID());
		if (peer == null) {
			print("peer not yet discovered by LS");
			//TODO: where is IDiscoveryListener??
			peer = new Peer(info.getID());
			peers.put(info.getID(), peer);
		}
		peer.setServiceInfo(info);
	}
	
	public void serviceLoggedOut(ServiceID id) {
		Object peer = peers.remove(id);
		if (peer != null)
			print("peer successfully removed.");
		else
			print("peer could not be removed from list.");
	}
	
	public void serviceNameChanged(ServiceID id, String name) {
		Peer peer = (Peer) peers.get(id);
		peer.getServiceInfo().updateName(name);
		//TODO: send notification to UIConsole
		print("\n***\n Received name update from [" 
				+ id.toString() + "]: " + name + "\n***\n");
	}
	
	public void addPeer(Peer aPeer) {
		peers.put(aPeer.getServiceID(), aPeer);
	}
	
	public void updateMyName(String newName) {
		//for all peers, call serviceNameChanged
		
		try {
			Iterator iter = peers.values().iterator();
			while (iter.hasNext()) {
				Peer peer = (Peer) iter.next();
				print("peer: " + peer);
				try {
					peer.getPeerListener().serviceNameChanged(localID, newName);
				} catch (RemoteException re) {
					print("Connection error: " + re.getMessage()); 
				}
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		
	}
	
	public void logout() {
		//for all peers, call serviceLogout
		print("for all peers, call serviceLogout");
		Iterator iter = peers.values().iterator();
		while (iter.hasNext()) {
			Peer peer = (Peer) iter.next();
			try {
				peer.getPeerListener().serviceLogout(localID);
			} catch (RemoteException re) {
				print("Connection error: " + re.getMessage()); 
			}
		}
		//TODO: quit service registration
	}
	
	
}
