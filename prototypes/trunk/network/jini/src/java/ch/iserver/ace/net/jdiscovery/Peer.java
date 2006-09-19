package ch.iserver.ace.net.jdiscovery;

import net.jini.core.lookup.ServiceID;

/**
 * @author lukaszbinden
 * @date   Sep 17, 2006 4:44:35 PM
 *
 */
public class Peer {

	private ServiceID id;
	private ServiceDO info;
	private IDiscoveryListener peerListener;
	
	public Peer(ServiceID id) {
		this.id = id;
	}
	
	public Peer(ServiceID id, IDiscoveryListener peer) {
		this(id);
		peerListener = peer;
	}
	
	public void setServiceInfo(ServiceDO info) {
		this.info = info;
	}
	
	public ServiceDO getServiceInfo() {
		return info;
	}
	
	public ServiceID getServiceID() {
		return id;
	}
	
	public IDiscoveryListener getPeerListener() {
		return this.peerListener;
	}
}
