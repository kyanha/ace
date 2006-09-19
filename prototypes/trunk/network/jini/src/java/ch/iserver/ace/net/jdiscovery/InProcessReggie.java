/**
 * 
 */
package ch.iserver.ace.net.jdiscovery;

import com.sun.jini.start.ServiceStarter;

/**
 * @author lukaszbinden
 *
 */
public class InProcessReggie {

	private static InProcessReggie instance; 
	private boolean isStarted = false;
	
	
	public static InProcessReggie getInstance() {
		if (instance == null) {
			instance = new InProcessReggie();
		}
		return instance;
	}
	
	public boolean start(String[] args) {
		if (!isStarted) {
			print("start class server and reggie...");
			ServiceStarter.main(args);
			print("done.");
			isStarted = true;
			return true;
		
		}
		return false;	
	}
	
	private static void print(String msg) {
		PeerApp.print(msg);
	}

}
