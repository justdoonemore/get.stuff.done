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
package com.jdom.get.stuff.done.presenter.converter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.jdom.get.stuff.done.domain.TaskList;

public class TaskListToListNameConverter implements
		Converter<Set<TaskList>, List<String>> {

	public List<String> convert(Set<TaskList> source) {
		List<String> converted = new ArrayList<String>();

		for (TaskList item : source) {
			converted.add(item.getName());
		}

		return converted;
	}

}
