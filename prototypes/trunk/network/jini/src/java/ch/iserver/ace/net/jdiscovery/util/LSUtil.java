/**
 * 
 */
package ch.iserver.ace.net.jdiscovery.util;

import java.net.UnknownHostException;
import java.rmi.RemoteException;

import net.jini.core.lookup.ServiceRegistrar;

import ch.iserver.ace.net.jdiscovery.LogUtil;

import com.sun.jini.config.ConfigUtil;

/**
 * @author Luke
 *
 */
public class LSUtil {

	public static boolean isLocalLS(ServiceRegistrar registrar) throws RemoteException, 
				UnknownHostException {
		String registrarHost = registrar.getLocator().getHost();
		LogUtil.print("isLocalLS#1: " + registrarHost + " ?= " + ConfigUtil.getHostAddress());
		LogUtil.print("isLocalLS#2: " + registrarHost + " ?= " + ConfigUtil.getHostName());
		return registrarHost.equals(ConfigUtil.getHostAddress()) || 
					registrarHost.equals(ConfigUtil.getHostName());
	}
	
}
