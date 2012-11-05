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

import java.util.ArrayList;
import java.util.Collection;

import com.jdom.util.patterns.observer.Observer;
import com.jdom.util.patterns.observer.base.AbstractModel;

/**
 * This is the abstract representation of a model. It handles the adding and
 * removing of Observer classes.
 * 
 * @author djohnson
 * 
 */
public abstract class AbstractModelImpl implements AbstractModel {

	/**
	 * The observers for this model.
	 */
	protected Collection<Observer> observers = new ArrayList<Observer>();

	/**
	 * Whether or not to send an update.
	 */
	protected boolean sendUpdate = true;

	public AbstractModelImpl() {

	}

	public boolean addObserver(Observer observer) {
		boolean added = false;

		if (!observers.contains(observer)) {
			added = observers.add(observer);
		}

		// Everytime an observer is added send an update
		// This will allow the newly added observer to get his starting state
		notifyObservers();

		return added;
	}

	public boolean removeObserver(Observer observer) {
		boolean removed = false;

		if (observers.contains(observer)) {
			removed = observers.remove(observer);
		}

		return removed;

	}

	public void notifyObservers() {
		if (isSendUpdate()) {
			for (Observer observer : observers) {
				observer.update(this);
			}
		}
	}

	public boolean isSendUpdate() {
		return sendUpdate;
	}
}
