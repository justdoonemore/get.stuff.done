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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.mockito.Mockito;

import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.model.dao.DaoFactory;
import com.jdom.get.stuff.done.model.impl.EditTaskModelImpl;
import com.jdom.get.stuff.done.presenter.EditTaskPresenterTest;
import com.jdom.util.patterns.observer.Observer;

public class EditTaskModelImplTest extends
		BaseApplicationModelImplTest<EditTaskModelImpl> {

	@Test
	public void testGetTaskForEditReturnsTheTaskSetUponTheModel() {
		Task task = new Task("task1", "desc");

		model.setTaskForEdit(task);

		assertSame("Did not find the task set on the model when retrieving!",
				task, model.getTaskForEdit());
	}

	@Test
	public void testGetAvailableDependenciesDoesReturnDependenciesAlreadyOnTheTask() {
		Task dep1 = new Task("task1", "task1 desc");
		Task dep2 = new Task("task2", "task2 desc");
		List<Task> dependencies = new ArrayList<Task>(Arrays.asList(dep1, dep2));
		Task taskWithDeps = new Task("taskWithDeps", "has deps", dependencies);
		Task notDep1 = new Task("notdep1", "not dep");
		Task notDep2 = new Task("notdep2", "not dep");
		Set<Task> allTasks = EditTaskPresenterTest.getDependenciesSet(dep1,
				dep2, taskWithDeps, notDep1, notDep2);

		ApplicationDao dao = Mockito.mock(ApplicationDao.class);
		DaoFactory daoFactory = Mockito.mock(DaoFactory.class);
		Mockito.when(contextFactory.getDaoFactory()).thenReturn(daoFactory);
		Mockito.when(daoFactory.getApplicationDao()).thenReturn(dao);
		Mockito.when(dao.getTasks()).thenReturn(allTasks);

		model.taskForEdit = taskWithDeps;

		Set<Task> availableDependencies = model.getAvailableDependencies();
		assertTrue(
				"The available dependencies list should have contained a task that is already a dependency!",
				availableDependencies.contains(dep1));
		assertTrue(
				"The available dependencies list should have contained a task that is already a dependency!",
				availableDependencies.contains(dep2));
	}

	@Test
	public void testGetAvailableDependenciesIncludeTasksNotDependenciesOnTheTask() {
		Task dep1 = new Task("task1", "task1 desc");
		Task dep2 = new Task("task2", "task2 desc");
		List<Task> dependencies = new ArrayList<Task>(Arrays.asList(dep1, dep2));
		Task taskWithDeps = new Task("taskWithDeps", "has deps", dependencies);
		Task notDep1 = new Task("notdep1", "not dep");
		Task notDep2 = new Task("notdep2", "not dep");
		Set<Task> allTasks = EditTaskPresenterTest.getDependenciesSet(dep1,
				dep2, taskWithDeps, notDep1, notDep2);

		ApplicationDao dao = Mockito.mock(ApplicationDao.class);
		DaoFactory daoFactory = Mockito.mock(DaoFactory.class);
		Mockito.when(contextFactory.getDaoFactory()).thenReturn(daoFactory);
		Mockito.when(daoFactory.getApplicationDao()).thenReturn(dao);
		Mockito.when(dao.getTasks()).thenReturn(allTasks);

		model.taskForEdit = taskWithDeps;

		Set<Task> availableDependencies = model.getAvailableDependencies();
		assertTrue(
				"The available dependencies list should have contained a task that is not already a dependency!",
				availableDependencies.contains(notDep1));
		assertTrue(
				"The available dependencies list should have contained a task that is not already a dependency!",
				availableDependencies.contains(notDep2));
	}

	@Test
	public void testGetAvailableDependenciesDoesNotReturnTheTasksForEdit() {
		Task dep1 = new Task("task1", "task1 desc");
		List<Task> dependencies = new ArrayList<Task>(Arrays.asList(dep1));
		Task taskWithDeps = new Task("taskWithDeps", "has deps", dependencies);
		Task notDep1 = new Task("notdep1", "not dep");
		Set<Task> allTasks = EditTaskPresenterTest.getDependenciesSet(dep1,
				taskWithDeps, notDep1);

		ApplicationDao dao = mock(ApplicationDao.class);
		DaoFactory daoFactory = Mockito.mock(DaoFactory.class);
		when(contextFactory.getDaoFactory()).thenReturn(daoFactory);
		when(daoFactory.getApplicationDao()).thenReturn(dao);
		when(dao.getTasks()).thenReturn(allTasks);

		model.taskForEdit = taskWithDeps;

		Set<Task> availableDependencies = model.getAvailableDependencies();
		assertFalse(
				"The available dependencies list should not have contained the task being edited!",
				availableDependencies.contains(taskWithDeps));
	}

	@Test
	public void testSaveTaskWillCreateTaskWithSpecifiedAttributes() {
		Task dep1 = new Task("dep1", "dep1 desc");
		Task dep2 = new Task("dep2", "dep2 desc");
		Task notDep1 = new Task("notdep1", "not dep");
		Set<Task> allTasks = EditTaskPresenterTest.getDependenciesSet(dep1,
				dep2, notDep1);
		Set<String> tags = new HashSet<String>(Arrays.asList("tag1", "tag2"));

		ApplicationDao dao = mock(ApplicationDao.class);
		DaoFactory daoFactory = Mockito.mock(DaoFactory.class);
		Mockito.when(contextFactory.getDaoFactory()).thenReturn(daoFactory);
		when(daoFactory.getApplicationDao()).thenReturn(dao);
		when(dao.getTasks()).thenReturn(allTasks);

		Date date = new Date(234512351235L);

		model.taskForEdit = new Task("task1", "description");
		Task savedTask = model.saveTask("newName", "newDescription", date,
				true, "Personal", Arrays.asList("dep1", "dep2"), tags);

		assertEquals("Incorrect task name found!", "newName",
				savedTask.getName());
		assertEquals("Incorrect task description found!", "newDescription",
				savedTask.getDescription());
		assertEquals("Incorrect task list found!", "Personal",
				savedTask.getTaskList());
		assertEquals("Incorrect due date found!", date, savedTask.getDueDate());
		assertEquals("Incorrect task completed status found!", true,
				savedTask.isCompleted());
		assertTrue("Didn't find the specified dependency on the saved task!",
				savedTask.getDependencies().contains(dep1));
		assertTrue("Didn't find the specified dependency on the saved task!",
				savedTask.getDependencies().contains(dep2));
		assertTrue("Didn't find the specified tag on the saved task!",
				savedTask.getTags().contains("tag1"));
		assertTrue("Didn't find the specified tag on the saved task!",
				savedTask.getTags().contains("tag2"));
		assertFalse(
				"Shouldn't have found the specified dependency on the saved task!",
				savedTask.getDependencies().contains(notDep1));
	}

	@Test
	public void testSaveTaskWillCallDaoUpdateTaskMethodWithAppropriateArguments() {
		ApplicationDao dao = Mockito.mock(ApplicationDao.class);
		DaoFactory daoFactory = Mockito.mock(DaoFactory.class);
		Mockito.when(contextFactory.getDaoFactory()).thenReturn(daoFactory);
		when(daoFactory.getApplicationDao()).thenReturn(dao);

		model.taskForEdit = new Task("task1", "description");

		Task updated = model.saveTask("newName", "newDescription", new Date(),
				true, "Personal", Collections.<String> emptyList(),
				Collections.<String> emptySet());

		verify(dao).updateTask(model.taskForEdit, updated);
	}

	@Test
	public void testSetTaskForEditLoadsTaskFromDaoAndCallsOverloadedSetTaskForEdit() {
		ApplicationDao dao = Mockito.mock(ApplicationDao.class);
		DaoFactory daoFactory = Mockito.mock(DaoFactory.class);
		Mockito.when(contextFactory.getDaoFactory()).thenReturn(daoFactory);
		when(daoFactory.getApplicationDao()).thenReturn(dao);
		Task task = Mockito.mock(Task.class);
		when(dao.getTaskByName(Mockito.anyString())).thenReturn(task);

		model.setTaskForEdit("taskToEdit");

		assertSame("Did not find the loaded task set on the model!", task,
				model.taskForEdit);
	}

	@Test
	public void testSetTaskForEditNotifiesObservers() {
		Task task = Mockito.mock(Task.class);
		Observer observer = Mockito.mock(Observer.class);
		model.addObserver(observer);

		model.setTaskForEdit(task);

		verify(observer).update(model);
	}

	@Test(expected = IllegalStateException.class)
	public void testThrowsExceptionIfGetAvailableDependenciesIsCalledBeforeTheTaskIsSetOnTheModel() {
		model.getAvailableDependencies();
	}

	@Override
	protected EditTaskModelImpl getModel() {
		return new EditTaskModelImpl();
	}
}
