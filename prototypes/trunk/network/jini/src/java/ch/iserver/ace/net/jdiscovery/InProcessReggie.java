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
			LogUtil.print("InProcessReggie: start class server and reggie...");
			ServiceStarter.main(args);
			LogUtil.print("InProcessReggie: done.");
			isStarted = true;
			return true;
		
		}
		return false;	
	}

}
