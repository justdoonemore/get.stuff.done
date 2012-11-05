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

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

public class ListFilterOption implements FilterOption {
	static final String LIST_FILTER_PREFIX = "List Filter: ";

	private final String listName;

	public ListFilterOption(String listName) {
		this.listName = listName;
	}

	public String getListName() {
		return listName;
	}

	@Override
	public String toString() {
		return LIST_FILTER_PREFIX + listName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ListFilterOption) {
			ListFilterOption other = (ListFilterOption) obj;
			EqualsBuilder eqBuilder = new EqualsBuilder();
			eqBuilder.append(this.getListName(), other.getListName());
			return eqBuilder.isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcBuilder = new HashCodeBuilder();
		hcBuilder.append(this.getListName());

		return hcBuilder.toHashCode();
	}

	public static FilterOption parse(String string) {
		String listName = string.replaceAll(LIST_FILTER_PREFIX, "");
		return new ListFilterOption(listName);
	}

	public boolean accept(Task task) {
		return listName.equals(task.getTaskList());
	}
}
