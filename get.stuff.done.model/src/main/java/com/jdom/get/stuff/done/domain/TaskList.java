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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jdom.get.stuff.done.sync.GoogleRemoteTaskList;
import com.jdom.get.stuff.done.sync.LocalTaskList;

public class TaskList implements LocalTaskList, Comparable<TaskList>, Cloneable {
	public static final long DEFAULT_LAST_SYNC_TIME = -1L;
	public static final long DEFAULT_LAST_UPDATE_TIME = -1L;
	private String name;
	private String remoteId;
	private long lastSyncTime = DEFAULT_LAST_SYNC_TIME;
	private long lastUpdatedTime = DEFAULT_LAST_UPDATE_TIME;
	private boolean deleted;

	public TaskList(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setRemoteIdentifier(String id) {
		this.remoteId = id;
	}

	public String getRemoteIdentifier() {
		if (Constants.DEFAULT_LIST.equals(getName())) {
			return GoogleRemoteTaskList.DEFAULT_LIST;
		}
		return remoteId;
	}

	public long getLastSyncTime() {
		return lastSyncTime;
	}

	public void setLastSyncTime(long time) {
		this.lastSyncTime = time;
	}

	public long getLastUpdatedTime() {
		return lastUpdatedTime;
	}

	public void setLastUpdatedTime(long time) {
		this.lastUpdatedTime = time;
	}

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TaskList) {
			TaskList other = (TaskList) obj;
			EqualsBuilder eqBuilder = new EqualsBuilder();
			eqBuilder.append(this.getName(), other.getName());
			return eqBuilder.isEquals();
		}
		return false;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcBuilder = new HashCodeBuilder();
		hcBuilder.append(this.getName());

		return hcBuilder.toHashCode();
	}

	@Override
	public String toString() {
		return this.getName();
	}

	public int compareTo(TaskList other) {
		return this.getName().compareTo(other.getName());
	}

	public void checkForSettingDefaultValues() {
		if (Constants.DEFAULT_LIST.equals(name)) {
			this.remoteId = GoogleRemoteTaskList.DEFAULT_LIST;
		}
	}

	@Override
	public TaskList clone() {
		try {
			return (TaskList) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new IllegalStateException(e);
		}
	}

}