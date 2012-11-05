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

import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.presenter.AboutPresenter;
import com.jdom.get.stuff.done.presenter.AboutView;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplication;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplicationViewPanel;

public class AboutPanel extends
		SwingDashboardApplicationViewPanel<ContextFactory, AboutPresenter>
		implements AboutView {
	private static final long serialVersionUID = 4200887963519472902L;

	private final JPanel versionPanel = new JPanel();
	private final JLabel versionLabel = new JLabel();

	public AboutPanel(SwingDashboardApplication main, Properties params) {
		super(main, params);

		versionPanel.add(versionLabel);
		this.add(versionPanel);

		getPresenter().init();
	}

	public void setVersion(String version) {
		versionLabel.setText(version);
	}

	@Override
	protected AboutPresenter setupPresenter(Properties params) {
		return AboutPresenter.construct(this, params);
	}

	@Override
	protected ContextFactory setupApplicationContextFactory(
			SwingDashboardApplication application) {
		return new SwingApplicationContextFactory(application);
	}

	public void setAppName(String appName) {
		// TODO Auto-generated method stub

	}

	public void setEdition(String edition) {
		// TODO Auto-generated method stub

	}
}
