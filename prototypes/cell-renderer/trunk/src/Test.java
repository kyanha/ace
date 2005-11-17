import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class Test extends JFrame {

	public Test() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		final DefaultListModel model = new DefaultListModel();
		
		final JList list = new JList(model);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.setCellRenderer(new CustomListCellRenderer());

		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton add = new JButton("add");
		add.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Integer el = new Integer((int) (Math.random() * 10000000));
				model.addElement(el);
				list.setSelectedValue(el, true);
			}
		});
		JButton remove = new JButton("remove");
		remove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int index = list.getSelectedIndex();
				if (index != -1) {
					model.remove(index);
				}
			}
		});
		buttons.add(add);
		buttons.add(remove);
		
		JScrollPane pane = new JScrollPane(list);
		getContentPane().add(pane, BorderLayout.CENTER);
		getContentPane().add(buttons, BorderLayout.SOUTH);
		
		setSize(200, 150);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new Test();
	}

}
