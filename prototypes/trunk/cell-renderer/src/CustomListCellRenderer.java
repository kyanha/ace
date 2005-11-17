import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class CustomListCellRenderer extends JPanel implements ListCellRenderer {

	private Object value;
	
	public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
		this.value = value;
		if (isSelected) {
			this.setBackground(Color.LIGHT_GRAY);
		} else {
			this.setBackground(Color.WHITE);
		}
		return this;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(150, 20);
	}
	
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.BLACK);
		g.drawString(value.toString(), 5, 15);
	}

}
