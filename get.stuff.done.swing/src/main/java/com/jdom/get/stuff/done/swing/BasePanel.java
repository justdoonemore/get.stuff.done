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

import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.util.patterns.mvp.BaseApplicationPresenter;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplication;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplicationViewPanel;

public abstract class BasePanel<PRESENTER extends BaseApplicationPresenter<ContextFactory, ?, ?>>
		extends SwingDashboardApplicationViewPanel<ContextFactory, PRESENTER> {
	private static final long serialVersionUID = 1L;

	BasePanel(SwingDashboardApplication main, Properties params) {
		super(main, params);
	}
}
