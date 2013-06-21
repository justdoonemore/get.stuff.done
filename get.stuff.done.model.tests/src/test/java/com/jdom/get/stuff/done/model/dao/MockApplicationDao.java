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
import java.util.TreeSet;

import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.dao.BaseApplicationDao;

public class MockApplicationDao extends BaseApplicationDao {
	Set<Task> tasks = new TreeSet<Task>();
	Set<TaskList> taskLists = new TreeSet<TaskList>();
	String syncAccount;
	private long lastSyncTime;
	private boolean alreadyDisplayedSyncPrompt;
	private Integer taskFieldConfiguration;
	private Integer syncOptionsConfiguration;

	public Set<Task> getTasks() {
		return tasks;
	}

	public void storeTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}

	public Set<TaskList> getTaskLists() {
		return taskLists;
	}

	@Override
	protected void storeTaskLists(Set<TaskList> lists) {
		this.taskLists = lists;
	}

	public void enableSyncAccount(String account) {
		this.syncAccount = account;
	}

	public void disableSyncAccount() {
		this.syncAccount = null;
	}

	public String getSyncAccount() {
		return syncAccount;
	}

	public long getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(long time) {
		this.lastSyncTime = time;
	}

	public void updateTaskList(TaskList localTaskList, TaskList localTaskList2) {
		// TODO Auto-generated method stub

	}

	public boolean isAlreadyDisplayedSyncPrompt() {
		return alreadyDisplayedSyncPrompt;
	}

	public void setAlreadyDisplayedSyncPrompt(boolean displayed) {
		this.alreadyDisplayedSyncPrompt = displayed;
	}

	public void setTaskFieldConfiguration(int configuration) {
		this.taskFieldConfiguration = configuration;
	}

	public Integer getTaskFieldConfiguration() {
		return taskFieldConfiguration;
	}

	public void setSyncOptionsConfiguration(int configuration) {
		this.syncOptionsConfiguration = configuration;
	}

	public Integer getSyncOptionsConfiguration() {
		return this.syncOptionsConfiguration;
	}
}