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
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.api.services.tasks.model.TaskLists;
import com.google.api.services.tasks.model.Tasks;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;

public abstract class GoogleSynchronizeStrategy
		extends
		BaseSynchronizeStrategy<Task, GoogleRemoteTask, TaskList, GoogleRemoteTaskList> {
	private static final GoogleTaskListStrategy TASK_LIST_STRATEGY = new GoogleTaskListStrategy();
	private static final GoogleTaskStrategy TASK_STRATEGY = new GoogleTaskStrategy();
	private final ApplicationDao applicationDao;
	private com.google.api.services.tasks.Tasks service;

	public GoogleSynchronizeStrategy(ApplicationDao applicationDao) {
		super(TASK_LIST_STRATEGY, TASK_STRATEGY);

		this.applicationDao = applicationDao;

		TASK_LIST_STRATEGY.setApplicationDao(applicationDao);
		TASK_STRATEGY.setApplicationDao(applicationDao);
	}

	@Override
	protected List<GoogleRemoteTaskList> getRemoteTaskLists()
			throws IOException {
		service = getTasksService();
		TASK_LIST_STRATEGY.setService(service);
		TASK_STRATEGY.setService(service);

		List<GoogleRemoteTaskList> wrappedRemoteTaskLists = new ArrayList<GoogleRemoteTaskList>();

		TaskLists lists = service.tasklists().list().execute();
		for (com.google.api.services.tasks.model.TaskList gtl : lists
				.getItems()) {
			// Skip null or empty title lists
			if (StringUtils.isEmpty(gtl.getTitle())) {
				continue;
			}
			wrappedRemoteTaskLists.add(new GoogleRemoteTaskList(gtl));
		}
		return wrappedRemoteTaskLists;
	}

	@Override
	protected List<TaskList> getLocalTaskLists() {
		return new ArrayList<TaskList>(applicationDao.getTaskLists());
	}

	@Override
	protected List<GoogleRemoteTask> getRemoteTasks() throws IOException {
		List<GoogleRemoteTask> wrappedRemoteTasks = new ArrayList<GoogleRemoteTask>();

		service = getTasksService();
		TASK_LIST_STRATEGY.setService(service);
		TASK_STRATEGY.setService(service);

		TaskLists lists = service.tasklists().list().execute();
		for (com.google.api.services.tasks.model.TaskList gtl : lists
				.getItems()) {
			// Skip null or empty title lists
			if (StringUtils.isEmpty(gtl.getTitle())) {
				continue;
			}
			String id = gtl.getId();
			Tasks tasks = service.tasks().list(id).execute();

			List<com.google.api.services.tasks.model.Task> items = tasks
					.getItems();
			if (items == null) {
				continue;
			}
			for (com.google.api.services.tasks.model.Task gt : items) {
				if (StringUtils.isEmpty(gt.getTitle())) {
					continue;
				}
				wrappedRemoteTasks.add(new GoogleRemoteTask(gt, gtl));
			}

		}
		return wrappedRemoteTasks;
	}

	@Override
	protected List<Task> getLocalTasks() {
		return new ArrayList<Task>(applicationDao.getTasks());
	}

	protected abstract List<com.google.api.services.tasks.model.TaskList> getRemoteTaskListsInternal()
			throws IOException;

	protected abstract com.google.api.services.tasks.Tasks getTasksService();
}
