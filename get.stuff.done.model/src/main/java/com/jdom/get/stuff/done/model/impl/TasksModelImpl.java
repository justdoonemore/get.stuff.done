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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.jdom.get.stuff.done.domain.AlphabeticalSortOption;
import com.jdom.get.stuff.done.domain.FilterOption;
import com.jdom.get.stuff.done.domain.ListFilterOption;
import com.jdom.get.stuff.done.domain.SortOption;
import com.jdom.get.stuff.done.domain.TagFilterOption;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.model.TasksModel;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.presenter.SyncOption;
import com.jdom.util.mvp.api.BaseApplicationModelImpl;

public class TasksModelImpl extends BaseApplicationModelImpl<ContextFactory>
		implements TasksModel {

	FilterOption filterOption = FilterOption.ACCEPT_ALL;

	SortOption sortOption = AlphabeticalSortOption.ALPHABETICAL;

	public Set<Task> getTasks() {
		ApplicationDao taskDao = getDao();

		Set<Task> tasks = taskDao.getTasks();

		Set<Task> oldSet = tasks;
		tasks = new TreeSet<Task>(sortOption.getComparator());
		for (Task task : oldSet) {
			if (filterOption == null || filterOption.accept(task)) {
				tasks.add(task);
			}
		}

		return tasks;
	}

	public void deleteTask(String taskName) {
		ApplicationDao dao = getDao();
		Task task = dao.getTaskByName(taskName);
		Task updated = task.clone();
		updated.setDeleted(true);
		dao.updateTask(task, updated);

		notifyObservers();

		applicationContextFactory.getSyncStrategy().synchronizeIfEnabled(
				SyncOption.LOCAL_CHANGE);
	}

	public Task toggleTaskCompleted(String taskName) {
		ApplicationDao dao = getDao();
		Task task = dao.getTaskByName(taskName);
		task.setCompleted(!task.isCompleted());

		dao.updateTask(dao.getTaskByName(taskName), task);

		notifyObservers();

		applicationContextFactory.getSyncStrategy().synchronizeIfEnabled(
				SyncOption.LOCAL_CHANGE);

		return task;
	}

	public void setFilterOption(FilterOption filterOption) {
		this.filterOption = filterOption;

		notifyObservers();
	}

	public FilterOption getFilterOption() {
		return filterOption;
	}

	public List<FilterOption> getFilterOptions() {
		List<FilterOption> filterOptions = new ArrayList<FilterOption>();
		filterOptions.add(FilterOption.ACCEPT_ALL);
		filterOptions.add(FilterOption.DUE_TODAY);
		filterOptions.add(FilterOption.DUE_TOMORROW);
		filterOptions.add(FilterOption.DUE_THIS_WEEK);

		ApplicationDao dao = getDao();

		Set<TaskList> taskLists = dao.getTaskLists();
		for (TaskList taskList : taskLists) {
			filterOptions.add(new ListFilterOption(taskList.getName()));
		}

		Set<Task> tasks = dao.getTasks();
		Set<String> tagNames = new TreeSet<String>();

		for (Task task : tasks) {
			tagNames.addAll(task.getTags());
		}

		for (String tag : tagNames) {
			filterOptions.add(new TagFilterOption(tag));
		}

		return filterOptions;
	}

	private ApplicationDao getDao() {
		return applicationContextFactory.getDaoFactory().getApplicationDao();
	}

	public void setSortOption(SortOption sortOption) {
		this.sortOption = sortOption;

		notifyObservers();
	}

	public List<SortOption> getSortOptions() {
		return Arrays.<SortOption> asList(AlphabeticalSortOption.values());
	}

	public SortOption getSortOption() {
		return sortOption;
	}
}
