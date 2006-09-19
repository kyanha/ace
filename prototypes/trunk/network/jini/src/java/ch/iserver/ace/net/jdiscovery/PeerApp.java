package ch.iserver.ace.net.jdiscovery;

import java.rmi.RMISecurityManager;

import net.jini.config.Configuration;
import net.jini.config.ConfigurationProvider;


public class PeerApp {
	
	private static final String LOG_PREFIX = "PeerApp: ";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) {
			print("Usage: java -Djava.security.policy=<reggie-policy-file> -Dsecurity=<security-dir> " +
					"-Dport=<codebase-port> <further options> PeerApp <reggie-config-file> <jini-config-file>");
			System.exit(0);
		} else {
			String argsList = "";
			for (int i=0; i<args.length;i++) 
				argsList += args[i] + " ";
			print("got command line arguments: " + argsList);
		}
		
		try {
			//Set a security manager
			System.setSecurityManager(new RMISecurityManager());
			
			// Start reggie
			InProcessReggie reggie = InProcessReggie.getInstance();
			String[] argsReggie = new String[] { args[0] };
			reggie.start(argsReggie);

		    // Get discovery listener configuration (from Server.config file)
		    Configuration config = ConfigurationProvider.getInstance(new String[]{args[1]});
			
			// Register discovery listener for other peers to register themselves
			new RegistrationWorker(config).start();
			
			//Start search for other peers in the local area network
			//and register yourself with them
			new PeerCommunicatorWorker().start();
			
			//go into user interaction mode
			//new UIConsole().execute();
			
//		    // Make sure that the JVM does not exit			
//			Object keepAlive = new Object();
//	        synchronized(keepAlive) {
//	            try {
//	            	//TODO: to stop peer app call notify() on keepAlive object
//	                keepAlive.wait();
//	            } catch(InterruptedException e) {
//	                // do nothing
//	            }
//	        }
		} catch (Exception e) {
			print("ERROR:\n:" + e);
			e.printStackTrace();
			print("System EXIT.");
		}
	}
	
	public static void print(String msg) {
		System.out.println(LOG_PREFIX + msg);
	}

}
