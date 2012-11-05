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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.jdom.get.stuff.done.domain.AlphabeticalSortOption;
import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.FilterOption;
import com.jdom.get.stuff.done.domain.ListItemConfiguration;
import com.jdom.get.stuff.done.domain.SortOption;
import com.jdom.get.stuff.done.domain.TagFilterOption;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.model.TasksModel;
import com.jdom.get.stuff.done.model.impl.AddTaskModelImplTest;
import com.jdom.get.stuff.done.presenter.EditTaskView;
import com.jdom.get.stuff.done.presenter.TasksPresenter;
import com.jdom.get.stuff.done.presenter.TasksView;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.patterns.mvp.MenuItemConfiguration;

@RunWith(PowerMockRunner.class)
public class TasksPresenterTest extends
		AbstractPresenterTest<TasksModel, TasksView, TasksPresenter> {

	@Test
	public void testListOfTasksIsRetrievedFromModelAndSentToViewOnUpdate() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2");
		task2.setCompleted(true);
		Set<Task> tasks = EditTaskPresenterTest
				.getDependenciesSet(task1, task2);
		Mockito.when(model.getFilterOption()).thenReturn(
				FilterOption.ACCEPT_ALL);
		Mockito.when(model.getTasks()).thenReturn(tasks);

		presenter.update(model);

		Mockito.verify(view, Mockito.times(1)).setTasks(
				Mockito.anyListOf(ListItemConfiguration.class),
				Mockito.eq(new boolean[] { false, true }));
	}

	@Test
	public void testGetListItemConfigurationsForTasksCreatesListItemsWithCorrectAttributes() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2");
		Set<Task> tasksReadyToWork = EditTaskPresenterTest.getDependenciesSet(
				task1, task2);

		List<ListItemConfiguration> listItemConfigurations = TasksPresenter
				.getListItemConfigurationsForTasks(tasksReadyToWork);

		assertEquals("Incorrect display text for task!", "task1",
				listItemConfigurations.get(0).getDisplayText());
		assertEquals("Incorrect display text for task!", "task2",
				listItemConfigurations.get(1).getDisplayText());
	}

	@Test
	public void testGetsFilterOptionsFromModelOnInit() {
		presenter.init();

		Mockito.verify(model, Mockito.times(1)).getFilterOptions();
	}

	@Test
	public void testGetsSortOptionsFromModelOnInit() {
		presenter.init();

		Mockito.verify(model, Mockito.times(1)).getSortOptions();
	}

	@Test
	public void testSetsFilterOptionOnModelOnInit() {
		presenter.init();

		Mockito.verify(model, Mockito.times(1)).setFilterOption(
				TasksPresenter.DEFAULT_FILTER_OPTION);
	}

	@Test
	public void testSetsSortOptionOnModelOnInit() {
		presenter.init();

		Mockito.verify(model, Mockito.times(1)).setSortOption(
				TasksPresenter.DEFAULT_SORT_OPTION);
	}

	@Test
	public void testSetsFilterOptionsWithRunnableToViewOnInit() {
		when(model.getFilterOptions()).thenReturn(
				Arrays.<FilterOption> asList(FilterOption.ACCEPT_ALL));

		presenter.init();

		List<String> expected = Arrays.asList(FilterOption.ACCEPT_ALL
				.toString());

		Mockito.verify(view, Mockito.times(1)).setFilterOptions(
				Matchers.eq(expected),
				Matchers.same(presenter.filterChangedRunnable));
	}

	@Test
	public void testSetsSortOptionsWithRunnableToViewOnInit() {
		when(model.getSortOptions()).thenReturn(
				Arrays.<SortOption> asList(AlphabeticalSortOption.values()));

		presenter.init();

		List<String> expected = Arrays.asList(
				AlphabeticalSortOption.ALPHABETICAL.toString(),
				AlphabeticalSortOption.REVERSE_ALPHABETICAL.toString());

		Mockito.verify(view, Mockito.times(1)).setSortOptions(
				Matchers.eq(expected),
				Matchers.same(presenter.sortChangedRunnable));
	}

	@Test
	public void testSetsSelectedFilterOptionToViewOnUpdate() {
		TagFilterOption value = new TagFilterOption("tag1");
		when(model.getFilterOption()).thenReturn(value);

		presenter.update(model);

		Mockito.verify(view, Mockito.times(1)).setSelectedFilterOption(
				value.toString());
	}

	@Test
	public void testSetsSelectedSortOptionToViewOnUpdate() {
		SortOption value = AlphabeticalSortOption.ALPHABETICAL;
		when(model.getSortOption()).thenReturn(value);

		presenter.update(model);

		Mockito.verify(view, Mockito.times(1)).setSelectedSortOption(
				value.toString());
	}

	@Test
	public void testSetsSelectedItemMenuOptionsToViewOnUpdate() {
		presenter.update(model);

		Mockito.verify(view, Mockito.times(1)).setRightClickMenuItems(
				Mockito.anyListOf(MenuItemConfiguration.class));
	}

	@Test
	public void testGetMenuItemsForMenuOptionsReturnsModelResultsDirectly() {
		List<MenuItemConfiguration> menuOptions = Arrays
				.asList(new MenuItemConfiguration("Add Task", null));

		assertSame(
				"Expected the presenter to return the model results directly in this case!",
				menuOptions,
				TasksPresenter.getMenuItemsForMenuOptions(menuOptions));
	}

	@Test
	public void testGetSelectedItemMenuOptionsReturnsModelResultsDirectly() {
		List<MenuItemConfiguration> selectedItemMenuOptions = Arrays
				.asList(new MenuItemConfiguration("Delete Task", null));

		assertSame(
				"Expected the presenter to return the model results directly in this case!",
				selectedItemMenuOptions, TasksPresenter
						.getSelectedItemMenuOptions(selectedItemMenuOptions));

	}

	@Test
	public void testSetsMultipleItemsSelectedButtonOptionsOnInit() {
		presenter.init();

		verify(view).setMultipleItemsSelectedButtonOptions(
				presenter.multipleItemsSelectedButtonOptions);
	}

	@Test
	public void testEditTaskWillRetrieveSelectedTaskFromView() {
		when(view.getSelectedTasks()).thenReturn(
				CollectionsUtil.asSet("someTask"));

		presenter.editTask();

		Mockito.verify(view, Mockito.times(1)).getSelectedTasks();
	}

	@Test
	public void testDeleteTaskWillDisplayConfirmation() {
		when(view.getSelectedTasks()).thenReturn(
				CollectionsUtil.asSet("someTask"));

		presenter.deleteTask();

		Mockito.verify(contextFactory, Mockito.times(1))
				.displayYesNoConfirmation(
						anyString(),
						anyString(),
						Matchers.same(presenter.confirmedDeleteTaskClickAction),
						Matchers.same(Constants.DO_NOTHING_ACTION));
	}

	@Test
	public void testDeleteTaskWillRequestConfirmation() {
		presenter.deleteTask();

		Mockito.verify(contextFactory, Mockito.times(1))
				.displayYesNoConfirmation(same(Constants.CONFIRM_TITLE),
						anyString(),
						same(presenter.confirmedDeleteTaskClickAction),
						same(Constants.DO_NOTHING_ACTION));
	}

	@Test
	public void testConfirmedDeleteTaskWillRetrieveSelectedTaskFromView() {
		presenter.confirmedDeleteTask();

		Mockito.verify(view, Mockito.times(1)).getSelectedTasks();
	}

	@Test
	public void testConfirmedDeleteTaskWillTellModelDeleteTaskWasConfirmed() {
		when(view.getSelectedTasks()).thenReturn(
				CollectionsUtil.asSet("someTask"));

		presenter.confirmedDeleteTask();

		Mockito.verify(model, Mockito.times(1)).deleteTask(anyString());
	}

	@Test
	public void testToggleTaskAsCompletedInvokesModel() {
		when(view.getSelectedTasks()).thenReturn(
				CollectionsUtil.asSet("someTask"));

		presenter.toggleTaskCompleted();

		verify(model, times(1)).toggleTaskCompleted(anyString());
	}

	@Test
	public void testToggleTaskAsCompletedWillRetrieveSelectedTaskFromView() {
		presenter.toggleTaskCompleted();

		verify(view, times(1)).getSelectedTasks();
	}

	@Test
	public void testFilterChangedWillRetrieveSelectedFilterFromView() {
		when(view.getSelectedFilter()).thenReturn(
				AlphabeticalSortOption.REVERSE_ALPHABETICAL.toString());

		presenter.filterOptionChanged();

		verify(view, times(1)).getSelectedFilter();
	}

	@Test
	public void testFilterChangedWillCreateFilterObjectAndSetOnModel() {
		TagFilterOption tagFilterOption = new TagFilterOption("tag1");
		when(view.getSelectedFilter()).thenReturn(tagFilterOption.toString());

		presenter.filterOptionChanged();

		verify(model, times(1)).setFilterOption(Matchers.eq(tagFilterOption));
	}

	@Test
	public void testInvokingRunnableOnFilterListSelectionWillInvokeFilterChanged() {
		TagFilterOption tagFilterOption = new TagFilterOption("tag1");
		when(view.getSelectedFilter()).thenReturn(tagFilterOption.toString());

		presenter.filterChangedRunnable.run();

		verify(model, times(1)).setFilterOption(Mockito.eq(tagFilterOption));
	}

	@Test
	public void testInvokingRunnableOnSortListSelectionWillInvokeSortOptionChanged() {
		SortOption sortOption = AlphabeticalSortOption.REVERSE_ALPHABETICAL;
		when(view.getSelectedSort()).thenReturn(sortOption.toString());

		presenter.sortChangedRunnable.run();

		verify(model, times(1)).setSortOption(Mockito.eq(sortOption));
	}

	@Test
	public void testSelectedItemMenuItemConfigurationsAreConstructedWithClickActions() {
		List<MenuItemConfiguration> menuOptions = presenter
				.getSelectedItemMenuOptions();

		AddTaskModelImplTest.assertProperMenuItemConfiguration(
				menuOptions.get(0), "Toggle Complete",
				presenter.markTaskCompletedClickAction);
		AddTaskModelImplTest.assertProperMenuItemConfiguration(
				menuOptions.get(1), "Edit Task", presenter.editTaskClickAction);
		AddTaskModelImplTest.assertProperMenuItemConfiguration(
				menuOptions.get(2), "Delete Task",
				presenter.deleteTaskClickAction);
	}

	@Test
	public void testLaunchEditTaskWillInvokeActionFromFactory() {
		when(view.getSelectedTasks()).thenReturn(
				CollectionsUtil.asSet("someTask"));

		presenter.editTask();

		verify(contextFactory, times(1)).launchView(
				Matchers.same(EditTaskView.class),
				Matchers.any(Properties.class));
	}

	@Override
	protected Class<TasksModel> getModelClass() {
		return TasksModel.class;
	}

	@Override
	protected Class<TasksView> getViewClass() {
		return TasksView.class;
	}

	@Override
	protected TasksPresenter getPresenter(TasksModel model, TasksView view) {
		return TasksPresenter.construct(model, view, new Properties());
	}
}
