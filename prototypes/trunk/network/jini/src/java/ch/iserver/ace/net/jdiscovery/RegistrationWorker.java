/**
 * 
 */
package ch.iserver.ace.net.jdiscovery;

import net.jini.config.Configuration;

/**
 * @author lukaszbinden
 *
 */
public class RegistrationWorker extends Thread {

	private Configuration config;
	
	public RegistrationWorker(Configuration config) {
		this.config = config;
	}
	
	public void run() {
		try {
			ServiceRegistrationHandler handler = new ServiceRegistrationHandler(config);
			handler.execute();
		} catch (Exception e) {
			LogUtil.print("RegistrationWorker", "Registration failed due to: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
}
