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
package com.jdom.get.stuff.done.sync;

import com.google.api.services.tasks.model.Task;
import com.google.api.services.tasks.model.TaskList;

public class GoogleRemoteTask implements RemoteTask {
	private static final String NEEDS_ACTION = "needsAction";
	private static final String COMPLETED = "completed";

	private final Task task;

	private final TaskList taskList;

	private String newListName;

	public GoogleRemoteTask(Task task, TaskList taskList) {
		this.task = task;
		this.taskList = taskList;
	}

	public boolean isCompleted() {
		return COMPLETED.equals(task.getStatus());
	}

	public void setCompleted(boolean completed) {
		String status = (completed) ? COMPLETED : NEEDS_ACTION;
		task.setStatus(status);
	}

	public String getId() {
		return task.getId();
	}

	public String getName() {
		return task.getTitle();
	}

	public void setName(String name) {
		task.setTitle(name);
	}

	public long getLastUpdatedTime() {
		// TODO: Do I need to specify the timezone or offset for it?
		return task.getUpdated().getValue();
	}

	public String getListName() {
		return taskList.getTitle();
	}

	public void setListName(String listName) {
		if (listName != null && !getListName().equals(listName)) {
			newListName = listName;
		}
	}

	/**
	 * Used as a flag for the GoogleSyncStrategy to know to change the list this
	 * task belongs to.
	 * 
	 * @return the new list name this task should be attached to
	 */
	String getNewListName() {
		return newListName;
	}

	public Task getGoogleTask() {
		return task;
	}

	public TaskList getGoogleTaskList() {
		return taskList;
	}
}
