/** 
 *  Copyright (C) 2012  Just Do One More
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.jdom.util.patterns.mvp.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class ListDialog extends JDialog implements ActionListener {
	private static final long serialVersionUID = -5778377468594605848L;
	private static ListDialog dialog;
	private static Collection<String> values = Collections.emptyList();
	private final JPanel panel = new JPanel();

	public static Collection<String> showDialog(Component frameComp,
			Component locationComp, String labelText, String title,
			Collection<String> possibleValues,
			Collection<String> initialValues, String longValue) {
		Frame frame = JOptionPane.getFrameForComponent(frameComp);
		dialog = new ListDialog(frame, locationComp, labelText, title,
				possibleValues, initialValues, longValue);
		dialog.setVisible(true);
		return values;
	}

	private ListDialog(Frame frame, Component locationComp, String labelText,
			String title, Collection<String> data,
			Collection<String> initialValues, String longValue) {
		super(frame, title, true);

		final JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(this);

		final JButton setButton = new JButton("Set");
		setButton.setActionCommand("Set");
		setButton.addActionListener(this);
		getRootPane().setDefaultButton(setButton);

		for (String option : data) {
			JCheckBox checkBox = new JCheckBox(option);
			checkBox.setSelected(initialValues.contains(option));
			panel.add(checkBox);
		}

		JScrollPane listScroller = new JScrollPane(panel);
		listScroller.setPreferredSize(new Dimension(250, 80));
		listScroller.setAlignmentX(LEFT_ALIGNMENT);

		JPanel listPane = new JPanel();
		listPane.setLayout(new BoxLayout(listPane, BoxLayout.PAGE_AXIS));
		JLabel label = new JLabel(labelText);

		listPane.add(label);
		listPane.add(Box.createRigidArea(new Dimension(0, 5)));
		listPane.add(listScroller);
		listPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
		buttonPane.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
		buttonPane.add(Box.createHorizontalGlue());
		buttonPane.add(cancelButton);
		buttonPane.add(Box.createRigidArea(new Dimension(10, 0)));
		buttonPane.add(setButton);

		Container contentPane = getContentPane();
		contentPane.add(listPane, BorderLayout.CENTER);
		contentPane.add(buttonPane, BorderLayout.PAGE_END);

		pack();
		setLocationRelativeTo(locationComp);
	}

	public void actionPerformed(ActionEvent e) {
		if ("Set".equals(e.getActionCommand())) {
			Collection<String> selectedValues = new ArrayList<String>();

			for (Component comp : panel.getComponents()) {
				if (comp instanceof JCheckBox) {
					JCheckBox checkbox = (JCheckBox) comp;
					if (checkbox.isSelected()) {
						selectedValues.add(checkbox.getText());
					}
				}
			}

			ListDialog.values = selectedValues;
		}
		ListDialog.dialog.setVisible(false);
	}
}
