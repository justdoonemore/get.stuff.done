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
package com.jdom.get.stuff.done.swing;

import java.awt.GridLayout;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.presenter.DashboardPresenter;
import com.jdom.get.stuff.done.presenter.DashboardView;
import com.jdom.util.patterns.mvp.MenuItemConfiguration;
import com.jdom.util.patterns.mvp.RunnableWithResults;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplication;
import com.jdom.util.patterns.mvp.swing.SwingUtils;

public class DashboardPanel extends BasePanel<DashboardPresenter> implements
		DashboardView {
	private static final long serialVersionUID = -2213713615614757572L;

	private final JPanel iconPanel = new JPanel();

	public DashboardPanel(SwingDashboardApplication main, Properties params) {
		super(main, params);

		this.add(iconPanel);
		iconPanel.setLayout(new GridLayout(4, 2));

		getPresenter().init();
	}

	public void setDashboardOptions(List<MenuItemConfiguration> dashboardOptions) {
		iconPanel.removeAll();

		for (final MenuItemConfiguration menuItem : dashboardOptions) {
			JButton shouldBeIcon = new JButton();
			shouldBeIcon.setText(menuItem.getDisplayText());
			shouldBeIcon.addActionListener(SwingUtils
					.getActionListenerForRunnable(menuItem.getClickAction()));

			iconPanel.add(shouldBeIcon);
		}
	}

	@Override
	protected DashboardPresenter setupPresenter(Properties params) {
		return DashboardPresenter.construct(this, params);
	}

	@Override
	protected ContextFactory setupApplicationContextFactory(
			SwingDashboardApplication application) {
		return new SwingApplicationContextFactory(application);
	}

	public void displaySyncAccountPrompt(RunnableWithResults<String> callback) {
		// TODO Auto-generated method stub

	}
}
