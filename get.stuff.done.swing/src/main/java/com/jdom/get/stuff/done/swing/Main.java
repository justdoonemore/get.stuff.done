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

import com.jdom.util.patterns.mvp.BaseApplicationView;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplication;

public class Main extends SwingDashboardApplication {
	private static final long serialVersionUID = 952777574369490616L;

	public static void main(String[] args) {
		new Main();
	}

	private Main() {
	}

	@Override
	protected Class<? extends BaseApplicationView<?>> getDashboardPanel(
			SwingDashboardApplication swingView) {
		return DashboardPanel.class;
	}
}
