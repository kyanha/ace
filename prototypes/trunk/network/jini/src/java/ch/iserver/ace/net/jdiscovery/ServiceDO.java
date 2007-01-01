package ch.iserver.ace.net.jdiscovery;

import java.io.Serializable;
import java.net.InetAddress;

import net.jini.core.lookup.ServiceID;

/**
 * 
 * @author lukaszbinden
 *
 */
public class ServiceDO implements Serializable{

	ServiceID id;
	String name;
	InetAddress address;
	int port;
	
	/**
	 * @param id
	 * @param name
	 * @param address
	 * @param port
	 */
	public ServiceDO(ServiceID id, String name, InetAddress address, int port) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.port = port;
	}
	
	ServiceID getID() {
		return id;
	}
	
	String getName() {
		return name;
	}
	
	InetAddress getAddress() {
		return address;
	}
	
	int getPort() {
		return port;
	}
	
	void updateName(String name) {
		this.name = name;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return id + " " + name + " " + address.toString() + " " + port;
	}
	
}
