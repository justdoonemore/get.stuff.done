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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.model.EditTaskModel;
import com.jdom.get.stuff.done.model.impl.EditTaskModelImpl;
import com.jdom.get.stuff.done.presenter.converter.TaskListToTaskNameViewList;
import com.jdom.util.patterns.mvp.ActionConfiguration;
import com.jdom.util.patterns.observer.Subject;

public class EditTaskPresenter extends
		BaseTaskPresenter<EditTaskModel, EditTaskView> {
	private static final TaskListToTaskNameViewList TASK_TO_VIEW_DEPENDENCIES_LIST_CONVERTER = new TaskListToTaskNameViewList();
	static final String TITLE = "Edit Task";
	static final String COMPLETED_LABEL = "Completed:";

	final Runnable saveClickAction = new Runnable() {
		public void run() {
			saveTask();
		}
	};

	final Runnable cancelClickAction = new Runnable() {
		public void run() {
			cancel();
		}
	};

	public static EditTaskPresenter construct(EditTaskView view,
			Properties params) {
		return construct(new EditTaskModelImpl(), view, params);
	}

	static EditTaskPresenter construct(EditTaskModel model, EditTaskView view,
			Properties params) {
		EditTaskPresenter presenter = new EditTaskPresenter(model, view, params);
		model.addObserver(presenter);

		return presenter;
	}

	private EditTaskPresenter(EditTaskModel model, EditTaskView view,
			Properties params) {
		super(model, view, params);
	}

	@Override
	public void instanceInit() {
		super.instanceInit();

		view.setCompletedLabel(COMPLETED_LABEL);

		List<String> taskListNames = ListsPresenter
				.convertListOfListItemsToListOfString(model.getTaskLists());
		view.setTaskLists(taskListNames);
		view.setButtonOptions(getButtonOptions());

		model.setTaskForEdit(params.getProperty(Constants.TASK_FOR_EDIT));
	}

	public void update(Subject subject) {
		Task task = model.getTaskForEdit();

		if (task != null) {
			view.setTaskName(task.getName());
			view.setTaskDescription(task.getDescription());
			view.setSelectedTaskList(task.getTaskList());
			view.setTaskCompleted(task.isCompleted());
			view.setTags(StringUtils.join(new TreeSet<String>(task.getTags()),
					' '));
			view.setOriginalDueDate(getDueDateString(task.getDueDate()));
			view.setDueDate(Constants.ORIGINAL);
		}
	}

	public List<ActionConfiguration> getButtonOptions() {
		ActionConfiguration saveTaskButton = new ActionConfiguration("Save",
				saveClickAction);
		ActionConfiguration cancelButton = new ActionConfiguration("Cancel",
				cancelClickAction);

		return Arrays.asList(saveTaskButton, cancelButton);
	}

	static String getDueDateString(Date dueDate) {
		String dueDateAsString = Constants.NEVER;
		if (!Task.NEVER.equals(dueDate)) {
			dueDateAsString = new SimpleDateFormat(
					AddTaskPresenter.DUE_DATE_PATTERN).format(dueDate);
		}
		return dueDateAsString;
	}

	static List<String> convertTaskDependenciesToViewList(Set<Task> list) {
		return TASK_TO_VIEW_DEPENDENCIES_LIST_CONVERTER.convert(list);
	}

	public void saveTask() {
		if (!validate()) {
			return;
		}

		String newName = view.getTaskName();
		String newDescription = view.getDescription();
		boolean completed = view.getCompleted();
		List<String> newDependencies = getSelectedDependencies();
		String tagsLine = view.getTags().trim();
		Date dueDate = parseDueDate(view.getDueDate());

		List<String> tags = (StringUtils.isEmpty(tagsLine)) ? Collections
				.<String> emptyList() : Arrays.asList(StringUtils.split(
				tagsLine, ' '));

		model.saveTask(newName, newDescription, dueDate, completed,
				view.getTaskList(), newDependencies, new HashSet<String>(tags));

		view.close();
	}

	public void cancel() {
		view.close();
	}

	public void setTaskForEdit(String taskName) {
		model.setTaskForEdit(taskName);
	}

	@Override
	String getTitle() {
		return TITLE;
	}

	@Override
	Collection<String> getCurrentDependencies() {
		return convertTaskDependenciesToViewList(model.getTaskForEdit()
				.getDependencies());
	}

	@Override
	List<String> getDueDateOptions() {
		List<String> options = new ArrayList<String>(DEFAULT_DUE_DATE_OPTIONS);
		options.add(0, Constants.ORIGINAL);
		return options;
	}
}
