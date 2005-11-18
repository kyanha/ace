import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

public class CustomListCellRenderer extends JPanel implements ListCellRenderer {

	private Object value;

	private Icon icon;
	
	public CustomListCellRenderer() {
		this.icon = new ImageIcon(getClass().getResource("view_file_local.gif"));
	}

	public Component getListCellRendererComponent(JList list, Object value,
					int index, boolean isSelected, boolean cellHasFocus) {
		this.value = value;
		if (isSelected) {
			this.setBackground(Color.LIGHT_GRAY);
			this.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
		} else {
			this.setBackground(Color.WHITE);
			this.setBorder(BorderFactory.createEmptyBorder(1,1,1,1));
		}
		return this;
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(150, 20);
	}
	
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		icon.paintIcon(this, g, 0, 2);
		g.setColor(Color.BLACK);
		g.drawString(value.toString(), 25, 15);
	}

}
