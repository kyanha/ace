package ch.iserver.ace.net.jdiscovery;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author lukaszbinden
 * @date   Sep 19, 2006 9:12:39 PM
 *
 */
public class LogUtil {

	private static final OutputWriter WRITER;
	
	static {
//		WRITER = new StandardOutputWriter();
		WRITER = new FileOutputWriter("PeerApp.log");
	}
	
	public static void print(String source, String msg) {
		WRITER.write(source + ": " + msg);
	}
	
	public static void print(String msg) {
		WRITER.write(msg);
	}
	
	interface OutputWriter {
		
		void write(String message);
		
	}
	
	static class StandardOutputWriter implements OutputWriter {
		
		public void write(String message) {
			System.out.println(message);
		}
		
	}
	
	static class FileOutputWriter implements OutputWriter {
		
		private BufferedWriter out; 
		
		FileOutputWriter(String filename) {
			try {
		        out = new BufferedWriter(new FileWriter(filename));
		    } catch (IOException e) {
		    		e.printStackTrace();
		    		throw new RuntimeException("could not open stream to file");
		    }
		}
		
		public void write(String message) {
			try {
				out.write("\n");
				out.write(message);
				out.flush();
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException("could not write to file [" + e.getMessage() + "]");
			}
		}
		
		
	}
	
}
