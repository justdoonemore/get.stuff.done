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
import com.jdom.get.stuff.done.model.EditTaskModel;
import com.jdom.get.stuff.done.presenter.SyncOption;

public class EditTaskModelImpl extends BaseTaskModelImpl implements
		EditTaskModel {

	Task taskForEdit;

	@Override
	public Set<Task> getAvailableDependencies() {
		if (taskForEdit == null) {
			throw new IllegalStateException(
					"You must call setTaskForEdit() prior to getting available dependencies!");
		}

		Set<Task> allTasks = super.getAvailableDependencies();

		allTasks.remove(taskForEdit);

		return allTasks;
	}

	public void setTaskForEdit(Task task) {
		this.taskForEdit = task;

		notifyObservers();
	}

	public Task getTaskForEdit() {
		return taskForEdit;
	}

	public Task saveTask(String newName, String newDescription, Date dueDate,
			boolean completed, String taskList, List<String> newDependencies,
			Set<String> tags) {

		Task task = new Task(newName, newDescription, taskList);
		task.setDueDate(dueDate);
		task.setCompleted(completed);
		task.addTags(tags);
		task.setRemoteIdentifier(taskForEdit.getRemoteIdentifier());
		addTaskDependenciesFromList(newDependencies, task);

		applicationContextFactory.getDaoFactory().getApplicationDao()
				.updateTask(getTaskForEdit(), task);

		applicationContextFactory.getSyncStrategy().synchronizeIfEnabled(
				SyncOption.LOCAL_CHANGE);

		return task;
	}

	public void setTaskForEdit(String taskNameToEdit) {
		Task task = applicationContextFactory.getDaoFactory()
				.getApplicationDao().getTaskByName(taskNameToEdit);
		setTaskForEdit(task);
	}
}
