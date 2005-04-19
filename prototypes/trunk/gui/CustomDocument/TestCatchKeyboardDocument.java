import java.io.*;
import javax.swing.*;
import javax.swing.text.*;


public class TestCatchKeyboardDocument {

	public TestCatchKeyboardDocument() {
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		JTextPane textPane = new JTextPane(new CatchKeyboardStyledDocument());
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(textPane);
		f.setSize(300, 200);
		f.setVisible(true);

		
		
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String txt = "";
		
		try {
			while(!txt.equals("exit")) {
				txt = in.readLine();
				((CatchKeyboardStyledDocument)textPane.getStyledDocument()).insertSynchedString(0, txt + "\n", null);
			}
			
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} catch(BadLocationException ble) {
			ble.printStackTrace();
		}

	}
}
