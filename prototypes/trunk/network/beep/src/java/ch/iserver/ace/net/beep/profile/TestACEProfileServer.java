package ch.iserver.ace.net.beep.profile;

import org.beepcore.beep.core.ProfileRegistry;
import org.beepcore.beep.transport.tcp.TCPSessionCreator;

public class TestACEProfileServer extends Thread {

	public static final int LISTENING_PORT = 41236;
	
	private ProfileRegistry registry;
	private ACEProfile profile;
	
	public TestACEProfileServer() {
		registry = new ProfileRegistry();
		profile = new ACEProfile();
	}
	
	public void run() {
		try {
			registry.addStartChannelListener(ACEProfile.ACE_URI, profile.init(ACEProfile.ACE_URI, null), null);
			
			while (true) {
				System.out.println("S: listening...");
				TCPSessionCreator.listen(LISTENING_PORT, registry);
				
				System.out.println("S: Client connected.");
			}
			
		} catch (Exception e) {
			System.err.println("TestACEProfileServer stopped. ["+e.getMessage()+"]");
		}
	}
	
	public void terminate() {
		System.out.println("S: terminate");
		interrupt();
	}
}
