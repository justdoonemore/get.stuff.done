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
package com.jdom.util.patterns.mvp;

import java.util.Properties;

import com.jdom.util.patterns.observer.Observer;

public abstract class BaseApplicationPresenter<CONTEXT_FACTORY extends ApplicationContextFactory, MODEL extends BaseApplicationModel<CONTEXT_FACTORY>, VIEW extends BaseApplicationView<CONTEXT_FACTORY>>
		implements Observer {
	protected final MODEL model;
	protected final VIEW view;
	protected final Properties params;

	protected BaseApplicationPresenter(MODEL model, VIEW view, Properties params) {
		this.model = model;
		this.view = view;
		this.params = params;
		this.model.setApplicationContextFactory(view
				.getApplicationContextFactory());
	}

	public abstract void init();
}
