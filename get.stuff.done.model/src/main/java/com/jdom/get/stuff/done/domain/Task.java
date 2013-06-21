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
package com.jdom.get.stuff.done.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.api.client.util.DateTime;
import com.jdom.get.stuff.done.sync.LocalTask;
import com.jdom.util.date.DateUtil;

public class Task implements Comparable<Task>, LocalTask, Cloneable {
	public static final Date NEVER = new Date(0);

	static final Date DEFAULT_DUE_DATE = NEVER;

	private static final String NO_DESCRIPTION = "<No Description>";

	public static final long DEFAULT_LAST_SYNC_TIME = -1L;
	public static final long DEFAULT_LAST_UPDATE_TIME = -1L;

	private String name;

	private final Set<Task> dependencies = new HashSet<Task>();

	private boolean completed;

	private String description;

	private String taskList;

	private final Set<String> tags = new HashSet<String>();

	private Date due = DEFAULT_DUE_DATE;

	private String remoteIdentifier;

	private long lastSyncTime = DEFAULT_LAST_SYNC_TIME;

	private long lastUpdatedTime = DEFAULT_LAST_UPDATE_TIME;

	private boolean deletedFlag;

	public Task(String name) {
		this(name, (String) null);
	}

	public Task(String name, String description) {
		this(name, description, Constants.DEFAULT_LIST);
	}

	public Task(String name, Collection<Task> dependencies) {
		this(name, null, dependencies);
	}

	public Task(String name, String description, Collection<Task> dependencies) {
		this(name, description, Constants.DEFAULT_LIST, dependencies);
	}

	public Task(String taskName, String description, String taskList) {
		this(taskName, description, taskList, new HashSet<Task>());
	}

	public Task(String name, String description, String taskList,
			Collection<Task> dependencies) {
		this.name = name;
		this.description = description;
		this.taskList = taskList;
		if (dependencies != null) {
			this.dependencies.addAll(dependencies);
		}
	}

	public String getName() {
		return name;
	}

	public Set<Task> getDependencies() {
		return Collections.unmodifiableSet(dependencies);
	}

	public void setCompleted(boolean completed) {
		this.completed = completed;
	}

	public boolean isCompleted() {
		return completed;
	}

	public String getDescription() {
		return (description != null) ? description : NO_DESCRIPTION;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTaskList() {
		return taskList;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Task) {
			Task other = (Task) obj;
			EqualsBuilder eqBuilder = new EqualsBuilder();
			eqBuilder.append(this.getName(), other.getName());
			return eqBuilder.isEquals();
		}

		return false;
	}

	public boolean equals(com.google.api.services.tasks.model.Task googleTask,
			String listName) {
		EqualsBuilder eqBuilder = new EqualsBuilder();
		eqBuilder.append(this.isDeleted(),
				Boolean.parseBoolean("" + googleTask.getDeleted()));
		eqBuilder.append(this.isCompleted(),
				"completed".equals(googleTask.getStatus()));
		eqBuilder.append(this.getName(), googleTask.getTitle());
		eqBuilder.append(this.getListName(), listName);

		Date thisDueDate = DateUtil.getDateZeroingHoursAndBelow(getDueDate());
		long raw = 0;
		DateTime googleDue = googleTask.getDue();
		if (googleDue != null) {
			raw = googleDue.getValue();
			raw -= TimeZone.getDefault().getOffset(raw);
		}
		Date thatDueDate = DateUtil.getDateZeroingHoursAndBelow(new Date(raw));

		eqBuilder.append(thisDueDate, thatDueDate);
		return eqBuilder.isEquals();
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcBuilder = new HashCodeBuilder();
		hcBuilder.append(this.getName());

		return hcBuilder.toHashCode();
	}

	@Override
	public String toString() {
		return this.getName() + "::" + this.getDescription();
	}

	public Set<String> getTags() {
		return Collections.unmodifiableSet(tags);
	}

	public void addTag(String tag) {
		this.tags.add(tag.toLowerCase());
	}

	public void addDependency(Task dependency) {
		this.dependencies.add(dependency);
	}

	public void removeDependency(Task dependency) {
		this.dependencies.remove(dependency);
	}

	public int compareTo(Task other) {
		return this.getName().compareTo(other.getName());
	}

	public void addTags(Collection<String> tags) {
		for (String tag : tags) {
			addTag(tag);
		}
	}

	public Date getDueDate() {
		return due;
	}

	public void setDueDate(Date due) {
		this.due = due;
	}

	public void setName(String remoteName) {
		this.name = remoteName;
	}

	public void setRemoteIdentifier(String id) {
		this.remoteIdentifier = id;
	}

	public String getRemoteIdentifier() {
		return remoteIdentifier;
	}

	public long getLastSyncTime() {
		return lastSyncTime;
	}

	public long getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastSyncTime(long time) {
		this.lastSyncTime = time;
	}

	public void setLastUpdatedTime(long time) {
		this.lastUpdatedTime = time;
	}

	public boolean isDeleted() {
		return deletedFlag;
	}

	public void setDeleted(boolean deleted) {
		this.deletedFlag = deleted;
	}

	public void setListName(String list) {
		this.taskList = list;
	}

	public String getListName() {
		return getTaskList();
	}

	public void checkForSettingDefaultValues() {
	}

	@Override
	public Task clone() {
		try {
			return (Task) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

}