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

import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JPanel;

import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.presenter.SettingsPresenter;
import com.jdom.get.stuff.done.presenter.SettingsView;
import com.jdom.util.patterns.mvp.ActionConfiguration;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplication;
import com.jdom.util.patterns.mvp.swing.SwingUtils;

public class SettingsPanel extends BasePanel<SettingsPresenter> implements
		SettingsView {
	private static final long serialVersionUID = 4200887963519472902L;

	private final JPanel labelPanel = new JPanel();
	private final JButton enableSyncButton = new JButton();

	public SettingsPanel(SwingDashboardApplication main, Properties params) {
		super(main, params);

		labelPanel.add(enableSyncButton);
		this.add(labelPanel);

		getPresenter().init();
	}

	@Override
	protected SettingsPresenter setupPresenter(Properties params) {
		return SettingsPresenter.construct(this, params);
	}

	@Override
	protected ContextFactory setupApplicationContextFactory(
			SwingDashboardApplication application) {
		return new SwingApplicationContextFactory(application);
	}

	public void setOptions(List<ActionConfiguration> listItems) {
		this.enableSyncButton.setText(listItems.get(0).getDisplayText());
		this.enableSyncButton
				.addActionListener(SwingUtils
						.getActionListenerForRunnable(listItems.get(0)
								.getClickAction()));
	}
}
