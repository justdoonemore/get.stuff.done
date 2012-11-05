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

import java.util.Set;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.model.ListsModel;
import com.jdom.get.stuff.done.presenter.SyncOption;
import com.jdom.util.patterns.mvp.BaseApplicationModelImpl;

public class ListsModelImpl extends BaseApplicationModelImpl<ContextFactory>
		implements ListsModel {

	public static final String CANT_DELETE_DEFAULT_LIST = "The "
			+ Constants.DEFAULT_LIST + " list cannot be deleted!";

	public Set<TaskList> getListListItems() {
		Set<TaskList> taskLists = applicationContextFactory.getDaoFactory()
				.getApplicationDao().getTaskLists();

		java.util.Iterator<TaskList> iter = taskLists.iterator();
		while (iter.hasNext()) {
			TaskList taskList = iter.next();
			if (taskList.isDeleted()) {
				iter.remove();
			}
		}

		return taskLists;
	}

	public void addList(String listName) {
		TaskList taskList = new TaskList(listName);
		applicationContextFactory.getDaoFactory().getApplicationDao()
				.addTaskList(taskList);

		notifyObservers();

		applicationContextFactory.getSyncStrategy().synchronizeIfEnabled(
				SyncOption.LOCAL_CHANGE);
	}

	public void deleteList(String name) {
		try {
			if (Constants.DEFAULT_LIST.equals(name)) {
				throw new IllegalArgumentException(CANT_DELETE_DEFAULT_LIST);
			}
			Set<TaskList> taskLists = applicationContextFactory.getDaoFactory()
					.getApplicationDao().getTaskLists();

			for (TaskList taskList : taskLists) {
				if (taskList.getName().equals(name)) {
					TaskList newVersion = taskList.clone();
					newVersion.setDeleted(true);
					applicationContextFactory.getDaoFactory()
							.getApplicationDao()
							.updateTaskList(taskList, newVersion);
				}
			}

			notifyObservers();

			applicationContextFactory.getSyncStrategy().synchronizeIfEnabled(
					SyncOption.LOCAL_CHANGE);
		} catch (IllegalArgumentException iae) {
			applicationContextFactory.displayAlert(iae.getMessage());
		}
	}
}
