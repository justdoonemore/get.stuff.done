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

import static org.junit.Assert.assertSame;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.model.dao.MockApplicationDao;
import com.jdom.get.stuff.done.model.impl.AddTaskModelImpl;
import com.jdom.get.stuff.done.presenter.EditTaskPresenterTest;
import com.jdom.get.stuff.done.presenter.SyncOption;
import com.jdom.util.collections.CollectionsUtil;

public class AddTaskModelImplTest extends
		BaseApplicationModelImplTest<AddTaskModelImpl> {

	@Test
	public void testAddTaskCreatesTaskWithAppropriateArgumentsAndSendsToDaoForStorage() {
		ApplicationDao dao = new MockApplicationDao();
		Mockito.when(daoFactory.getApplicationDao()).thenReturn(dao);
		Calendar cal = Calendar.getInstance();
		Date dueToday = cal.getTime();

		cal.roll(Calendar.DATE, -1);
		Date dueYesterday = cal.getTime();

		cal.roll(Calendar.DATE, 2);
		Date dueTomorrow = cal.getTime();

		model.addTask("task1", "task1 description", "Miscellaneous", null,
				new HashSet<String>(Arrays.asList("tag1", "tag2")), dueToday);
		model.addTask("task2", "task2 description", "Inbox",
				Collections.<String> emptyList(),
				Collections.<String> emptySet(), dueYesterday);
		model.addTask("task3", "task3 description", "Personal",
				Arrays.asList("task1", "task2"),
				new HashSet<String>(Arrays.asList("tag3")), dueTomorrow);

		Set<Task> tasks = dao.getTasks();
		Iterator<Task> iter = tasks.iterator();

		assertProperTask(iter.next(), "task1", "task1 description",
				"Miscellaneous", Collections.<Task> emptyList(),
				Arrays.asList("tag1", "tag2"), dueToday);
		assertProperTask(iter.next(), "task2", "task2 description", "Inbox",
				Collections.<Task> emptyList(),
				Collections.<String> emptyList(), dueYesterday);
		assertProperTask(iter.next(), "task3", "task3 description", "Personal",
				Arrays.asList(new Task("task1"), new Task("task2")),
				Arrays.asList("tag3"), dueTomorrow);
	}

	@Test
	public void testAddTaskInvokesSyncStrategy() {
		model.addTask("taskName", "description", "Inbox",
				Collections.<String> emptyList(),
				Collections.<String> emptySet(), new Date());

		Mockito.verify(syncStrategy).synchronizeIfEnabled(
				SyncOption.LOCAL_CHANGE);
	}

	@Test
	public void testGetAvailableDependenciesReturnsAllTasks() {
		Set<Task> tasks = EditTaskPresenterTest.getDependenciesSet(new Task(
				"task1", "desc"), new Task("task2", "desc"));
		Mockito.when(dao.getTasks()).thenReturn(tasks);

		assertSame("Did not find all tasks returned!", tasks,
				model.getAvailableDependencies());
	}

	@Test
	public void testGetTaskListsReturnsThemFromDao() {
		Set<TaskList> taskLists = CollectionsUtil.asSet(new TaskList("Inbox"),
				new TaskList("Personal"));
		Mockito.when(dao.getTaskLists()).thenReturn(taskLists);

		assertSame("Did not find all tasks returned!", taskLists,
				model.getTaskLists());
	}

	private void assertProperTask(Task task, String expectedTaskName,
			String expectedDescription, String expectedTaskList,
			List<Task> dependencies, Collection<String> tags,
			Date expectedDueDate) {
		Assert.assertEquals("The task name was not as expected!",
				expectedTaskName, task.getName());
		Assert.assertEquals("The description was not as expected!",
				expectedDescription, task.getDescription());
		Assert.assertEquals("The task list was not the correct value!",
				expectedTaskList, task.getTaskList());

		Set<Task> actualDependencies = task.getDependencies();
		Assert.assertEquals("Incorrect number of dependencies found!",
				dependencies.size(), actualDependencies.size());
		Assert.assertTrue("Didn't find all dependencies the same!",
				actualDependencies.containsAll(dependencies));

		Set<String> actualTags = task.getTags();
		Assert.assertEquals("Incorrect number of tags found!", tags.size(),
				actualTags.size());
		Assert.assertTrue("Didn't find all tags the same!",
				actualTags.containsAll(tags));

		Assert.assertEquals("Did not find the correct due date on the task!",
				expectedDueDate, task.getDueDate());
	}

	@Override
	protected AddTaskModelImpl getModel() {
		return new AddTaskModelImpl();
	}
}
