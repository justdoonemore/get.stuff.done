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
package com.jdom.get.stuff.done.presenter;

import java.util.Properties;

import com.jdom.get.stuff.done.model.AboutModel;
import com.jdom.get.stuff.done.model.impl.AboutModelImpl;
import com.jdom.util.patterns.observer.Subject;

public class AboutPresenter extends BasePresenter<AboutModel, AboutView> {

	static final String TITLE = "About";

	public static AboutPresenter construct(AboutView view, Properties params) {
		return construct(new AboutModelImpl(), view, params);
	}

	static AboutPresenter construct(AboutModel model, AboutView view,
			Properties params) {
		AboutPresenter presenter = new AboutPresenter(model, view, params);
		model.addObserver(presenter);
		return presenter;
	}

	private AboutPresenter(AboutModel model, AboutView view, Properties params) {
		super(model, view, params);
	}

	public void update(Subject subject) {
		// Nothing to do at this time
	}

	@Override
	public void instanceInit() {
		view.setVersion(view.getApplicationContextFactory().getVersion());
		view.setAppName(getAppName());
		view.setEdition(getEdition());
	}

	@Override
	String getTitle() {
		return TITLE;
	}

}
