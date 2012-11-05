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

import java.util.Properties;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jdom.util.patterns.mvp.ApplicationContextFactory;
import com.jdom.util.patterns.mvp.BaseApplicationPresenter;
import com.jdom.util.patterns.mvp.BaseApplicationView;

public abstract class SwingDashboardApplicationViewPanel<CONTEXT_FACTORY extends ApplicationContextFactory, PRESENTER extends BaseApplicationPresenter<CONTEXT_FACTORY, ?, ?>>
		extends JPanel implements BaseApplicationView<CONTEXT_FACTORY> {
	private static final long serialVersionUID = 4019759677845131300L;

	private final PRESENTER presenter;

	protected final SwingDashboardApplication main;

	private final CONTEXT_FACTORY contextFactory;

	private final JPanel titlePanel = new JPanel();

	private final JButton homeButton = new JButton("Home");
	private final JLabel title = new JLabel();
	private final JButton aboutButton = new JButton("i");
	private final JButton addTaskButton = new JButton("+");

	SwingDashboardApplicationViewPanel(SwingDashboardApplication main) {
		this(main, new Properties());
	}

	protected SwingDashboardApplicationViewPanel(
			SwingDashboardApplication main, Properties params) {
		this.main = main;
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		contextFactory = setupApplicationContextFactory(main);

		this.presenter = setupPresenter(params);

		titlePanel.add(homeButton);
		titlePanel.add(title);
		titlePanel.add(addTaskButton);
		titlePanel.add(aboutButton);
		this.add(titlePanel);
	}

	protected abstract PRESENTER setupPresenter(Properties params);

	protected abstract CONTEXT_FACTORY setupApplicationContextFactory(
			SwingDashboardApplication application);

	public CONTEXT_FACTORY getApplicationContextFactory() {
		return contextFactory;
	}

	protected PRESENTER getPresenter() {
		return presenter;
	}

	public void close() {
		main.openDashboard();
	}

	public void setTitleBarTitle(String title) {
		this.title.setText(title);
	}

	public void setTitleBarAboutAction(final Runnable action) {
		this.aboutButton.addActionListener(SwingUtils
				.getActionListenerForRunnable(action));
	}

	public void setTitleBarHomeAction(final Runnable action) {
		this.homeButton.addActionListener(SwingUtils
				.getActionListenerForRunnable(action));
	}

	public void setTitleBarAddTaskAction(Runnable action) {
		this.addTaskButton.addActionListener(SwingUtils
				.getActionListenerForRunnable(action));
	}

}
