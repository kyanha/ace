package ch.iserver.ace.net.jdiscovery;

import java.io.IOException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;

import com.sun.jini.config.ConfigUtil;

import ch.iserver.ace.net.jdiscovery.util.LSUtil;

import net.jini.core.discovery.LookupLocator;
import net.jini.core.lookup.ServiceRegistrar;
import net.jini.core.lookup.ServiceTemplate;
import net.jini.discovery.DiscoveryChangeListener;
import net.jini.discovery.DiscoveryEvent;
import net.jini.discovery.LookupDiscovery;
import net.jini.discovery.LookupDiscoveryManager;

/**
 * @author lukaszbinden
 * @date   Sep 17, 2006 2:37:51 PM
 *
 */
public class PeerCommunicator {
	
//	private LookupDiscoveryManager discovery = null;
	private ServiceTemplate template;
	private LookupDiscovery lDiscovery = null;
	
	public PeerCommunicator() {
		init();
	}
	
	private void init() {
		Class[] serviceTypes = {IDiscoveryListener.class};
	    template = new ServiceTemplate(null, serviceTypes, null);
	}
	
	public void execute() {
		try {
			PeerEventListener eventListener = new PeerEventListener();
//			LookupLocator locator = new LookupLocator(Constants.LS_ADDRESS);
//			discovery = new LookupDiscoveryMa§nager(new String[] {Constants.ACE_GROUP}, 
//					new LookupLocator[] {locator}, eventListener);
			lDiscovery = new LookupDiscovery(new String[] {Constants.ACE_GROUP});
			lDiscovery.addDiscoveryListener(eventListener);
		} catch (IOException ioe) {
			print("Error: " + ioe.getMessage());
		}
	}
	
	private static void print(String msg) {
		LogUtil.print(msg);
	}
	
	
	class PeerEventListener implements DiscoveryChangeListener {

		public void changed(DiscoveryEvent arg0) {
			print("LS changed..., taking no action.");
		}

		public void discarded(DiscoveryEvent arg0) {
			print("LS discarded..., taking no action.");
		}

		public void discovered(DiscoveryEvent event) {
			print("Lookup service discovered");
			ServiceRegistrar[] registrars = event.getRegistrars();
			print("LS has " + registrars.length + " registrars.");
		      for (int i = 0; i < registrars.length; i++) {
		    	    //TODO: how to determine if it's the registrar for the local LS?
		    	  	//If true, don't contact the remote object

		    	  try {
		    		  
		    		  if (!LSUtil.isLocalLS(registrars[i])) {
		    			  print("Register with peer located at " + registrars[i].getLocator().getHost() + ":" + registrars[i].getLocator().getPort());

		    			  try {
		    				  print("wait for 1 second before service lookup...");
		    				  //TODO: fix -> the sleep might be a hack
		    				  Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
		    			  
		    			  registerWithPeer(registrars[i]);
		    		  } else {
			    		  print("Do not register with local peer [" + registrars[i].getLocator().getHost() + "]");
		    		  }

//		    		  if (RegistrationLookupMediator.getInstance().
//		    		  getLocalLookupLocator() != null && !RegistrationLookupMediator.getInstance().
//		    		  getLocalLookupLocator().equals(registrars[i].getLocator())) {
//		    		  print("Register with peer located at " + registrars[i].getLocator().getHost() + ":" + registrars[i].getLocator().getPort()); 
//		    		  registerWithPeer(registrars[i]);
//		    		  } else {
//		    		  print("Do not register with local peeer (myself)");
//		    		  }

		    	  } catch (RemoteException re) {
		    		  print("Error registering with peer: " + re.getMessage());
		    	  } catch (UnknownHostException uhe) {
		    		  print("Error registering with peer: " + uhe.getMessage());
		    	  } 
		      }
		}
		
		private void registerWithPeer(ServiceRegistrar registrar) throws RemoteException {
				Object service = registrar.lookup(template);
				if (service == null)
					print("No matching service found in LS");
				else {
					print("Matching service found");
					IDiscoveryListener peer = (IDiscoveryListener) service;
					ServiceDO myself = RegistrationLookupMediator.getInstance().getServiceDO();
					String peerID = peer.serviceLogon(myself);
					print("received service id from peer: " + peerID);
					RegistrationLookupMediator.getInstance().addPeer(new Peer(peerID, peer));
					new CheckPeerAliveWorker(peerID, peer).start();
				}
		}
	}
	
}
