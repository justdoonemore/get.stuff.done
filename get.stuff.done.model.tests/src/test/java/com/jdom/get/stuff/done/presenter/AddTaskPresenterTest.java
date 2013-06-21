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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.AddTaskModel;
import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.mvp.api.ActionConfiguration;

public class AddTaskPresenterTest extends
		BaseTaskPresenterTest<AddTaskModel, AddTaskView, AddTaskPresenter> {

	@Test
	public void testSetsButtonOptionsToViewOnInit() {
		presenter.init();

		verify(view, times(1)).setButtonOptions(
				anyListOf(ActionConfiguration.class));
	}

	@Test
	public void testSetsDueDateOnInit() {
		presenter.init();

		verify(view, times(1)).setDueDate(Constants.NEVER);
	}

	@Test
	public void testCreatesCorrectStringListForAvailableDependencies() {
		Task task1 = new Task("task1", "desc");
		Task task2 = new Task("task2", "desc");
		Set<Task> availableTaskDeps = EditTaskPresenterTest.getDependenciesSet(
				task1, task2);

		List<String> availableDependencies = AddTaskPresenter
				.createListOfAvailableDependencies(availableTaskDeps);

		assertEquals("Incorrect available dependencies size!",
				availableTaskDeps.size(), availableDependencies.size());
		assertTrue("Didn't find a correctly converted dependency!",
				availableDependencies.contains("task1"));
		assertTrue("Didn't find a correctly converted dependency!",
				availableDependencies.contains("task2"));
	}

	@Test
	public void testWhenAddTaskIsCalledRetrievesTaskDetailsFromViewAndSendsToModel() {
		final String taskName = "SomeTask";
		final String description = "SomeDescription";
		final List<String> dependencies = Arrays.asList("task1", "task2");
		final String taskList = "Personal";
		final String tagsLine = "tag1 tag2";
		final String dueDate = "03-17-2012";
		when(view.getTaskName()).thenReturn(taskName);
		when(view.getDescription()).thenReturn(description);
		presenter.selectedDependencies = dependencies;
		when(view.getTaskList()).thenReturn(taskList);
		when(view.getTags()).thenReturn(tagsLine);
		when(view.getDueDate()).thenReturn(dueDate);

		presenter.addTask();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.MARCH);
		cal.set(Calendar.DAY_OF_MONTH, 17);
		cal.set(Calendar.YEAR, 2012);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		verify(model)
				.addTask(
						Matchers.eq(taskName),
						Matchers.eq(description),
						Matchers.eq(taskList),
						Matchers.eq(dependencies),
						Matchers.eq(new HashSet<String>(Arrays.asList("tag1",
								"tag2"))), Matchers.eq(cal.getTime()));
	}

	@Test
	public void testWhenAddTaskIsCalledCallsCloseOnView() {
		final String taskName = "SomeTask";
		final String description = "SomeDescription";
		final List<String> dependencies = Arrays.asList("task1", "task2");
		when(view.getTaskName()).thenReturn(taskName);
		when(view.getDescription()).thenReturn(description);
		presenter.selectedDependencies = dependencies;
		when(view.getTags()).thenReturn("tag1 tag2");
		when(view.getDueDate()).thenReturn(Constants.NEVER);

		presenter.addTask();

		verify(view).close();
	}

	@Test
	public void testAddTaskRequiresNameToBeNotNull() {
		final String taskName = null;
		final String description = "SomeDescription";
		final List<String> dependencies = Arrays.asList("task1", "task2");
		ContextFactory contextFactory = mock(ContextFactory.class);
		when(view.getTaskName()).thenReturn(taskName);
		when(view.getDescription()).thenReturn(description);
		presenter.selectedDependencies = dependencies;
		when(view.getTags()).thenReturn("tag1 tag2");
		when(view.getDueDate()).thenReturn(Constants.NEVER);
		when(view.getApplicationContextFactory()).thenReturn(contextFactory);

		presenter.addTask();

		verify(contextFactory, times(1)).displayAlert(
				AddTaskPresenter.PLEASE_ENTER_A_TASK_NAME);
		verify(model, never()).addTask(anyString(), anyString(), anyString(),
				Mockito.anyListOf(String.class),
				Mockito.anySetOf(String.class), Mockito.<Date> any());
	}

	@Test
	public void testAddTaskRequiresNameToBeNotEmptyString() {
		final String taskName = "";
		final String description = "SomeDescription";
		final List<String> dependencies = Arrays.asList("task1", "task2");
		ContextFactory contextFactory = mock(ContextFactory.class);
		when(view.getTaskName()).thenReturn(taskName);
		when(view.getDescription()).thenReturn(description);
		presenter.selectedDependencies = dependencies;
		when(view.getTags()).thenReturn("tag1 tag2");
		when(view.getDueDate()).thenReturn(Constants.NEVER);
		when(view.getApplicationContextFactory()).thenReturn(contextFactory);

		presenter.addTask();

		verify(contextFactory, times(1)).displayAlert(
				AddTaskPresenter.PLEASE_ENTER_A_TASK_NAME);
		verify(model, never()).addTask(anyString(), anyString(), anyString(),
				Mockito.anyListOf(String.class),
				Mockito.anySetOf(String.class), Mockito.<Date> any());
	}

	@Test
	public void testAddTaskTrimsName() {
		final String taskName = "task1 ";
		final String description = "SomeDescription";
		final List<String> dependencies = Collections.emptyList();
		ContextFactory contextFactory = mock(ContextFactory.class);
		when(view.getTaskName()).thenReturn(taskName);
		when(view.getDescription()).thenReturn(description);
		presenter.selectedDependencies = dependencies;
		when(view.getTags()).thenReturn("tag1 tag2");
		when(view.getDueDate()).thenReturn(Constants.NEVER);
		when(view.getApplicationContextFactory()).thenReturn(contextFactory);

		presenter.addTask();

		verify(model).addTask(Matchers.eq("task1"), anyString(), anyString(),
				Mockito.anyListOf(String.class),
				Mockito.anySetOf(String.class), Mockito.<Date> any());
	}

	@Test
	public void testParsingInvalidDueDateDisplaysErrorMessageToUser() {
		final String taskName = "SomeName";
		final String description = "SomeDescription";
		final List<String> dependencies = Arrays.asList("task1", "task2");
		ContextFactory contextFactory = mock(ContextFactory.class);
		when(view.getTaskName()).thenReturn(taskName);
		when(view.getDescription()).thenReturn(description);
		presenter.selectedDependencies = dependencies;
		when(view.getTags()).thenReturn("tag1 tag2");
		when(view.getDueDate()).thenReturn("invalid");
		when(view.getApplicationContextFactory()).thenReturn(contextFactory);

		presenter.addTask();

		verify(contextFactory, times(1)).displayAlert(
				AddTaskPresenter.PLEASE_ENTER_DATE_WITH_CORRECT_FORMAT);
	}

	@Test
	public void testParsingDueDateOfNeverParsesToMaximumFutureDate() {
		Date expected = Task.NEVER;

		Date neverDue = AddTaskPresenter.parseDueDate(Constants.NEVER);
		assertEquals(
				"A never due date should have resulted in the specified value!",
				expected, neverDue);
	}

	@Test
	public void testCancelInvokesCloseOnView() {
		presenter.cancel();

		verify(view, times(1)).close();
	}

	@Test
	public void testSetsUpAvailableListsOnInitAndInitializesToDefaultList() {
		Set<TaskList> lists = CollectionsUtil.asSet(new TaskList("Inbox"),
				new TaskList("Miscellaneous"));
		when(model.getTaskLists()).thenReturn(lists);

		presenter.init();

		InOrder inOrder = inOrder(view);
		inOrder.verify(view, times(1)).setTaskLists(anyListOf(String.class));
		inOrder.verify(view).setSelectedTaskList(Constants.DEFAULT_LIST);
	}

	@Test
	public void testCorrectlyConfiguredAddButton() {
		List<ActionConfiguration> buttonConfigurations = presenter
				.getButtonOptions();
		ActionConfiguration buttonConfiguration = buttonConfigurations.get(0);
		assertEquals("Incorrect display text for the button!", "Add",
				buttonConfiguration.getDisplayText());
		assertSame(
				"Did not find the expected click action for the button configuration!",
				presenter.addClickAction, buttonConfiguration.getClickAction());
	}

	@Test
	public void testCorrectlyConfiguredCancelButton() {
		List<ActionConfiguration> buttonConfigurations = presenter
				.getButtonOptions();
		ActionConfiguration buttonConfiguration = buttonConfigurations.get(1);
		assertEquals("Incorrect display text for the button!", "Cancel",
				buttonConfiguration.getDisplayText());
		assertSame(
				"Did not find the expected click action for the button configuration!",
				presenter.cancelClickAction,
				buttonConfiguration.getClickAction());
	}

	@Override
	protected Class<AddTaskModel> getModelClass() {
		return AddTaskModel.class;
	}

	@Override
	protected Class<AddTaskView> getViewClass() {
		return AddTaskView.class;
	}

	@Override
	protected AddTaskPresenter getPresenter(AddTaskModel model, AddTaskView view) {
		return AddTaskPresenter.construct(model, view, new Properties());
	}
}
