/**
 * 
 */
package ch.iserver.ace.net.jdiscovery.util;

import java.net.UnknownHostException;
import java.rmi.RemoteException;

import net.jini.core.lookup.ServiceRegistrar;

import com.sun.jini.config.ConfigUtil;

/**
 * @author Luke
 *
 */
public class LSUtil {

	public static boolean isLocalLS(ServiceRegistrar registrar) throws RemoteException, 
				UnknownHostException {
		return registrar.getLocator().getHost().equals(
				  ConfigUtil.getHostAddress());
	}
	
}
