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
package com.jdom.get.stuff.done.model.impl;

import java.util.List;
import java.util.Set;

import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.util.mvp.api.BaseApplicationModelImpl;

public class BaseTaskModelImpl extends BaseApplicationModelImpl<ContextFactory> {

	protected void addTaskDependenciesFromList(List<String> dependencies,
			Task task) {
		if (dependencies != null && !dependencies.isEmpty()) {
			for (Task possibleDep : applicationContextFactory.getDaoFactory()
					.getApplicationDao().getTasks()) {
				if (dependencies.contains(possibleDep.getName())) {
					task.addDependency(possibleDep);
				}
			}
		}
	}

	public Set<TaskList> getTaskLists() {
		return applicationContextFactory.getDaoFactory().getApplicationDao()
				.getTaskLists();
	}

	public Set<Task> getAvailableDependencies() {
		return applicationContextFactory.getDaoFactory().getApplicationDao()
				.getTasks();
	}
}
