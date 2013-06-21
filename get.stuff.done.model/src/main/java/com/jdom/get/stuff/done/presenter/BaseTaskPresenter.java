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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.model.BaseTaskModel;
import com.jdom.util.date.DateUtil;
import com.jdom.util.mvp.api.ActionConfiguration;
import com.jdom.util.mvp.api.RunnableWithResults;
import com.jdom.util.time.TimeConstants;

public abstract class BaseTaskPresenter<MODEL extends BaseTaskModel, VIEW extends BaseTaskView>
		extends BasePresenter<MODEL, VIEW> {

	protected static final List<String> DEFAULT_DUE_DATE_OPTIONS = Arrays
			.asList(Constants.NEVER, Constants.TODAY, Constants.TOMORROW,
					Constants.NEXT_WEEK, Constants.SPECIFIC);
	static final String NAME_LABEL = "Name:";
	static final String DESCRIPTION_LABEL = "Description:";
	static final String DUE_DATE_LABEL = "Due:";
	static final String TASK_LIST_LABEL = "Task List:";
	static final String TAGS_LABEL = "Tags:";
	static final String DUE_DATE_PATTERN = "MM-dd-yyyy";
	static final String SELECT_DEPENDENCIES = "Dependencies";
	private static final String PLEASE_ENTER = "Please enter ";
	static final String PLEASE_ENTER_A_TASK_NAME = PLEASE_ENTER
			+ "a task name.";
	static final String PLEASE_ENTER_DATE_WITH_CORRECT_FORMAT = PLEASE_ENTER
			+ "a date with format " + DUE_DATE_PATTERN;

	Runnable selectDependenciesAction = new Runnable() {
		public void run() {
			displayDependenciesPopup();
		}
	};

	Collection<String> selectedDependencies;

	protected BaseTaskPresenter(MODEL model, VIEW view, Properties params) {
		super(model, view, params);
	}

	@Override
	public void instanceInit() {
		view.setNameLabel(NAME_LABEL);
		view.setDescriptionLabel(DESCRIPTION_LABEL);
		view.setDueDateLabel(DUE_DATE_LABEL);
		view.setTaskListLabel(TASK_LIST_LABEL);
		view.setTagsLabel(TAGS_LABEL);
		view.setDueDates(getDueDateOptions());
		view.setDependencySelectionButton(new ActionConfiguration(
				BaseTaskPresenter.SELECT_DEPENDENCIES, selectDependenciesAction));
	}

	boolean validate() {
		boolean valid = false;
		String taskName = view.getTaskName();
		String dateAsString = view.getDueDate();

		if (taskName == null || StringUtils.isEmpty(taskName.trim())) {
			view.getApplicationContextFactory().displayAlert(
					PLEASE_ENTER_A_TASK_NAME);
		} else {
			try {
				parseDueDate(dateAsString);
				valid = true;
			} catch (IllegalArgumentException iae) {
				view.getApplicationContextFactory().displayAlert(
						PLEASE_ENTER_DATE_WITH_CORRECT_FORMAT);
			}
		}

		return valid;
	}

	public void dueDateSelection(String dateSelection) {
		String dueDateAsString = new SimpleDateFormat(DUE_DATE_PATTERN)
				.format(parseDueDate(dateSelection));
		if (Constants.NEVER.equals(dateSelection)) {
			dueDateAsString = Constants.NEVER;
		}
		view.setDueDateText(dueDateAsString);
	}

	static Date parseDueDate(String dateAsString) {
		Date dueDate = null;
		if (Constants.NEVER.equals(dateAsString)) {
			dueDate = Task.NEVER;
		} else if (Constants.TODAY.equals(dateAsString)) {
			dueDate = DateUtil.getCurrentDateZeroingHoursAndBelow();
		} else if (Constants.TOMORROW.equals(dateAsString)) {
			dueDate = new Date(DateUtil.getCurrentDateZeroingHoursAndBelow()
					.getTime() + TimeConstants.MILLIS_PER_DAY);
		} else if (Constants.NEXT_WEEK.equals(dateAsString)) {
			dueDate = new Date(DateUtil.getCurrentDateZeroingHoursAndBelow()
					.getTime() + (TimeConstants.MILLIS_PER_DAY * 7));
		} else {
			try {
				dueDate = new SimpleDateFormat(DUE_DATE_PATTERN)
						.parse(dateAsString);
			} catch (ParseException e) {
				throw new IllegalArgumentException(
						"Unable to parse the due date!", e);
			}
		}

		return dueDate;
	}

	public static List<String> createListOfAvailableDependencies(
			Set<Task> availableTaskDeps) {

		List<String> dependencies = new ArrayList<String>();

		if (availableTaskDeps != null) {
			for (Task task : availableTaskDeps) {
				dependencies.add(task.getName());
			}
		}
		return dependencies;
	}

	private void displayDependenciesPopup() {

		// If no dependencies have been changed, initialize with the view
		// specified dependencies
		if (this.selectedDependencies == null) {
			selectedDependencies = getCurrentDependencies();
		}

		List<String> available = createListOfAvailableDependencies(model
				.getAvailableDependencies());
		RunnableWithResults<Collection<String>> runnable = new RunnableWithResults<Collection<String>>() {
			public void callback(Collection<String> results) {
				setDependencies(results);
			}
		};

		view.getApplicationContextFactory()
				.displayCollectionOfItemsAsCheckBoxes(available,
						selectedDependencies, runnable);
	}

	abstract Collection<String> getCurrentDependencies();

	abstract List<String> getDueDateOptions();

	private void setDependencies(Collection<String> results) {
		this.selectedDependencies = results;
	}

	protected List<String> getSelectedDependencies() {
		return (this.selectedDependencies == null) ? Collections
				.<String> emptyList() : new ArrayList<String>(
				this.selectedDependencies);
	}
}
