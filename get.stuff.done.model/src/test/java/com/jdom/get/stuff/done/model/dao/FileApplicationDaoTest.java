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
package com.jdom.get.stuff.done.model.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.dao.FileApplicationDao;
import com.jdom.get.stuff.done.presenter.EditTaskPresenterTest;
import com.jdom.junit.utils.AbstractFixture;
import com.jdom.junit.utils.AssertUtil;
import com.jdom.util.collections.CollectionsUtil;

public class FileApplicationDaoTest {

	private final File testDir = AbstractFixture.setupTestClassDir(getClass());

	private final FileApplicationDao fileTaskDao = new FileApplicationDao(
			testDir);

	@Test
	public void testStoreAndGetTasksRestoresTasks() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2");

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));
		Set<Task> restored = fileTaskDao.getTasks();

		assertTrue("Did not find the first task restored!",
				restored.contains(task1));
		assertTrue("Did not find the second task restored!",
				restored.contains(task2));
	}

	@Test
	public void testStoreAndGetTasksRestoresDueDate() {
		Date explicitDueDate = new Date();
		Task task1 = new Task("task1");
		task1.setDueDate(explicitDueDate);

		// Use default due date
		Task task2 = new Task("task2");
		Date implicitDueDate = task2.getDueDate();

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));
		Set<Task> restored = fileTaskDao.getTasks();

		Iterator<Task> iterator = restored.iterator();
		Task task1Restored = iterator.next();
		assertEquals("Did not find the explicit due date restored!",
				explicitDueDate, task1Restored.getDueDate());
		Task task2Restored = iterator.next();
		assertEquals("Did not find the implicit due date restored!",
				implicitDueDate, task2Restored.getDueDate());
	}

	@Test
	public void testStoreAndGetTasksCannotHaveDuplicateTasks() {
		Task task1 = new Task("task1");
		Task task1Also = new Task("task1");

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task1Also));
		Set<Task> restored = fileTaskDao.getTasks();

		assertEquals("Expected to only find one task!", 1, restored.size());
		assertTrue("Did not find the first task restored!",
				restored.contains(task1));
	}

	@Test
	public void testStoreAndGetTasksRestoresDescriptions() {
		Task task1 = new Task("task1", "This is task1.");
		Task task2 = new Task("task2", "This is task2.");

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));
		Set<Task> restored = fileTaskDao.getTasks();
		Iterator<Task> iter = restored.iterator();

		assertEquals("Did not find the first task description!",
				"This is task1.", iter.next().getDescription());
		assertEquals("Did not find the second task description!",
				"This is task2.", iter.next().getDescription());
	}

	@Test
	public void testStoreAndGetTasksRestoresDependencies() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2", Arrays.asList(task1));

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));
		Set<Task> restored = fileTaskDao.getTasks();
		Iterator<Task> iter = restored.iterator();
		iter.next();

		assertTrue("Did not find the task dependency as restored!", iter.next()
				.getDependencies().contains(task1));
	}

	@Test
	public void testStoreAndGetTasksRestoresRemoteIdentifier() {
		final String remoteId = "remoteId";
		Task noRemoteIdentifier = new Task("task1");
		Task remoteIdentifier = new Task("task2");
		remoteIdentifier.setRemoteIdentifier(remoteId);

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(
				noRemoteIdentifier, remoteIdentifier));
		Set<Task> restored = fileTaskDao.getTasks();
		Iterator<Task> iter = restored.iterator();

		Task restoredTask1 = iter.next();
		Task restoredTask2 = iter.next();
		assertNull("Did not find the task remote id as restored!",
				restoredTask1.getRemoteIdentifier());
		assertEquals("Did not find the task remote id as restored!", remoteId,
				restoredTask2.getRemoteIdentifier());
	}

	@Test
	public void testStoreAndGetTasksRestoresLastSyncTime() {
		final long lastSyncTime = 235L;
		Task defaultValue = new Task("task1");
		Task specificValue = new Task("task2");
		specificValue.setLastSyncTime(lastSyncTime);

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(
				defaultValue, specificValue));
		Set<Task> restored = fileTaskDao.getTasks();
		Iterator<Task> iter = restored.iterator();

		Task restoredTask1 = iter.next();
		Task restoredTask2 = iter.next();
		assertEquals("Did not find the task last sync time as restored!",
				Task.DEFAULT_LAST_SYNC_TIME, restoredTask1.getLastSyncTime());
		assertEquals("Did not find the task last sync time as restored!",
				lastSyncTime, restoredTask2.getLastSyncTime());
	}

	@Test
	public void testStoreAndGetTasksRestoresLastUpdatedTime() {
		final long lastUpdatedTime = 235L;
		Task defaultValue = new Task("task1");
		Task specificValue = new Task("task2");
		specificValue.setLastUpdatedTime(lastUpdatedTime);

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(
				defaultValue, specificValue));
		Set<Task> restored = fileTaskDao.getTasks();
		Iterator<Task> iter = restored.iterator();

		Task restoredTask1 = iter.next();
		Task restoredTask2 = iter.next();
		assertEquals("Did not find the task last updated time as restored!",
				Task.DEFAULT_LAST_UPDATE_TIME,
				restoredTask1.getLastUpdatedTime());
		assertEquals("Did not find the task last updated time as restored!",
				lastUpdatedTime, restoredTask2.getLastUpdatedTime());
	}

	@Test
	public void testStoreAndGetTasksRestoresDeletedFlag() {
		final boolean deleted = true;
		Task defaultValue = new Task("task1");
		Task specificValue = new Task("task2");
		specificValue.setDeleted(deleted);

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(
				defaultValue, specificValue));
		Set<Task> restored = fileTaskDao.getTasks();
		Iterator<Task> iter = restored.iterator();

		Task restoredTask1 = iter.next();
		Task restoredTask2 = iter.next();
		assertFalse("Did not find the task deleted flag as restored!",
				restoredTask1.isDeleted());
		assertTrue("Did not find the task deleted flag as restored!",
				restoredTask2.isDeleted());
	}

	@Test
	public void testStoreAndGetTasksRestoresTaskLists() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2", "my description", "Inbox",
				Arrays.asList(task1));

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));
		Set<Task> restored = fileTaskDao.getTasks();
		Iterator<Task> iter = restored.iterator();

		assertEquals("Did not find the task list as restored!",
				Constants.DEFAULT_LIST, iter.next().getTaskList());
		assertEquals("Did not find the task list as restored!", "Inbox", iter
				.next().getTaskList());
	}

	@Test
	public void testStoreAndGetTaskListsCannotHaveDuplicateTaskLists() {
		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(new TaskList("Inbox"),
				new TaskList("Inbox")));
		Set<TaskList> restored = fileTaskDao.getTaskLists();

		assertEquals("Incorrect number of task lists returned!", 1,
				restored.size());
		assertEquals("Did not find the task list as restored!", "Inbox",
				restored.iterator().next().getName());
	}

	@Test
	public void testStoreAndGetTasksRestoresCompletedStatus() {
		Task task1 = new Task("task1");
		task1.setCompleted(true);
		Task task2 = new Task("task2", Arrays.asList(task1));
		task2.setCompleted(false);

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));
		Set<Task> restored = fileTaskDao.getTasks();
		Iterator<Task> iter = restored.iterator();

		assertTrue("Did not find the task completed status as restored!", iter
				.next().isCompleted());
		assertFalse("Did not find the task completed status as restored!", iter
				.next().isCompleted());
	}

	@Test
	public void testStoreAndGetTasksRestoresTags() {
		Task task1 = new Task("task1");
		task1.addTag("tag1");
		task1.addTag("tag2");
		Task task2 = new Task("task2", Arrays.asList(task1));
		task2.addTag("tag3");

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));
		Set<Task> restored = fileTaskDao.getTasks();
		Iterator<Task> iter = restored.iterator();

		Task restoredTask1 = iter.next();
		assertTrue("Did not find the task tags as restored!", restoredTask1
				.getTags().contains("tag1"));
		assertTrue("Did not find the task tags as restored!", restoredTask1
				.getTags().contains("tag2"));

		Task restoredTask2 = iter.next();
		assertTrue("Did not find the task tags as restored!", restoredTask2
				.getTags().contains("tag3"));
	}

	@Test
	public void testStoreAndGetTaskListsRestoresAllTaskLists() {
		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(new TaskList("Inbox"),
				new TaskList(Constants.DEFAULT_LIST)));
		Set<TaskList> restored = fileTaskDao.getTaskLists();

		assertTrue("Didn't find the restored task list!",
				restored.contains(new TaskList("Inbox")));
		assertTrue("Didn't find the restored task list!",
				restored.contains(new TaskList(Constants.DEFAULT_LIST)));
	}

	@Test
	public void testAddTaskListAddsNewList() {
		// First create some already existing task lists
		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(new TaskList("Inbox"),
				new TaskList(Constants.DEFAULT_LIST)));

		final TaskList taskList = new TaskList("blah");
		fileTaskDao.addTaskList(taskList);
		Set<TaskList> restored = fileTaskDao.getTaskLists();

		assertTrue("Didn't find the restored task list!",
				restored.contains(taskList));
	}

	@Test
	public void testStoreAndGetTaskListsRestoresRemoteIdentifier() {
		final String remoteId = "remoteId";
		TaskList defaultValue = new TaskList("task1");
		TaskList specificValue = new TaskList("task2");
		specificValue.setRemoteIdentifier(remoteId);

		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(defaultValue,
				specificValue));
		Set<TaskList> restored = fileTaskDao.getTaskLists();
		Iterator<TaskList> iter = restored.iterator();

		TaskList restoredTaskList1 = iter.next();
		TaskList restoredTaskList2 = iter.next();
		assertNull("Did not find the task remote id as restored!",
				restoredTaskList1.getRemoteIdentifier());
		assertEquals("Did not find the task remote id as restored!", remoteId,
				restoredTaskList2.getRemoteIdentifier());
	}

	@Test
	public void testStoreAndGetTaskListsRestoresLastSyncTime() {
		final long lastSyncTime = 235L;
		TaskList defaultValue = new TaskList("task1");
		TaskList specificValue = new TaskList("task2");
		specificValue.setLastSyncTime(lastSyncTime);

		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(defaultValue,
				specificValue));
		Set<TaskList> restored = fileTaskDao.getTaskLists();
		Iterator<TaskList> iter = restored.iterator();

		TaskList restoredTaskList1 = iter.next();
		TaskList restoredTaskList2 = iter.next();
		assertEquals("Did not find the task last sync time as restored!",
				TaskList.DEFAULT_LAST_SYNC_TIME,
				restoredTaskList1.getLastSyncTime());
		assertEquals("Did not find the task last sync time as restored!",
				lastSyncTime, restoredTaskList2.getLastSyncTime());
	}

	@Test
	public void testStoreAndGetTaskListsRestoresLastUpdatedTime() {
		final long lastUpdatedTime = 235L;
		TaskList defaultValue = new TaskList("task1");
		TaskList specificValue = new TaskList("task2");
		specificValue.setLastUpdatedTime(lastUpdatedTime);

		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(defaultValue,
				specificValue));
		Set<TaskList> restored = fileTaskDao.getTaskLists();
		Iterator<TaskList> iter = restored.iterator();

		TaskList restoredTaskList1 = iter.next();
		TaskList restoredTaskList2 = iter.next();
		assertEquals("Did not find the task last updated time as restored!",
				TaskList.DEFAULT_LAST_UPDATE_TIME,
				restoredTaskList1.getLastUpdatedTime());
		assertEquals("Did not find the task last updated time as restored!",
				lastUpdatedTime, restoredTaskList2.getLastUpdatedTime());
	}

	@Test
	public void testStoreAndGetTaskListsRestoresDeletedFlag() {
		final boolean deleted = true;
		TaskList defaultValue = new TaskList("task1");
		TaskList specificValue = new TaskList("task2");
		specificValue.setDeleted(deleted);

		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(defaultValue,
				specificValue));
		Set<TaskList> restored = fileTaskDao.getTaskLists();

		Iterator<TaskList> iter = restored.iterator();

		TaskList restoredTaskList1 = iter.next();
		TaskList restoredTaskList2 = iter.next();
		assertFalse("Did not find the task deleted flag as restored!",
				restoredTaskList1.isDeleted());
		assertTrue("Did not find the task deleted flag as restored!",
				restoredTaskList2.isDeleted());
	}

	@Test
	public void testDeleteTaskListDeletesList() {
		TaskList listToDelete = new TaskList("Inbox");
		fileTaskDao.storeTasks(Collections.<Task> emptySet());
		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(listToDelete,
				new TaskList(Constants.DEFAULT_LIST)));

		fileTaskDao.deleteTaskList(listToDelete);

		Set<TaskList> restored = fileTaskDao.getTaskLists();

		assertFalse("Shouldn't have found the deleted list!",
				restored.contains(listToDelete));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDeleteTaskThrowsExceptionWhenDefaultListIsPassedAsArgument() {
		TaskList listToDelete = new TaskList(Constants.DEFAULT_LIST);
		fileTaskDao.storeTasks(Collections.<Task> emptySet());
		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(new TaskList(
				Constants.DEFAULT_LIST)));

		fileTaskDao.deleteTaskList(listToDelete);
	}

	@Test
	public void testDeleteTaskListWillDeleteTasksOnThatList() {
		TaskList listToDelete = new TaskList("Inbox");
		TaskList personal = new TaskList("Personal");
		TaskList defaultList = new TaskList(Constants.DEFAULT_LIST);
		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(listToDelete,
				defaultList, personal));

		Task task1 = new Task("task1", "desc1", listToDelete.getName());
		Task task2 = new Task("task2", "desc2", personal.getName());
		Task task3 = new Task("task3", "desc3", defaultList.getName());
		fileTaskDao.storeTasks(CollectionsUtil.asSet(task1, task2, task3));

		fileTaskDao.deleteTaskList(listToDelete);

		Set<Task> tasks = fileTaskDao.getTasks();
		assertEquals(
				"The task(s) under the deleted list should have been deleted!",
				2, tasks.size());
		Iterator<Task> iter = tasks.iterator();

		Task restored1 = iter.next();
		Task restored2 = iter.next();

		assertEquals(
				"The task that was not under the deleted list should not have moved lists!",
				personal.getName(), restored1.getTaskList());
		assertEquals(
				"The task that was not under the deleted list should not have moved lists!",
				defaultList.getName(), restored2.getTaskList());
	}

	@Test
	public void testAddTaskListSetsUpdatedTime() {
		// First create some already existing task lists
		fileTaskDao.storeTaskLists(CollectionsUtil.asSet(new TaskList("Inbox"),
				new TaskList(Constants.DEFAULT_LIST)));

		final TaskList taskList = new TaskList("blah");
		fileTaskDao.addTaskList(taskList);
		Set<TaskList> restored = fileTaskDao.getTaskLists();

		boolean found = false;
		for (TaskList addedList : restored) {
			if (addedList.getName().equals("blah")) {
				assertFalse("The last updated time should have been set!",
						TaskList.DEFAULT_LAST_UPDATE_TIME == addedList
								.getLastUpdatedTime());
				found = true;
				break;
			}
		}

		assertTrue("Should have found the TaskList restored!", found);
	}

	@Test
	public void testAddTaskCreatesATaskThatIsRetrievable() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2", Arrays.asList(task1));

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));

		Task taskWithDeps = new Task("task3", "description", Arrays.asList(
				task1, task2));

		fileTaskDao.addTask(taskWithDeps);

		Set<Task> tasks = fileTaskDao.getTasks();
		assertEquals("Incorrect number of tasks returned!", 3, tasks.size());

		Iterator<Task> iter = tasks.iterator();
		iter.next();
		iter.next();

		Task task = iter.next();
		assertEquals("Incorrect task name found!", "task3", task.getName());
		assertEquals("Incorrect task description found!", "description",
				task.getDescription());
		assertTrue("Did not find the expected dependency!", task
				.getDependencies().contains(task1));
		assertTrue("Did not find the expected dependency!", task
				.getDependencies().contains(task2));
	}

	@Test
	public void testAddTaskCreatesATaskWithANonDefaultUpdateTime() {
		Task task1 = new Task("task1");

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1));

		Task newTask = new Task("task3", "description");

		fileTaskDao.addTask(newTask);

		Set<Task> tasks = fileTaskDao.getTasks();
		assertEquals("Incorrect number of tasks returned!", 2, tasks.size());

		Iterator<Task> iter = tasks.iterator();
		iter.next();

		Task task = iter.next();
		assertFalse("The updated time should have been set on adding a task!",
				Task.DEFAULT_LAST_UPDATE_TIME == task.getLastUpdatedTime());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFindTaskInCollectionByNameThrowsExceptionWhenInvalidNameSpecified() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2");
		Set<Task> tasks = EditTaskPresenterTest
				.getDependenciesSet(task1, task2);

		FileApplicationDao.findTaskInCollectionByName(tasks, "task3");
	}

	@Test
	public void testFindTaskInCollectionByNameReturnsCorrectTaskForName() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2");
		Set<Task> tasks = EditTaskPresenterTest
				.getDependenciesSet(task1, task2);

		assertSame("Did not return the correct task by name!", task2,
				FileApplicationDao.findTaskInCollectionByName(tasks, "task2"));
	}

	@Test
	public void testDeleteTaskWillDeleteTheSpecifiedTaskAndOnlyTheSpecifiedTask() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2");

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));
		fileTaskDao.deleteTask(task1);

		Set<Task> tasks = fileTaskDao.getTasks();
		assertEquals("Incorrect number of tasks returned!", 1, tasks.size());

		Task task = tasks.iterator().next();
		assertEquals("Incorrect task name found!", task.getName(), "task2");
	}

	@Test
	public void testDeleteTaskWillRemoveTheSpecifiedTaskFromOtherTasksAsADependency() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2", "task2 description",
				Arrays.asList(task1));

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));
		fileTaskDao.deleteTask(task1);

		Set<Task> tasks = fileTaskDao.getTasks();
		Task task = tasks.iterator().next();
		assertTrue("Should not have found the deleted task as a dependency!",
				task.getDependencies().isEmpty());
	}

	@Test
	public void testUpdateTaskWillDeleteOldTaskAndAddNewOne() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2", "task2 description");
		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));

		Task updated = new Task("newName", "newDescription",
				Arrays.asList(task2));
		fileTaskDao.updateTask(task1, updated);

		Set<Task> newTasks = fileTaskDao.getTasks();
		assertEquals("Incorrect number of tasks returned!", 2, newTasks.size());
		Task taskToVerify = null;
		for (Task task : newTasks) {
			if (!task.getName().equals("task2")) {
				taskToVerify = task;
				break;
			}
		}

		assertNotNull("Did not find the updated task!", taskToVerify);
		assertEquals("Did not find the updated name!", "newName",
				taskToVerify.getName());
		assertEquals("Did not find the updated description!", "newDescription",
				taskToVerify.getDescription());
		assertTrue("Did not find the updated dependency!", taskToVerify
				.getDependencies().contains(task2));
	}

	@Test
	public void testUpdateTaskWillRewireTasksDependentOnOldToNewTask() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2", "task2 description",
				Arrays.asList(task1));
		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2));

		Task updated = new Task("newName", "newDescription");
		fileTaskDao.updateTask(task1, updated);

		Set<Task> newTasks = fileTaskDao.getTasks();
		assertEquals("Incorrect number of tasks returned!", 2, newTasks.size());
		Task taskToVerify = null;
		for (Task task : newTasks) {
			if (task.getName().equals("task2")) {
				taskToVerify = task;
				break;
			}
		}

		assertNotNull(
				"Did not find the task that should have had its dependencies updated!",
				taskToVerify);
		assertTrue("Did not find the updated dependency!", taskToVerify
				.getDependencies().contains(updated));
	}

	@Test
	public void testUpdateTaskWillSetLastUpdatedTime() {
		Task task1 = new Task("task1");
		fileTaskDao.storeTasks(CollectionsUtil.asSet(task1));

		Task updated = new Task("newName", "newDescription");
		fileTaskDao.updateTask(task1, updated);

		Set<Task> newTasks = fileTaskDao.getTasks();
		Task updatedTask = newTasks.iterator().next();

		assertFalse("The last updated time should have been set!",
				Task.DEFAULT_LAST_UPDATE_TIME == updatedTask
						.getLastUpdatedTime());
	}

	@Test
	public void testGetTaskByNameReturnsCorrectTask() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2", "task2 description");
		Task task3 = new Task("task3", "task3 description");

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2, task3));

		Task foundTask = fileTaskDao.getTaskByName("task2");

		assertEquals(
				"The returned task does not appear to be the correct one when getting by name!",
				task2, foundTask);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testGetTaskByNameThrowsIllegalArgumentExceptionWhenNoTaskIsFoundWithThatName() {
		Task task1 = new Task("task1");
		Task task2 = new Task("task2", "task2 description");
		Task task3 = new Task("task3", "task3 description");

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2, task3));
		fileTaskDao.getTaskByName("invalid");
	}

	@Test
	public void testGetTagsWillReturnAllTagsOnTasks() {
		Task task1 = new Task("task1");
		task1.addTags(Arrays.asList("tag1", "tag2"));
		Task task2 = new Task("task2");
		Task task3 = new Task("task3");
		task3.addTag("tag3");

		fileTaskDao.storeTasks(EditTaskPresenterTest.getDependenciesSet(task1,
				task2, task3));

		Set<String> actualTags = fileTaskDao.getTags();
		List<String> expectedTags = Arrays.asList("tag1", "tag2", "tag3");
		AssertUtil.assertEquals(expectedTags, actualTags);
	}

	@Test
	public void testIsAlreadyDisplayedSyncAccountPromptReturnsFalseByDefault() {
		assertFalse("Should have returned false by default!",
				fileTaskDao.isAlreadyDisplayedSyncPrompt());
	}

	@Test
	public void testIsAlreadyDisplayedSyncAccountPromptReturnsTrueIfSetWasCalled() {
		fileTaskDao.setAlreadyDisplayedSyncPrompt(true);
		assertTrue("Should have returned true after being set!",
				fileTaskDao.isAlreadyDisplayedSyncPrompt());
	}
}
