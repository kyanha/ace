package ch.iserver.ace.net.jdiscovery;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author lukaszbinden
 * @date   Sep 17, 2006 4:44:35 PM
 *
 */
public class Peer {

	private String id;
	private ServiceDO info;
	private IDiscoveryListener peerListener;
	
	public Peer(String id) {
		this.id = id;
	}
	
	public Peer(String id, IDiscoveryListener peer) {
		this(id);
		peerListener = peer;
	}
	
	public void setServiceInfo(ServiceDO info) {
		this.info = info;
	}
	
	public ServiceDO getServiceInfo() {
		return info;
	}
	
	public String getServiceID() {
		return id;
	}
	
	public IDiscoveryListener getPeerListener() {
		return this.peerListener;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	
}
