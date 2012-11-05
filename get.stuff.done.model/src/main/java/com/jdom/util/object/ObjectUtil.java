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
package com.jdom.util.object;

import java.lang.reflect.Array;

public class ObjectUtil {

	private ObjectUtil() {

	}

	public static <T> T[] wrapObjectInArray(T objectToWrap) {
		@SuppressWarnings("unchecked")
		Class<T> payloadClass = (Class<T>) objectToWrap.getClass();

		// Create the array to wrap the object
		Object wrapperArray = Array.newInstance(payloadClass, 1);

		// Set the object on the array
		Array.set(wrapperArray, 0, objectToWrap);

		@SuppressWarnings("unchecked")
		T[] retVal = (T[]) wrapperArray;

		return retVal;
	}
}
