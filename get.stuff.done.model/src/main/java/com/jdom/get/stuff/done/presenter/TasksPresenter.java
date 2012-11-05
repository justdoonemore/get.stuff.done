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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.jdom.get.stuff.done.domain.AlphabeticalSortOption;
import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.FilterFactory;
import com.jdom.get.stuff.done.domain.FilterOption;
import com.jdom.get.stuff.done.domain.ListItemConfiguration;
import com.jdom.get.stuff.done.domain.SortOption;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.model.TasksModel;
import com.jdom.get.stuff.done.model.impl.TasksModelImpl;
import com.jdom.get.stuff.done.presenter.converter.Converter;
import com.jdom.get.stuff.done.presenter.converter.TaskToListItemConverter;
import com.jdom.util.patterns.mvp.MenuItemConfiguration;
import com.jdom.util.patterns.observer.Subject;

public class TasksPresenter extends BasePresenter<TasksModel, TasksView> {

	static final FilterOption DEFAULT_FILTER_OPTION = FilterOption.ACCEPT_ALL;

	static final SortOption DEFAULT_SORT_OPTION = AlphabeticalSortOption.ALPHABETICAL;

	private static final TaskToListItemConverter TASK_TO_LIST_ITEM_CONVERTER = new TaskToListItemConverter();

	private static final String TITLE = "Tasks";

	final Runnable filterChangedRunnable = new Runnable() {
		public void run() {
			filterOptionChanged();
		}
	};

	final Runnable sortChangedRunnable = new Runnable() {
		public void run() {
			sortOptionChanged();
		}
	};

	final Runnable markTaskCompletedClickAction = new Runnable() {

		public void run() {
			toggleTaskCompleted();
		}
	};

	final Runnable editTaskClickAction = new Runnable() {
		public void run() {
			editTask();
		}
	};

	final Runnable deleteTaskClickAction = new Runnable() {
		public void run() {
			deleteTask();
		}
	};

	final Runnable confirmedDeleteTaskClickAction = new Runnable() {
		public void run() {
			confirmedDeleteTask();
		}
	};

	final Runnable toggleCompleteRunnable = new Runnable() {
		public void run() {
			multipleToggleComplete();
		}
	};

	final Runnable deleteRunnable = new Runnable() {
		public void run() {
			multipleDelete();
		}
	};

	public List<MenuItemConfiguration> multipleItemsSelectedButtonOptions = Arrays
			.asList(new MenuItemConfiguration(Constants.TOGGLE_COMPLETE,
					toggleCompleteRunnable), new MenuItemConfiguration(
					Constants.DELETE, deleteRunnable));

	public static TasksPresenter construct(TasksView view, Properties params) {
		return construct(new TasksModelImpl(), view, params);
	}

	static TasksPresenter construct(TasksModel model, TasksView view,
			Properties params) {
		TasksPresenter presenter = new TasksPresenter(model, view, params);
		model.addObserver(presenter);

		return presenter;
	}

	private TasksPresenter(TasksModel model, TasksView view, Properties params) {
		super(model, view, params);
	}

	@Override
	public void instanceInit() {
		view.setFilterOptions(
				convertFilterOptionsToStringList(model.getFilterOptions()),
				filterChangedRunnable);
		view.setSortOptions(
				convertSortOptionsToStringList(model.getSortOptions()),
				sortChangedRunnable);
		view.setMultipleItemsSelectedButtonOptions(multipleItemsSelectedButtonOptions);

		String filterToSetAsString = params.getProperty(
				Constants.FILTER_OPTION, DEFAULT_FILTER_OPTION.toString());
		setModelFilter(filterToSetAsString);

		model.setSortOption(TasksPresenter.DEFAULT_SORT_OPTION);
	}

	static List<ListItemConfiguration> getListItemConfigurationsForTasks(
			Collection<Task> tasks) {
		Iterator<Task> iter = tasks.iterator();
		while (iter.hasNext()) {
			Task task = iter.next();
			if (task.isDeleted()) {
				iter.remove();
			}
		}

		return convertDomainObjectToDisplayObject(tasks,
				TASK_TO_LIST_ITEM_CONVERTER);
	}

	static List<MenuItemConfiguration> getMenuItemsForMenuOptions(
			List<MenuItemConfiguration> menuOptions) {
		return menuOptions;
	}

	public static List<MenuItemConfiguration> getSelectedItemMenuOptions(
			List<MenuItemConfiguration> selectedItemMenuOptions) {
		return selectedItemMenuOptions;
	}

	private static <SOURCE_TYPE, DESTINATION_TYPE> List<DESTINATION_TYPE> convertDomainObjectToDisplayObject(
			Collection<SOURCE_TYPE> sourceList,
			Converter<SOURCE_TYPE, DESTINATION_TYPE> converter) {
		List<DESTINATION_TYPE> destinationList = new ArrayList<DESTINATION_TYPE>(
				sourceList.size());

		for (SOURCE_TYPE source : sourceList) {
			destinationList.add(converter.convert(source));
		}

		return destinationList;
	}

	static List<String> convertFilterOptionsToStringList(
			List<FilterOption> filtersList) {
		List<String> filters = new ArrayList<String>();

		for (FilterOption filter : filtersList) {
			filters.add(filter.toString());
		}

		return filters;
	}

	static List<String> convertSortOptionsToStringList(
			List<SortOption> sortOptions) {
		List<String> sortStrings = new ArrayList<String>();

		for (SortOption sortOption : sortOptions) {
			sortStrings.add(sortOption.toString());
		}

		return sortStrings;
	}

	public void update(Subject subject) {
		view.setSelectedFilterOption("" + model.getFilterOption());
		view.setSelectedSortOption("" + model.getSortOption());
		Set<Task> tasks = model.getTasks();
		view.setTasks(getListItemConfigurationsForTasks(tasks),
				getCompletedFlags(tasks));
		view.setRightClickMenuItems(getSelectedItemMenuOptions());
	}

	private boolean[] getCompletedFlags(Set<Task> tasks) {
		Iterator<Task> iter = tasks.iterator();
		boolean[] completedFlags = new boolean[tasks.size()];
		for (int i = 0; i < tasks.size(); i++) {
			Task task = iter.next();
			completedFlags[i] = task.isCompleted();
		}
		return completedFlags;
	}

	public List<MenuItemConfiguration> getSelectedItemMenuOptions() {
		List<MenuItemConfiguration> menuItemConfigurations = new ArrayList<MenuItemConfiguration>();

		menuItemConfigurations.add(new MenuItemConfiguration("Toggle Complete",
				markTaskCompletedClickAction));
		menuItemConfigurations.add(new MenuItemConfiguration("Edit Task",
				editTaskClickAction));
		menuItemConfigurations.add(new MenuItemConfiguration("Delete Task",
				deleteTaskClickAction));

		return menuItemConfigurations;
	}

	public void editTask() {
		String selectedTask = view.getSelectedTasks().iterator().next();
		Properties params = new Properties();
		params.setProperty(Constants.TASK_FOR_EDIT, selectedTask);
		view.getApplicationContextFactory().launchView(EditTaskView.class,
				params);
	}

	public void deleteTask() {
		view.getApplicationContextFactory().displayYesNoConfirmation(
				Constants.CONFIRM_TITLE,
				"Are you sure you want to delete the selected task(s)?",
				confirmedDeleteTaskClickAction, Constants.DO_NOTHING_ACTION);
	}

	public void confirmedDeleteTask() {
		Set<String> selectedTasks = view.getSelectedTasks();
		for (String selectedTask : selectedTasks) {
			model.deleteTask(selectedTask);
		}
	}

	public void toggleTaskCompleted() {
		Set<String> selectedTask = view.getSelectedTasks();
		for (String task : selectedTask) {
			model.toggleTaskCompleted(task);
		}
	}

	void filterOptionChanged() {
		String selectedFilter = view.getSelectedFilter();
		setModelFilter(selectedFilter);
	}

	void sortOptionChanged() {
		String selectedSort = view.getSelectedSort();
		SortOption sortOption = AlphabeticalSortOption.parse(selectedSort);
		model.setSortOption(sortOption);
	}

	private void setModelFilter(String selectedFilter) {
		FilterOption filter = FilterFactory.create(selectedFilter);
		model.setFilterOption(filter);
	}

	protected void multipleDelete() {
		view.getApplicationContextFactory().displayYesNoConfirmation(
				Constants.CONFIRM_TITLE,
				"Are you sure you want to delete the selected task(s)?",
				confirmedDeleteTaskClickAction, Constants.DO_NOTHING_ACTION);
	}

	protected void multipleToggleComplete() {
		for (String task : view.getSelectedTasks()) {
			model.toggleTaskCompleted(task);
		}
	}

	@Override
	String getTitle() {
		return TITLE;
	}
}
