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
package com.jdom.get.stuff.done.presenter;

import java.util.List;

import com.jdom.util.mvp.api.ActionConfiguration;

public interface BaseTaskView extends BaseView {

	void setButtonOptions(List<ActionConfiguration> buttonOptions);

	String getTaskName();

	String getDescription();

	void close();

	void setTaskLists(List<String> taskLists);

	String getTaskList();

	void setSelectedTaskList(String taskList);

	String getTags();

	String getDueDate();

	void setDueDates(List<String> dueDates);

	void setDueDate(String dueDate);

	void setNameLabel(String label);

	void setDescriptionLabel(String label);

	void setDueDateLabel(String label);

	void setDueDateText(String dueDate);

	void setTaskListLabel(String label);

	void setTagsLabel(String label);

	void setDependencySelectionButton(ActionConfiguration actionConfiguration);
}
