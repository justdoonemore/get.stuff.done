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

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.model.AddTaskModel;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.presenter.SyncOption;

public class AddTaskModelImpl extends BaseTaskModelImpl implements AddTaskModel {

	public void addTask(String taskName, String description, String taskList,
			List<String> dependencies, Set<String> tags, Date due) {
		Task task = new Task(taskName, description, taskList);
		task.addTags(tags);
		task.setDueDate(due);
		addTaskDependenciesFromList(dependencies, task);

		ApplicationDao dao = applicationContextFactory.getDaoFactory()
				.getApplicationDao();
		dao.addTask(task);

		applicationContextFactory.getSyncStrategy().synchronizeIfEnabled(
				SyncOption.LOCAL_CHANGE);
	}
}
