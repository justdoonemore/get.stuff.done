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
package com.jdom.get.stuff.done.model.dao;

import java.util.Set;

import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;

public interface ApplicationDao {
	Set<Task> getTasks();

	void storeTasks(Set<Task> tasks);

	void addTask(Task task);

	void deleteTask(Task taskForEdit);

	void updateTask(Task taskForEdit, Task updated);

	Task getTaskByName(String anyString);

	Set<TaskList> getTaskLists();

	Set<String> getTags();

	void addTaskList(TaskList taskList);

	void deleteTaskList(TaskList taskList);

	void enableSyncAccount(String account);

	void disableSyncAccount();

	String getSyncAccount();

	long getLastSyncTime();

	void setLastSyncTime(long time);

	void updateTaskList(TaskList oldVersion, TaskList newVersion);

	boolean isAlreadyDisplayedSyncPrompt();

	void setAlreadyDisplayedSyncPrompt(boolean displayed);

	void setTaskFieldConfiguration(int configuration);

	Integer getTaskFieldConfiguration();

	void setSyncOptionsConfiguration(int configuration);

	Integer getSyncOptionsConfiguration();
}
