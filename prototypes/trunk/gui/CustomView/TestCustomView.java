import java.io.*;
import javax.swing.*;
import javax.swing.text.*;


public class TestCustomView {

	public TestCustomView() {
	}

	public static void main(String[] args) {
		JFrame f = new JFrame();
		JTextPane textPane = new JTextPane();
		textPane.setEditorKit(new CustomEditorKit());

		try {		
			textPane.getDocument().insertString(0, "hallo", null);
		} catch(BadLocationException ble) {
			ble.printStackTrace();
		}
		
		
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(textPane);
		f.setSize(300, 200);
		f.setVisible(true);

	}
}
