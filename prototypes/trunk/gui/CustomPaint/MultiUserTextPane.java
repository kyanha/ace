import java.net.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;



public class MultiUserTextPane extends JTextPane {

	// data structure
	// - user
	//   - 
	private static int currentUserID;



	private Color currentHighlightColor;
	private Position currentPosition;
	private static Position pos;
	
	private static MultiUserTextPane textPane;
	
	private Color[] userHighlightColors, userCursorColors;
	private static Position[] userCursorPositions;
	


	public MultiUserTextPane() {
		// initialize user data
		addHLText(new Color(220, 220, 255), "i am a\n");
		addHLText(new Color(220, 255, 220), "proto");
		addHLText(new Color(255, 220, 220), "type");
		
		currentUserID = 0;
		
		userHighlightColors = new Color[3];
		userHighlightColors[0] = new Color(220, 220, 255);
		userHighlightColors[1] = new Color(225, 220, 220);
		userHighlightColors[2] = new Color(220, 255, 220);
		
		userCursorColors = new Color[3];
		userCursorColors[0] = new Color(0, 0, 255);
		userCursorColors[1] = new Color(255, 0, 0);
		userCursorColors[2] = new Color(0, 128, 0);
		
		userCursorPositions = new Position[3];
		try {
			userCursorPositions[0] = getDocument().createPosition(3);
			userCursorPositions[1] = getDocument().createPosition(15);
			userCursorPositions[2] = getDocument().createPosition(9);
		} catch(BadLocationException ble) {
			ble.printStackTrace();
		}
		
	}
	
	
	
	public void switchUser(int userID) {
		currentUserID = userID;
		currentHighlightColor = userHighlightColors[userID];
		// set cursor position
		setCaretPosition(userCursorPositions[userID].getOffset());
		requestFocus();
	
		// force repaint
		repaint();
	}



	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		try {
			for(int userID = 0; userID < userCursorColors.length; userID++) {
				if(userID != currentUserID) {
					g.setColor(userCursorColors[userID]);
					Rectangle rect = modelToView(userCursorPositions[userID].getOffset());
					g.drawLine(rect.x-1, rect.y+rect.height-1, rect.x, rect.y+rect.height-2);
					g.drawLine(rect.x+1, rect.y+rect.height-1, rect.x, rect.y+rect.height-2);
					g.drawLine(rect.x-2, rect.y+rect.height-1, rect.x, rect.y+rect.height-3);
					g.drawLine(rect.x+2, rect.y+rect.height-1, rect.x, rect.y+rect.height-3);
				}
			}
		} catch(BadLocationException ble) {
			ble.printStackTrace();
		}
	}





	public void addHLText(Color c, String text) {
		SimpleAttributeSet attrs = new SimpleAttributeSet();
		StyleConstants.setBackground(attrs, c);
		// add the message
		Document doc = getDocument();
		try {
			doc.insertString(doc.getLength(), text, attrs);
		} catch(BadLocationException ble) { ble.printStackTrace(); }
	}






    protected static class MyCaretListener implements CaretListener {
        public MyCaretListener() {
        }

        public void caretUpdate(CaretEvent e) {
            //displaySelectionInfo(e.getDot(), e.getMark());
            //System.out.println(e.getDot());
            try {
    	        userCursorPositions[currentUserID] = textPane.getDocument().createPosition(e.getDot());
			} catch(BadLocationException ble) { ble.printStackTrace(); }
        }
	}







	public static void main(String[] args) throws Exception {
		JFrame f = new JFrame();
		textPane = new MultiUserTextPane();
		textPane.addCaretListener(new MyCaretListener());
		final JButton userButton = new JButton("<html><font color=\"#0000FF\">Blue User</font></html>");
		userButton.addActionListener( new ActionListener() {
			private int userID = 0;
			public void actionPerformed(ActionEvent e) {
				switch(userID) {
					case 0: {
						//caption = RED
						userButton.setText("<html><font color=\"#FF0000\">Red User</font></html>");
						userID = 1;
						break;
					}
					case 1: {
						//caption = GREEN
						userButton.setText("<html><font color=\"#00FF00\">Green User</font></html>");
						userID = 2;
						break;
					}
					case 2: {
						//caption = BLUE
						userButton.setText("<html><font color=\"#0000FF\">Blue User</font></html>");
						userID = 0;
						break;
					}
				}
				textPane.switchUser(userID);
			};
		});
		JPanel panel = new JPanel();
		panel.add(userButton);

		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.getContentPane().add(textPane, BorderLayout.CENTER);
		f.getContentPane().add(panel, BorderLayout.SOUTH);
		f.setSize(300,200);
		f.setVisible(true);
	}
}