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

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jdom.util.patterns.mvp.ApplicationContextFactory;
import com.jdom.util.patterns.mvp.BaseApplicationView;
import com.jdom.util.patterns.mvp.RunnableWithResults;

public class SwingApplicationContextFactory implements
		ApplicationContextFactory {
	protected final SwingDashboardApplication application;

	protected SwingApplicationContextFactory(
			SwingDashboardApplication application) {
		this.application = application;
	}

	public void displayAlert(String msg) {
		JOptionPane.showMessageDialog(application, msg);
	}

	public void displayCollectionOfItemsAsCheckBoxes(
			Collection<String> collection,
			Collection<String> initialSelections,
			RunnableWithResults<Collection<String>> callback) {
		Collection<String> selected = ListDialog.showDialog(application,
				application, "labelText", "title", collection,
				initialSelections, "longValue");
		callback.callback(selected);
	}

	public void displayYesNoConfirmation(String title, String msg,
			Runnable yesAction, Runnable noAction) {
		int result = JOptionPane.showConfirmDialog(application, msg, title,
				JOptionPane.YES_NO_OPTION);

		if (result == JOptionPane.YES_OPTION) {
			yesAction.run();
		} else {
			noAction.run();
		}
	}

	public void launchView(
			Class<? extends BaseApplicationView<? extends ApplicationContextFactory>> viewInterface) {
		launchView(viewInterface, new Properties());
	}

	public void launchView(
			Class<? extends BaseApplicationView<? extends ApplicationContextFactory>> viewInterface,
			Properties params) {
		application.setView(viewInterface, params);
	}

	public String getVersion() {
		return "1.0";
	}

	public void getTextInputForAction(String title, String hintText,
			String doButtonText, String dontButtonText,
			final RunnableWithResults<String> callback) {
		final JTextField taskText = new JTextField(25);
		taskText.setEditable(true);
		JPanel textPanel = new JPanel();
		textPanel.add(taskText);

		JPanel buttonPanel = new JPanel();
		JButton doButton = new JButton(doButtonText);
		JButton dontButton = new JButton(dontButtonText);

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));
		rootPanel.add(textPanel);
		rootPanel.add(buttonPanel);

		buttonPanel.add(doButton);
		buttonPanel.add(dontButton);

		final JDialog dialog = new JDialog(application);
		dialog.add(rootPanel);
		dialog.setModal(true);

		Runnable runnable = new Runnable() {
			public void run() {
				callback.callback(taskText.getText());
				dialog.dispose();
			}
		};

		doButton.addActionListener(SwingUtils
				.getActionListenerForRunnable(runnable));

		dontButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});

		dialog.pack();
		dialog.setVisible(true);

	}

	public void displayCollectionOfItemsAsRadioButtonGroup(String message,
			Collection<String> collection, String initialSelection,
			RunnableWithResults<String> callback) {
		String selected = RadioButtonGroupDialog.showDialog(application,
				application, message, "title", collection, initialSelection,
				"longValue");
		callback.callback(selected);
	}
}
