package ch.iserver.ace.net.jdiscovery;

import java.io.Serializable;
import java.net.InetAddress;

/**
 * 
 * @author lukaszbinden
 *
 */
public class ServiceDO implements Serializable{

	String id;
	String name;
	InetAddress address;
	int port;
	
	/**
	 * @param id
	 * @param name
	 * @param address
	 * @param port
	 */
	public ServiceDO(String id, String name, InetAddress address, int port) {
		this.id = id;
		this.name = name;
		this.address = address;
		this.port = port;
	}
	
	String getID() {
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
