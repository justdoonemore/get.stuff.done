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
package com.jdom.util.patterns.observer.model.base;

import javax.swing.JPanel;

import com.jdom.util.patterns.observer.Observer;
import com.jdom.util.patterns.observer.Subject;
import com.jdom.util.patterns.observer.base.AbstractModel;

public abstract class AbstractObserverPanel extends JPanel implements Observer {

	/**
	 * serialVersionUID.
	 */
	private static final long serialVersionUID = 5903355647373186232L;

	/**
	 * The model this panel is concerned with.
	 */
	protected AbstractModel model;

	/**
	 * Construct an observer panel that will observe the specified model.
	 * 
	 * @param modelToObserve
	 *            The model to observer.
	 */
	public AbstractObserverPanel(AbstractModel modelToObserve) {
		this.model = modelToObserve;
		modelToObserve.addObserver(this);
	}

	/**
	 * Retrieve the model associated with this view.
	 * 
	 * @return The model this view is associated with.
	 */
	public AbstractModel getModel() {
		return model;
	}

	public void update(Subject subject) {
		// If the subject that sent an update is the model
		// this panel cares about, tell it to update itself
		if (subject == model) {
			updateView();
		}
	}

	/**
	 * This method must be overridden by child classes. It will control the view
	 * updating itself.
	 */
	protected abstract void updateView();

}
