package ch.iserver.ace.net.jdiscovery;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.jini.config.Configuration;
import net.jini.core.discovery.LookupLocator;
import net.jini.core.lease.Lease;
import net.jini.core.lookup.ServiceID;
import net.jini.core.lookup.ServiceItem;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceRegistration;
import net.jini.discovery.DiscoveryChangeListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.LookupDiscoveryManager;
import net.jini.export.Exporter;

import ch.iserver.ace.net.jdiscovery.util.LSUtil;

import com.sun.jini.config.ConfigUtil;

/**
 * Registers a service for discovery in the local lookup service (in-process).
 *
 */
public class ServiceRegistrationHandler {
	
	private static final String CONFIG_COMP = "DiscoveryListener";
	private static final long LEASE_TIME = Lease.FOREVER;
	
	private Configuration config;
	private Remote discoveryListener;
	private ServiceItem serviceItem;
	private LookupDiscoveryManager ldMgr;
	
	
	public ServiceRegistrationHandler(Configuration config) {
		this.config = config;
	}
	
	public void execute() throws Exception {
		// Get server configuration
		//TODO: set codebase and policy via DiscoveryListener.config configuration 
//		String codebase = (String) config.getEntry(CONFIG_COMP, "codebase", String.class);
//		LogUtil.print("ServiceRegistrationHandler: setting codebase to:" + codebase);

//		String policy =	(String) config.getEntry(CONFIG_COMP, "policy", String.class);

		String[] groups = (String[]) config.getEntry(CONFIG_COMP, "groups", String[].class);

		LookupLocator[] locators = (LookupLocator[]) config.getEntry(CONFIG_COMP, "locators", LookupLocator[].class);

		String serviceIdFile = (String) config.getEntry(CONFIG_COMP, "serviceIdFile", String.class);

		discoveryListener = (Remote) config.getEntry(CONFIG_COMP, "discoveryListener", Remote.class);

		Exporter exporter = (Exporter) config.getEntry(CONFIG_COMP, "exporter", Exporter.class);

		// Set System properties -> given via command line arguments
		//System.setProperty("java.rmi.server.codebase", codebase);
		//System.setProperty("java.security.policy", policy);

		// Set a security manager (to load the LS proxy)
//		System.setSecurityManager(new RMISecurityManager());

		// Build service proxy
		Object proxy = exporter.export(discoveryListener);

		// Try to load the service ID from file (not possible
		// the first time the service is run)

		ServiceID id = null;
		try { 
			DataInputStream dis =
				new DataInputStream(new FileInputStream(serviceIdFile));
			id = new ServiceID(dis);
		} catch (FileNotFoundException fnfe) {/* n.a. */ }

		// Build a Jini service item (without attributes)
		serviceItem = new ServiceItem(id, proxy, null);

		// Create a LookupDiscoveryManager.
		LookupDiscoveryManager ldMgr =
			new LookupDiscoveryManager(groups, // Only group ACE
					locators, 			//locate and register only at local LS
					new LSEventListener()); // To get the LS events
	}
	
	public ServiceID getServiceID() {
		return serviceItem.serviceID;
	}
	
	  //Inner class to listen for discovery events
	  // ------------------------------------------
	  class LSEventListener implements DiscoveryChangeListener {
	    
	    // Called when a LS is discovered
	    public void discovered(DiscoveryEvent e) {
	    		print("Lookup service discovered");
	      ServiceRegistrar[] registrars = e.getRegistrars();
	      for (int i = 0; i < registrars.length; i++) {
	    	  	// Register service to LS
	    	  //TODO: make sure that the registration happens only at the local LS
	    	  
	    	  try {
	    		  
	    		  if (LSUtil.isLocalLS(registrars[i])) {
	    			  print("Register service at LS located " + 
	    					  registrars[i].getLocator().getHost() + 
	    					  ":" + registrars[i].getLocator().getPort());
	    			  registerService(registrars[i]);
	    		  } else {
	    			  print("foreign LS, don't register service [" + registrars[i].getLocator().getHost() + "]");
	    		  }
	    	  } catch (RemoteException re) {
				print("Error registering with peer: " + re.getMessage());
			} catch (UnknownHostException uhe) {
				print("Error registering with peer: " + uhe.getMessage());				
			}
	      }
	    }

	    // Called ONLY when a LS is EXPLICITELY discarded
	    public void discarded(DiscoveryEvent e) {
	    		print("LS discarded: " + e);
	    }
	    
	    public void changed(DiscoveryEvent e) {
	    		print("LS changed: " + e);
	    }
	  }
	  
	  // Register one service
	  private void registerService(final ServiceRegistrar registrar) {
	    try {
			final ServiceRegistration registration = registrar.register(serviceItem, LEASE_TIME);
			ServiceID id = registration.getServiceID();
			RegistrationLookupMediator.getInstance().setRegistered(registrar.getLocator());
			
			// If first registration, get and store service ID
	      if (serviceItem.serviceID == null) {
				DataOutputStream dos = new DataOutputStream(
						new FileOutputStream("DiscoveryListener.id"));
				id.writeBytes(dos);
				dos.close();
				serviceItem.serviceID = id;
			}
			print("Service registered - ID: " + serviceItem.serviceID
					+ "; Granted lease: " 
					+ (registration.getLease().getExpiration() - System.currentTimeMillis()) / 1000 + "s.");
		} catch (IOException ioe) {
			print("Error: " + ioe);
		}
	  }
	  
	  private void print(String msg) {
		  LogUtil.print(msg);
	  }
	
	
}
