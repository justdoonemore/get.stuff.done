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
package com.jdom.get.stuff.done.domain;


public final class FilterFactory {

	private FilterFactory() {

	}

	public static FilterOption create(String string) {
		FilterOption retVal = FilterOption.ACCEPT_ALL;

		if (string.startsWith(TagFilterOption.TAG_FILTER_PREFIX)) {
			retVal = TagFilterOption.parse(string);
		} else if (string.startsWith(ListFilterOption.LIST_FILTER_PREFIX)) {
			retVal = ListFilterOption.parse(string);
		} else if (string.startsWith(DueWithinDatesFilterOption.FILTER_PREFIX)) {
			retVal = DueWithinDatesFilterOption.parse(string);
		}

		return retVal;
	}
}
