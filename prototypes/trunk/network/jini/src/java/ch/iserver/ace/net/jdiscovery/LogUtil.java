package ch.iserver.ace.net.jdiscovery;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * @author lukaszbinden
 * @date   Sep 19, 2006 9:12:39 PM
 *
 */
public class LogUtil {

	private static final OutputWriter WRITER;
	private static final DateFormat FORMATTER;
	private static final int THREAD_NAME_LENGTH = 20;
	
	static {
//		WRITER = new StandardOutputWriter();
		WRITER = new FileOutputWriter("PeerApp.log");
		FORMATTER = new SimpleDateFormat("HH:mm:ss.SSS");
	}
	
	public static void print(String source, String msg) {
		printImpl(getLogPrefix() + source + ": " + msg);
	}
	
	public static void print(String msg) {
		printImpl(getLogPrefix() + msg);
	}
	
	private final static String getLogPrefix() {
		String prefix = "";		
		prefix += FORMATTER.format(new Date());
		prefix += "\t";
		String threadName = Thread.currentThread().getName();
		if (threadName.length() > THREAD_NAME_LENGTH) {
			threadName = "." + threadName.substring(threadName.length() - THREAD_NAME_LENGTH + 1);
		} else {
			threadName = StringUtils.rightPad(threadName, THREAD_NAME_LENGTH);
		}
		prefix += "[" + threadName + "]";
		prefix += "\t";
		return prefix;
	}
	
	private final static void printImpl(String msg) {
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
		        out = new BufferedWriter(new FileWriter(filename, true));
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
