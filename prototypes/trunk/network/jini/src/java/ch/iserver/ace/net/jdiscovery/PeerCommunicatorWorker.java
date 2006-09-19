package ch.iserver.ace.net.jdiscovery;

/**
 * @author lukaszbinden
 * @date   Sep 17, 2006 3:33:16 PM
 *
 */
public class PeerCommunicatorWorker extends Thread {

	
	public void run() {
		new PeerCommunicator().execute();
	}
	
	
}
