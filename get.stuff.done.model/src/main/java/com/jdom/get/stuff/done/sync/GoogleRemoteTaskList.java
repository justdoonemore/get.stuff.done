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

import com.google.api.services.tasks.model.TaskList;
import com.jdom.get.stuff.done.domain.Constants;

public class GoogleRemoteTaskList implements RemoteTaskList {
	public static final String DEFAULT_LIST = "@default";
	private static final String DEFAULT_LIST_TITLE_SUFFIX = "'s list";

	private final TaskList taskList;

	public GoogleRemoteTaskList(TaskList taskList) {
		this.taskList = taskList;
	}

	public String getId() {
		String name = getName();

		String retVal = (Constants.DEFAULT_LIST.equals(name)) ? DEFAULT_LIST
				: taskList.getId();

		return retVal;
	}

	public String getName() {
		String retVal = taskList.getTitle();

		if (retVal.endsWith(DEFAULT_LIST_TITLE_SUFFIX)) {
			retVal = Constants.DEFAULT_LIST;
		}

		return retVal;
	}

	public void setName(String name) {
		this.taskList.setTitle(name);
	}

	public long getLastUpdatedTime() {
		return 0;
	}

	public com.google.api.services.tasks.model.TaskList getGoogleTaskList() {
		return taskList;
	}
}
