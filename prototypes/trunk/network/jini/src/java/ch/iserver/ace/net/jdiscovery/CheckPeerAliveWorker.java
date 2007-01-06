/**
 * 
 */
package ch.iserver.ace.net.jdiscovery;

import java.rmi.RemoteException;

/**
 * @author Luke
 *
 */
public class CheckPeerAliveWorker extends Thread {

	private final IDiscoveryListener peer;
	private final String peerId;
	
	public CheckPeerAliveWorker(String peerId, IDiscoveryListener peer) {
		this.peer = peer;
		this.peerId = peerId;
	}
	
	
	public void run() {
		LogUtil.print("CheckPeerAliveWorker: checks peer " + peerId + " alive...");
		try {
			peer.checkAlive();
		} catch (RemoteException e) {
			LogUtil.print("peer: " + peerId + " disappeared.");
			RegistrationLookupMediator.getInstance().serviceLoggedOut(peerId);
		}
		
	}
}
