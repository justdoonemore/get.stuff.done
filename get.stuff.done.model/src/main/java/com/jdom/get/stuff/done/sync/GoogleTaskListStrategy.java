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
import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;

public class GoogleTaskListStrategy implements
		SynchableItemStrategy<TaskList, GoogleRemoteTaskList> {
	private ApplicationDao applicationDao;
	private Tasks service;

	public GoogleTaskListStrategy() {
	}

	public void saveLocalSynchableItem(TaskList localTaskList) {
		applicationDao.updateTaskList(localTaskList, localTaskList);

	}

	public void saveRemoteSynchableItem(GoogleRemoteTaskList remoteTaskList)
			throws IOException {
		com.google.api.services.tasks.model.TaskList task = remoteTaskList
				.getGoogleTaskList();

		service.tasklists().update(remoteTaskList.getId(), task).execute();
	}

	public void deleteLocalSynchableItem(TaskList local) {
		applicationDao.deleteTaskList(local);

	}

	public void deleteRemoteSynchableItem(GoogleRemoteTaskList remote)
			throws IOException {
		service.tasklists().delete(remote.getId()).execute();
	}

	public TaskList createLocalItem(GoogleRemoteTaskList remote) {
		String listName = remote.getName();
		if (GoogleRemoteTaskList.DEFAULT_LIST.equals(listName)) {
			listName = Constants.DEFAULT_LIST;
		}
		TaskList taskList = new TaskList(listName);
		return taskList;
	}

	public GoogleRemoteTaskList createRemoteItem(TaskList local)
			throws IOException {
		com.google.api.services.tasks.model.TaskList taskList = new com.google.api.services.tasks.model.TaskList();
		taskList.setTitle(local.getName());

		GoogleRemoteTaskList remote = new GoogleRemoteTaskList(service
				.tasklists().insert(taskList).execute());

		return remote;
	}

	public GoogleRemoteTaskList modifyRemoteItem(TaskList local,
			GoogleRemoteTaskList remote) {
		remote.setName(local.getName());
		return remote;
	}

	public TaskList modifyLocalItem(GoogleRemoteTaskList remote, TaskList local) {
		local.setName(remote.getName());
		return local;
	}

	public boolean itemsAreDifferent(GoogleRemoteTaskList remote, TaskList local) {
		return !local.getName().equals(remote.getName());
	}

	public void setService(Tasks service) {
		this.service = service;
	}

	public void setApplicationDao(ApplicationDao dao) {
		this.applicationDao = dao;
	}
}