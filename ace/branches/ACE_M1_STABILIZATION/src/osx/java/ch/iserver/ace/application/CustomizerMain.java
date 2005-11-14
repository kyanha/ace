package ch.iserver.ace.application;

import javax.swing.JFrame;

public class CustomizerMain {
	
	public static void main(String[] args) {
		String customizer = System.getProperty("ch.iserver.ace.customizer");
		if (customizer != null) {
			customize(customizer);
		}
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(300, 300);
		frame.setVisible(true);
	}
	
	private static void customize(String classname) {
		try {
			Class clazz = Class.forName(classname);
			Customizer customizer = (Customizer) clazz.newInstance();
			customizer.init(new ApplicationControllerImpl());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	private static class ApplicationControllerImpl implements ApplicationController {
		public void openFile(String filename) {
			System.out.println("open file: " + filename);
		}
		public void quit() {
			System.out.println("quit");
			System.exit(0);
		}
		public void showAbout() {
			System.out.println("show about");
		}
		public void showPreferences() {
			System.out.println("show preferences");
		}
	}
	
}
