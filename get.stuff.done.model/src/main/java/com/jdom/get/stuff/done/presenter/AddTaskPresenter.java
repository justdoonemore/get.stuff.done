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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.model.AddTaskModel;
import com.jdom.get.stuff.done.model.impl.AddTaskModelImpl;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.mvp.api.ActionConfiguration;
import com.jdom.util.mvp.api.Subject;

public class AddTaskPresenter extends
		BaseTaskPresenter<AddTaskModel, AddTaskView> {

	static final String TITLE = "Add Task";

	final Runnable addClickAction = new Runnable() {
		public void run() {
			addTask();
		}
	};

	final Runnable cancelClickAction = new Runnable() {
		public void run() {
			view.close();
		}
	};

	public static AddTaskPresenter construct(AddTaskView view, Properties params) {
		return construct(new AddTaskModelImpl(), view, params);
	}

	static AddTaskPresenter construct(AddTaskModel model, AddTaskView view,
			Properties params) {
		AddTaskPresenter presenter = new AddTaskPresenter(model, view, params);
		model.addObserver(presenter);

		return presenter;
	}

	private AddTaskPresenter(AddTaskModel model, AddTaskView view,
			Properties params) {
		super(model, view, params);
	}

	@Override
	public void instanceInit() {
		super.instanceInit();

		List<String> taskListNames = ListsPresenter
				.convertListOfListItemsToListOfString(model.getTaskLists());
		view.setTaskLists(taskListNames);
		view.setSelectedTaskList(Constants.DEFAULT_LIST);
		view.setDueDate(Constants.NEVER);
	}

	public void update(Subject subject) {
		view.setButtonOptions(getButtonOptions());
	}

	public List<ActionConfiguration> getButtonOptions() {
		ActionConfiguration addTaskButton = new ActionConfiguration("Add",
				addClickAction);
		ActionConfiguration cancelButton = new ActionConfiguration("Cancel",
				cancelClickAction);

		return Arrays.asList(addTaskButton, cancelButton);
	}

	public void addTask() {
		if (!validate()) {
			return;
		}

		String taskName = view.getTaskName().trim();
		String description = view.getDescription();
		String taskList = view.getTaskList();

		List<String> dependencies = getSelectedDependencies();
		String tagsLine = view.getTags().trim();
		Set<String> tags = CollectionsUtil.asSetFromLine(tagsLine, ' ');
		String dateAsString = view.getDueDate();
		Date dueDate = parseDueDate(dateAsString);

		model.addTask(taskName, description, taskList, dependencies, tags,
				dueDate);

		view.close();
	}

	public void cancel() {
		view.close();
	}

	@Override
	String getTitle() {
		return TITLE;
	}

	@Override
	Collection<String> getCurrentDependencies() {
		return Collections.emptyList();
	}

	@Override
	List<String> getDueDateOptions() {
		return BaseTaskPresenter.DEFAULT_DUE_DATE_OPTIONS;
	}
}
