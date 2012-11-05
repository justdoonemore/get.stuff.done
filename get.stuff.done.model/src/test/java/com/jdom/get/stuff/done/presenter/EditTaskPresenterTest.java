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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.model.EditTaskModel;
import com.jdom.get.stuff.done.presenter.AddTaskPresenter;
import com.jdom.get.stuff.done.presenter.BaseTaskPresenter;
import com.jdom.get.stuff.done.presenter.EditTaskPresenter;
import com.jdom.get.stuff.done.presenter.EditTaskView;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.patterns.mvp.ActionConfiguration;

public class EditTaskPresenterTest extends
		BaseTaskPresenterTest<EditTaskModel, EditTaskView, EditTaskPresenter> {

	private final Task taskForEdit = new Task("task1");

	@Override
	public void setUp() {
		super.setUp();
		when(model.getTaskForEdit()).thenReturn(taskForEdit);
	}

	@Test
	public void testSetsButtonOptionsToViewOnInit() {
		Task task = new Task("task1", "somedescription");
		when(model.getTaskForEdit()).thenReturn(task);

		presenter.init();

		verify(view, times(1)).setButtonOptions(
				anyListOf(ActionConfiguration.class));
	}

	@Test
	public void testSetsUpCompletedLabelOnInit() {
		presenter.init();

		verify(view, times(1)).setCompletedLabel(
				EditTaskPresenter.COMPLETED_LABEL);
	}

	@Test
	public void testSetsUpAvailableListsOnInit() {
		Set<TaskList> lists = CollectionsUtil.asSet(new TaskList("Inbox"),
				new TaskList("Miscellaneous"));
		when(model.getTaskLists()).thenReturn(lists);

		presenter.init();

		verify(view, times(1)).setTaskLists(anyListOf(String.class));
	}

	@Test
	public void testSetsSelectedListOnUpdate() {
		final String list = "Personal";
		Set<TaskList> lists = CollectionsUtil.asSet(new TaskList("Inbox"),
				new TaskList("Miscellaneous"), new TaskList(list));
		Task task = mock(Task.class);
		when(model.getTaskLists()).thenReturn(lists);
		when(model.getTaskForEdit()).thenReturn(task);
		when(task.getTaskList()).thenReturn(list);
		when(task.getDueDate()).thenReturn(new Date());

		presenter.update(model);

		verify(view, times(1)).setSelectedTaskList(list);
	}

	@Test
	public void testGetsTaskForEditFromModelOnUpdate() {
		Task task = new Task("task1", "somedescription");
		when(model.getTaskForEdit()).thenReturn(task);

		presenter.update(model);

		verify(model).getTaskForEdit();
	}

	@Test
	public void testSetsTaskNameToViewOnUpdate() {
		Task task = new Task("task1", "somedescription");
		when(model.getTaskForEdit()).thenReturn(task);

		presenter.update(model);

		verify(view).setTaskName(task.getName());
	}

	@Test
	public void testSetsTaskDescriptionToViewOnUpdate() {
		Task task = new Task("task1", "somedescription");
		when(model.getTaskForEdit()).thenReturn(task);

		presenter.update(model);

		verify(view).setTaskDescription(task.getDescription());
	}

	@Test
	public void testSetsTaskCompletedToViewOnUpdate() {
		Task task = new Task("task1", "somedescription");
		when(model.getTaskForEdit()).thenReturn(task);

		presenter.update(model);

		verify(view).setTaskCompleted(task.isCompleted());
	}

	@Test
	public void testSetsTaskTagsToViewOnUpdate() {
		Task task = new Task("task1", "somedescription");
		task.addTags(Arrays.asList("tag1", "tag2"));
		when(model.getTaskForEdit()).thenReturn(task);

		presenter.update(model);

		verify(view).setTags("tag1 tag2");
	}

	@Test
	public void testSetsOriginalDueDateToViewOnUpdate() {
		Date due = new Date(1329506669694L);
		Task task = new Task("task1", "somedescription");
		task.setDueDate(due);
		when(model.getTaskForEdit()).thenReturn(task);

		presenter.update(model);

		verify(view, times(1)).setDueDate(Constants.ORIGINAL);
	}

	@Test
	public void testConvertTaskDependenciesToViewListCorrectlyConverts() {
		Set<Task> dependencies = getDependenciesSet(new Task("task1",
				"task1 desc"), new Task("task2", "task2 desc"));

		List<String> convertedDeps = EditTaskPresenter
				.convertTaskDependenciesToViewList(dependencies);

		assertTrue(
				"Didn't find the original dependency task name in the converted list!",
				convertedDeps.contains("task1"));
		assertTrue(
				"Didn't find the original dependency task name in the converted list!",
				convertedDeps.contains("task2"));
	}

	@Test
	public void testSaveTaskRetrievesFieldsFromViewAndSendsToModel()
			throws ParseException {
		String newName = "newName";
		String newDescription = "newDescription";
		String taskList = "Personal";
		String tagsLine = "tag1 tag2";
		List<String> newDependencies = Arrays.asList("dep1", "dep2");
		when(view.getTaskName()).thenReturn(newName);
		when(view.getTaskList()).thenReturn(taskList);
		when(view.getDescription()).thenReturn(newDescription);
		when(view.getCompleted()).thenReturn(true);
		presenter.selectedDependencies = newDependencies;
		when(view.getTags()).thenReturn(tagsLine);
		when(view.getDueDate()).thenReturn("07-10-1982");

		presenter.saveTask();

		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, Calendar.JULY);
		cal.set(Calendar.DAY_OF_MONTH, 10);
		cal.set(Calendar.YEAR, 1982);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		verify(model, times(1)).saveTask(
				eq(newName),
				eq(newDescription),
				eq(new SimpleDateFormat(Constants.DUE_DATE_PATTERN)
						.parse("07-10-1982")), eq(true), eq(taskList),
				eq(newDependencies),
				eq(new HashSet<String>(Arrays.asList("tag1", "tag2"))));
	}

	@Test
	public void testSaveTaskClosesTheViewWhenDone() {
		String newName = "newName";
		String newDescription = "newDescription";
		String taskList = "taskList";
		List<String> newDependencies = Arrays.asList("dep1", "dep2");
		when(view.getTaskName()).thenReturn(newName);
		when(view.getDescription()).thenReturn(newDescription);
		when(view.getTaskList()).thenReturn(taskList);
		when(view.getCompleted()).thenReturn(true);
		presenter.selectedDependencies = newDependencies;
		when(view.getTags()).thenReturn("");
		when(view.getDueDate()).thenReturn(Constants.NEVER);

		presenter.saveTask();

		verify(view, times(1)).close();
	}

	@Test
	public void testSaveTaskRequiresNameToBeNotNull() {
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

		presenter.saveTask();

		verify(contextFactory, times(1)).displayAlert(
				AddTaskPresenter.PLEASE_ENTER_A_TASK_NAME);
		verify(model, never()).saveTask(anyString(), anyString(),
				any(Date.class), anyBoolean(), anyString(),
				anyListOf(String.class), anySetOf(String.class));
	}

	@Test
	public void testSaveTaskRequiresNameToBeNotEmptyString() {
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

		presenter.saveTask();

		verify(contextFactory, times(1)).displayAlert(
				BaseTaskPresenter.PLEASE_ENTER_A_TASK_NAME);
		verify(model, never()).saveTask(anyString(), anyString(),
				any(Date.class), anyBoolean(), anyString(),
				anyListOf(String.class), anySetOf(String.class));
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

		presenter.saveTask();

		verify(contextFactory, times(1)).displayAlert(
				BaseTaskPresenter.PLEASE_ENTER_DATE_WITH_CORRECT_FORMAT);
	}

	@Test
	public void testSetTaskForEditSetsTaskNameOnModel() {
		String taskNameToEdit = "taskToEdit";

		presenter.setTaskForEdit(taskNameToEdit);

		verify(model, times(1)).setTaskForEdit(taskNameToEdit);
	}

	@Test
	public void testCancelInvokesCloseOnView() {
		presenter.cancel();

		verify(view, times(1)).close();
	}

	@Test
	public void testCorrectlyConfiguredSaveButton() {
		List<ActionConfiguration> buttonConfigurations = presenter
				.getButtonOptions();
		ActionConfiguration buttonConfiguration = buttonConfigurations.get(0);
		assertEquals("Incorrect display text for the button!", "Save",
				buttonConfiguration.getDisplayText());
		assertSame(
				"Did not find the expected click action for the button configuration!",
				presenter.saveClickAction, buttonConfiguration.getClickAction());
	}

	@Test
	public void testcorrectlyConfiguredCancelButton() {

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
	protected Class<EditTaskModel> getModelClass() {
		return EditTaskModel.class;
	}

	@Override
	protected Class<EditTaskView> getViewClass() {
		return EditTaskView.class;
	}

	@Override
	protected EditTaskPresenter getPresenter(EditTaskModel model,
			EditTaskView view) {
		return EditTaskPresenter.construct(model, view, new Properties());
	}

	public static Set<Task> getDependenciesSet(Task... tasks) {
		return new TreeSet<Task>(Arrays.asList(tasks));
	}
}
