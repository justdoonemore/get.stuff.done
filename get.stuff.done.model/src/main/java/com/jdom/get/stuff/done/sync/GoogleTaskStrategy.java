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

import java.io.IOException;

import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;

public class GoogleTaskStrategy implements
		SynchableItemStrategy<Task, GoogleRemoteTask> {
	private ApplicationDao applicationDao;
	private Tasks service;

	public GoogleTaskStrategy() {
	}

	public void saveLocalSynchableItem(Task localTaskList) {
		applicationDao.updateTask(localTaskList, localTaskList);
	}

	public void saveRemoteSynchableItem(GoogleRemoteTask remoteTask)
			throws IOException {
		com.google.api.services.tasks.model.Task task = remoteTask
				.getGoogleTask();

		service.tasks()
				.update(remoteTask.getGoogleTaskList().getId(), task.getId(),
						task).execute();
	}

	public void deleteLocalSynchableItem(Task local) {
		applicationDao.deleteTask(local);
	}

	public void deleteRemoteSynchableItem(GoogleRemoteTask remote)
			throws IOException {
		com.google.api.services.tasks.model.Task task = remote.getGoogleTask();

		service.tasks()
				.delete(remote.getGoogleTaskList().getId(), task.getId())
				.execute();

	}

	public Task createLocalItem(GoogleRemoteTask remote) {
		Task task = new Task(remote.getName(), "", remote.getListName());
		task.setCompleted(remote.isCompleted());
		return task;
	}

	public GoogleRemoteTask createRemoteItem(Task local) throws IOException {
		com.google.api.services.tasks.model.Task task = new com.google.api.services.tasks.model.Task();
		com.google.api.services.tasks.model.TaskList taskList = new com.google.api.services.tasks.model.TaskList();
		taskList.setTitle(local.getListName());
		GoogleRemoteTask remote = new GoogleRemoteTask(task, taskList);
		remote.setCompleted(local.isCompleted());
		remote.setName(local.getName());

		for (TaskList list : service.tasklists().list().execute().getItems()) {
			if (local.getListName().equals(list.getTitle())) {
				task = service.tasks()
						.insert(list.getId(), remote.getGoogleTask()).execute();
				remote = new GoogleRemoteTask(task, list);
			}
		}

		return remote;
	}

	public GoogleRemoteTask modifyRemoteItem(Task local, GoogleRemoteTask remote) {
		remote.setCompleted(local.isCompleted());
		remote.setName(local.getName());
		remote.setListName(local.getListName());
		// googleTask.setDue(new DateTime(task.getDueDate()));
		// googleTask.setUpdated(new DateTime(new Date()));

		return remote;
	}

	public Task modifyLocalItem(GoogleRemoteTask remote, Task local) {
		local.setCompleted(remote.isCompleted());
		local.setName(remote.getName());
		local.setListName(remote.getListName());
		return local;
	}

	public boolean itemsAreDifferent(GoogleRemoteTask remote, Task local) {
		boolean equal = remote.getName().equals(local.getName());

		if (equal) {
			equal = remote.isCompleted() == local.isCompleted();
		}

		if (equal) {
			equal = !remote.getListName().equals(local.getListName());
		}

		return !equal;
	}

	public void setService(Tasks service) {
		this.service = service;
	}

	public void setApplicationDao(ApplicationDao dao) {
		this.applicationDao = dao;
	}
}
