import javax.swing.*;
import javax.swing.text.*;


public class CatchKeyboardStyledDocument extends DefaultStyledDocument {

	public CatchKeyboardStyledDocument() {
	}
	
	public void insertSynchedString(int offs, String str, AttributeSet a) throws BadLocationException {
		super.insertString(offs, str, a);
	}
	
	public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
		System.out.println("from JTextPane (insert): " + str);
	}
	
	public void remove(int offs, int len) throws BadLocationException {
		super.remove(offs, len);
	}
	
	/*public void createPosition(int offs) throws BadLocationException {
		super.createPosition(offs);
	}*/

}
