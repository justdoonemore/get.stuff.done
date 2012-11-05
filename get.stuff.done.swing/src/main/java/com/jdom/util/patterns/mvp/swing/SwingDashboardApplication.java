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

import java.lang.reflect.Constructor;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.jdom.util.patterns.mvp.BaseApplicationView;

public abstract class SwingDashboardApplication extends JFrame {

	private static final long serialVersionUID = 3430387860949573653L;

	protected SwingDashboardApplication() {
		init();
	}

	private void init() {
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		openDashboard();
	}

	public void openDashboard() {
		setView(getDashboardPanel(this), new Properties());
	}

	@SuppressWarnings("unchecked")
	public void setView(Class<? extends BaseApplicationView<?>> viewInterface,
			Properties params) {
		String implementationPackage = this.getClass().getPackage().getName();
		String implementationName = viewInterface.getSimpleName().replace(
				"View", "Panel");
		Class<? extends JPanel> implClass;
		try {

			implClass = (Class<? extends JPanel>) Class
					.forName(implementationPackage + "." + implementationName);
			Constructor<? extends JPanel> constructor = implClass
					.getConstructor(SwingDashboardApplication.class,
							Properties.class);
			JPanel instance = constructor.newInstance(this, params);
			openView(instance);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(
					"Unable to find activity class for the specified view interface!",
					e);
		} catch (Exception e) {
			throw new IllegalArgumentException(
					"Unable to construct the specified view implementation!", e);
		}

	}

	private void openView(JPanel view) {
		this.getContentPane().removeAll();
		this.getContentPane().add(view);

		this.pack();
		this.setVisible(true);
	}

	protected abstract Class<? extends BaseApplicationView<?>> getDashboardPanel(
			SwingDashboardApplication swingView);
}
