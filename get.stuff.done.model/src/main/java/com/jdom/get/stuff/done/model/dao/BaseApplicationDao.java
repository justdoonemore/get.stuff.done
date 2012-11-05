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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;

abstract class BaseApplicationDao implements ApplicationDao {

	public Task getTaskByName(String name) {
		for (Task task : getTasks()) {
			if (name.equals(task.getName())) {
				return task;
			}
		}
		throw new IllegalArgumentException("No task found with name [" + name
				+ "]!");
	}

	public Set<String> getTags() {
		Set<String> tags = new TreeSet<String>();

		for (Task task : getTasks()) {
			tags.addAll(task.getTags());
		}

		return tags;
	}

	public void updateTask(Task taskForEdit, Task updated) {
		updated.setLastUpdatedTime(System.currentTimeMillis());
		updated.setRemoteIdentifier(taskForEdit.getRemoteIdentifier());
		storeTasks(updateTaskInternal(taskForEdit, updated));
	}

	protected Set<Task> updateTaskInternal(Task taskForEdit, Task updated) {
		Set<Task> tasks = getTasks();
		for (Task task : tasks) {
			Set<Task> taskDeps = task.getDependencies();
			if (taskDeps.contains(taskForEdit)) {
				task.removeDependency(taskForEdit);
				task.addDependency(updated);
			}
		}

		updated.setLastUpdatedTime(System.currentTimeMillis());
		tasks.remove(taskForEdit);
		tasks.add(updated);

		return tasks;
	}

	public void addTask(Task task) {
		Set<Task> tasks = getTasks();
		task.setLastUpdatedTime(System.currentTimeMillis());
		tasks.remove(task);
		tasks.add(task);
		storeTasks(tasks);
	}

	public void deleteTask(Task taskToDelete) {
		Set<Task> tasks = getTasks();
		tasks.remove(taskToDelete);

		for (Task task : tasks) {
			task.removeDependency(taskToDelete);
		}

		storeTasks(tasks);
	}

	public void addTaskList(TaskList taskList) {
		Set<TaskList> lists = getTaskLists();
		taskList.setLastUpdatedTime(System.currentTimeMillis());
		lists.add(taskList);

		storeTaskLists(lists);
	}

	public void deleteTaskList(TaskList taskList) {
		if (Constants.DEFAULT_LIST.equals(taskList.getName())) {
			throw new IllegalArgumentException("The " + Constants.DEFAULT_LIST
					+ " list cannot be deleted!");
		}

		Set<Task> tasks = getTasks();
		Iterator<Task> iter = tasks.iterator();
		while (iter.hasNext()) {
			Task task = iter.next();
			if (taskList.getName().equals(task.getTaskList())) {
				iter.remove();
			}
		}

		storeTasks(tasks);

		Set<TaskList> lists = getTaskLists();
		lists.remove(taskList);

		storeTaskLists(lists);
	}

	protected abstract void storeTaskLists(Set<TaskList> lists);
}
