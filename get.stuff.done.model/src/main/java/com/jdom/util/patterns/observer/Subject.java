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
package com.jdom.util.patterns.observer;

/**
 * This is the Subject interface. It allows a component to be observed, and to
 * notify observers when its state changes.
 * 
 * @author djohnson
 * 
 */
public interface Subject {

	/**
	 * Add an observer to the list of observers.
	 * 
	 * @param observer
	 *            The observer to add
	 * @return Whether or not this observer was added successfully.
	 */
	boolean addObserver(Observer observer);

	/**
	 * Remove an observer from the list of observers.
	 * 
	 * @param observer
	 *            The observer to remove
	 * @return Whether or not this observer was removed successfully.
	 */
	boolean removeObserver(Observer observer);

	/**
	 * Notify observers a state change occured.
	 */
	void notifyObservers();

}
