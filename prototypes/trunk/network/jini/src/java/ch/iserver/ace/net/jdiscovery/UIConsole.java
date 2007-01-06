package ch.iserver.ace.net.jdiscovery;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * @author lukaszbinden
 * @date   Sep 17, 2006 5:06:54 PM
 *
 */
public class UIConsole {

	
	public void execute() {
		try {
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String command = "";
        while (command != null) {
            System.out.print("Enter command (u = update, e = exit gradually, q = exit recklessly) >  ");
            command = in.readLine();
            command = (command == null) ? "continue" : command;
            if (command.equals("u")) {
            		System.out.print("Enter new name >  ");
            		String newName = in.readLine();
            		RegistrationLookupMediator.getInstance().updateMyName(newName);
            } else if (command.equals("e")) {
            		RegistrationLookupMediator.getInstance().logout();
            		System.exit(0);
            } else if (command.equals("q")) {
            		//TODO: perform reckless exit
            		System.exit(0);
            } else {
            		System.out.println("invalid command!");
            }
        }
		} catch (Exception e) {
			LogUtil.print("Error in UIConsole: " + e);
			e.printStackTrace();
			StringWriter strw = new StringWriter();
			PrintWriter writer = new PrintWriter(strw);
			e.printStackTrace(writer);
			writer.close();
			LogUtil.print("Stacktrace:\n" + strw.toString());
		}
	}
	
	
}
