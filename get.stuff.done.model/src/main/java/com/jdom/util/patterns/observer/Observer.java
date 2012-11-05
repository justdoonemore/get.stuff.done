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
 * This is the Observer interface. It allows a component to implement this and
 * register itself for notification events from a Subject.
 * 
 * @author djohnson
 * 
 */
public interface Observer {

	/**
	 * This method is called when the Observer needs to update itself with the
	 * most current information from the Subject.
	 * 
	 * @param subject
	 *            The subject whose state has changed
	 */
	void update(Subject subject);

}
