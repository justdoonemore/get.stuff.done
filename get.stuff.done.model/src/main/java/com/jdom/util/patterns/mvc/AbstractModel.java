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
package com.jdom.util.patterns.mvc;

import java.util.ArrayList;
import java.util.List;

import com.jdom.util.mvp.api.Observer;
import com.jdom.util.mvp.api.Subject;

public abstract class AbstractModel implements Subject {

	private final List<Observer> observers = new ArrayList<Observer>();

	public boolean addObserver(Observer observer) {
		if (!observers.contains(observer)) {
			return observers.add(observer);
		}
		return false;
	}

	public boolean removeObserver(Observer observer) {
		if (observers.contains(observer)) {
			return observers.remove(observer);
		}
		return false;
	}

	protected List<Observer> getObservers() {
		return observers;
	}

	public void notifyObservers() {
		for (Observer observer : observers) {
			observer.update(this);
		}
	}
}
