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
package com.jdom.get.stuff.done.model.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import com.jdom.get.stuff.done.domain.AlphabeticalSortOption;
import com.jdom.get.stuff.done.domain.FilterOption;
import com.jdom.get.stuff.done.domain.ListFilterOption;
import com.jdom.get.stuff.done.domain.SortOption;
import com.jdom.get.stuff.done.domain.TagFilterOption;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.presenter.EditTaskPresenterTest;
import com.jdom.get.stuff.done.presenter.SyncOption;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.mvp.api.Observer;

public class TasksModelImplTest extends
		BaseApplicationModelImplTest<TasksModelImpl> {

	@Test
	public void testGetTasksDoesReturnATaskWithUnmetDependencies() {
		Task taskReadyToWork1 = new Task("task1");
		Task taskReadyToWork2 = new Task("task2");
		Task taskNotReadyToWork = new Task("task3",
				Arrays.asList(taskReadyToWork1));

		Set<Task> tasks = EditTaskPresenterTest.getDependenciesSet(
				taskReadyToWork1, taskReadyToWork2, taskNotReadyToWork);

		when(dao.getTasks()).thenReturn(tasks);

		Set<Task> retrievedTasks = model.getTasks();

		assertTrue(
				"Should have found a task with unmet dependencies in the list!",
				retrievedTasks.contains(taskNotReadyToWork));
	}

	@Test
	public void testGetTasksDoesReturnATaskWithNoDependencies() {
		Task taskReadyToWork1 = new Task("task1");
		Task taskReadyToWork2 = new Task("task2");
		Task taskNotReadyToWork = new Task("task3",
				Arrays.asList(taskReadyToWork1));

		Set<Task> tasks = EditTaskPresenterTest.getDependenciesSet(
				taskReadyToWork1, taskReadyToWork2, taskNotReadyToWork);

		when(dao.getTasks()).thenReturn(tasks);

		Set<Task> tasksReadyToWork = model.getTasks();

		assertTrue(
				"Should have found a task with no dependencies in the  list!",
				tasksReadyToWork.contains(taskReadyToWork1));
		assertTrue(
				"Should have found a task with no dependencies in the  list!",
				tasksReadyToWork.contains(taskReadyToWork2));
	}

	@Test
	public void testGetTasksReadyToWorkDoesReturnATaskWithMetDependencies() {
		Task completedTask = new Task("task1");
		Task taskReadyToWork2 = new Task("task2");
		Task taskWithMetDependencies = new Task("task3",
				Arrays.asList(completedTask));
		completedTask.setCompleted(true);

		Set<Task> tasks = EditTaskPresenterTest.getDependenciesSet(
				completedTask, taskReadyToWork2, taskWithMetDependencies);

		when(dao.getTasks()).thenReturn(tasks);

		Set<Task> retrievedTasks = model.getTasks();

		assertTrue(
				"Should have found a task with all met dependencies in the list!",
				retrievedTasks.contains(taskWithMetDependencies));
	}

	@Test
	public void testDeleteTaskWillDelegateToDao() {
		final String taskName = "taskName";

		Task task = new Task(taskName);
		when(dao.getTaskByName(taskName)).thenReturn(task);

		model.deleteTask(taskName);

		InOrder inOrder = inOrder(dao);
		inOrder.verify(dao).getTaskByName(taskName);
		inOrder.verify(dao).updateTask(same(task), any(Task.class));
	}

	@Test
	public void testDeleteTaskWillNotifyObservers() {
		final String taskName = "taskName";
		Task task = new Task(taskName);
		when(dao.getTaskByName(taskName)).thenReturn(task);

		Observer observer = mock(Observer.class);
		model.addObserver(observer);
		model.deleteTask(taskName);

		verify(observer).update(model);
	}

	@Test
	public void testDeleteTaskWillInvokeSyncStrategy() {
		final String taskName = "taskName";
		Task task = new Task(taskName);
		when(dao.getTaskByName(taskName)).thenReturn(task);

		model.deleteTask(taskName);

		verify(syncStrategy).synchronizeIfEnabled(SyncOption.LOCAL_CHANGE);
	}

	@Test
	public void testSetSortOptionOnModelWillUseSortOptionComparatorForGetTasks() {
		Set<Task> alphabeticalSet = new TreeSet<Task>();
		alphabeticalSet.add(new Task("task1"));
		alphabeticalSet.add(new Task("task2"));
		alphabeticalSet.add(new Task("task3"));
		when(dao.getTasks()).thenReturn(alphabeticalSet);

		model.setSortOption(AlphabeticalSortOption.REVERSE_ALPHABETICAL);
		Set<Task> tasks = model.getTasks();
		Iterator<Task> iter = tasks.iterator();

		assertEquals(
				"Incorrect task name found!  Probably wasn't sorted via the filter!",
				"task3", iter.next().getName());
		assertEquals(
				"Incorrect task name found!  Probably wasn't sorted via the filter!",
				"task2", iter.next().getName());
		assertEquals(
				"Incorrect task name found!  Probably wasn't sorted via the filter!",
				"task1", iter.next().getName());
	}

	@Test
	public void testSetFilterOptionOnModelWillUseFilterToAcceptApplicableTasks() {
		Set<Task> alphabeticalSet = new TreeSet<Task>();
		Task task1 = new Task("task1");
		task1.addTag("tag1");
		Task task3 = new Task("task3");
		task3.addTag("tag1");
		alphabeticalSet.add(task1);
		alphabeticalSet.add(new Task("task2"));
		alphabeticalSet.add(task3);
		when(dao.getTasks()).thenReturn(alphabeticalSet);

		model.setFilterOption(new TagFilterOption("tag1"));
		Set<Task> tasks = model.getTasks();
		assertEquals("Incorrect number of filtered tasks!", 2, tasks.size());

		Iterator<Task> iter = tasks.iterator();

		assertEquals(
				"Incorrect task name found!  Probably wasn't filtered out via the filter!",
				"task1", iter.next().getName());
		assertEquals(
				"Incorrect task name found!  Probably wasn't filtered out via the filter!",
				"task3", iter.next().getName());
	}

	@Test
	public void testSetFilterOptionOnModelWillNotifyObservers() {
		Observer observer = mock(Observer.class);

		model.addObserver(observer);
		model.setFilterOption(FilterOption.ACCEPT_ALL);

		verify(observer).update(model);
	}

	@Test
	public void testSetSortOptionOnModelWillNotifyObservers() {
		Observer observer = mock(Observer.class);

		model.addObserver(observer);
		model.setSortOption(AlphabeticalSortOption.REVERSE_ALPHABETICAL);

		verify(observer).update(model);
	}

	@Test
	public void testToggleTaskAsCompletedWillChangeCompletedStatusToTrueWhenFalse() {
		final String taskName = "taskName";

		Task task = new Task("task", "desc");
		when(dao.getTaskByName(taskName)).thenReturn(task);

		assertFalse(task.isCompleted());
		assertTrue(model.toggleTaskCompleted(taskName).isCompleted());
	}

	@Test
	public void testToggleTaskAsCompletedWillChangeCompletedStatusToFalseWhenTrue() {
		final String taskName = "taskName";

		Task task = new Task("task", "desc");
		task.setCompleted(true);
		when(dao.getTaskByName(taskName)).thenReturn(task);

		assertTrue(task.isCompleted());
		assertFalse(model.toggleTaskCompleted(taskName).isCompleted());
	}

	@Test
	public void testToggleTaskAsCompletedWillUpdateTaskThroughTheDao() {
		final String taskName = "taskName";

		when(dao.getTaskByName(taskName)).thenReturn(new Task("task", "desc"));

		model.toggleTaskCompleted(taskName);

		verify(dao).updateTask(Mockito.<Task> any(), Mockito.<Task> any());
	}

	@Test
	public void testToggleTaskAsCompletedWillNotifyObservers() {
		final String taskName = "taskName";

		when(dao.getTaskByName(taskName)).thenReturn(new Task("task", "desc"));

		Observer observer = mock(Observer.class);
		model.addObserver(observer);
		model.toggleTaskCompleted(taskName);

		verify(observer).update(model);
	}

	@Test
	public void testGetFilterOptionReturnsInstanceVariableValue() {
		model.filterOption = new TagFilterOption("tag1");

		assertEquals(model.filterOption, model.getFilterOption());
	}

	@Test
	public void testGetSortOptionReturnsInstanceVariableValue() {
		model.sortOption = AlphabeticalSortOption.REVERSE_ALPHABETICAL;

		assertEquals(model.sortOption, model.getSortOption());
	}

	@Test
	public void testGetFilterOptionsReturnsAcceptAll() {
		when(dao.getTasks()).thenReturn(Collections.<Task> emptySet());

		List<FilterOption> filterOptions = model.getFilterOptions();

		assertTrue("Did not find the accept all filter option!",
				filterOptions.contains(FilterOption.ACCEPT_ALL));
	}

	@Test
	public void testGetFilterOptionsReturnsFilterForEachTag() {
		Task task = new Task("task1");
		task.addTags(Arrays.asList("tag1", "tag2"));
		Task task2 = new Task("task2");
		task2.addTag("tag3");
		Task task3 = new Task("task3");
		task3.addTag("tag4");

		Set<String> expectedTags = new HashSet<String>();
		expectedTags.addAll(task.getTags());
		expectedTags.addAll(task2.getTags());
		expectedTags.addAll(task3.getTags());

		when(dao.getTasks()).thenReturn(
				CollectionsUtil.asSet(task, task2, task3));

		List<FilterOption> filterOptions = model.getFilterOptions();
		filterOptions.remove(FilterOption.ACCEPT_ALL);
		filterOptions.remove(FilterOption.DUE_TODAY);
		filterOptions.remove(FilterOption.DUE_TOMORROW);
		filterOptions.remove(FilterOption.DUE_THIS_WEEK);

		assertEquals("Incorrect number of tag filters found!", 4,
				filterOptions.size());

		for (FilterOption filterOption : filterOptions) {
			TagFilterOption tagFilterOption = (TagFilterOption) filterOption;
			expectedTags.remove(tagFilterOption.getTagName());
		}

		if (!expectedTags.isEmpty()) {
			fail("Found the following tags without a filter option: "
					+ expectedTags);
		}
	}

	@Test
	public void testGetFilterOptionsReturnsFilterForEachTaskList() {
		when(dao.getTaskLists()).thenReturn(
				CollectionsUtil.asSet(new TaskList("Inbox"), new TaskList(
						"Miscellaneous"), new TaskList("Also")));

		Set<String> expectedTags = new HashSet<String>();
		expectedTags.add("Inbox");
		expectedTags.add("Miscellaneous");
		expectedTags.add("Also");

		when(dao.getTasks())
				.thenReturn(CollectionsUtil.asSet(new Task("blah")));

		List<FilterOption> filterOptions = model.getFilterOptions();
		filterOptions.remove(FilterOption.ACCEPT_ALL);
		filterOptions.remove(FilterOption.ACCEPT_ALL);
		filterOptions.remove(FilterOption.DUE_TODAY);
		filterOptions.remove(FilterOption.DUE_TOMORROW);
		filterOptions.remove(FilterOption.DUE_THIS_WEEK);

		assertEquals("Incorrect number of tag filters found!",
				expectedTags.size(), filterOptions.size());

		for (FilterOption filterOption : filterOptions) {
			ListFilterOption tagFilterOption = (ListFilterOption) filterOption;
			expectedTags.remove(tagFilterOption.getListName());
		}

		if (!expectedTags.isEmpty()) {
			fail("Found the following tags without a filter option: "
					+ expectedTags);
		}
	}

	@Test
	public void testGetSortOptionsReturnsBothAlphabeticalOptions() {
		when(dao.getTasks()).thenReturn(Collections.<Task> emptySet());

		List<SortOption> sortOptions = model.getSortOptions();

		assertTrue("Did not find the sort option!",
				sortOptions.contains(AlphabeticalSortOption.ALPHABETICAL));
		assertTrue("Did not find the sort option!",
				sortOptions
						.contains(AlphabeticalSortOption.REVERSE_ALPHABETICAL));
	}

	@Override
	protected TasksModelImpl getModel() {
		return new TasksModelImpl();
	}
}
