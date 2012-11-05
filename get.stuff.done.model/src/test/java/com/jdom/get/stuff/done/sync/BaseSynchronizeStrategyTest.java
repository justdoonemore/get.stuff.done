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
package com.jdom.get.stuff.done.sync;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.jdom.get.stuff.done.presenter.SyncOption;
import com.jdom.get.stuff.done.sync.BaseSynchronizeStrategy;
import com.jdom.get.stuff.done.sync.LocalTask;
import com.jdom.get.stuff.done.sync.LocalTaskList;

public class BaseSynchronizeStrategyTest {
	private final List<MockRemoteTaskList> remoteTaskLists = new ArrayList<MockRemoteTaskList>();
	private final List<MockLocalTaskList> localTaskLists = new ArrayList<MockLocalTaskList>();
	private final List<MockRemoteTask> remoteTasks = new ArrayList<MockRemoteTask>();
	private final List<MockLocalTask> localTasks = new ArrayList<MockLocalTask>();
	private final MockTaskListStrategy taskListSaveStrategy = new MockTaskListStrategy();
	private final MockTaskStrategy taskSaveStrategy = new MockTaskStrategy();
	private final BaseSynchronizeStrategy<MockLocalTask, MockRemoteTask, MockLocalTaskList, MockRemoteTaskList> synchronizer = new MockBaseSynchronizeStrategy();

	@Test
	public void testTaskListAddedRemoteExistsSameNameLocalButNoRemoteIdentifierSetsRemoteIdentifierAndSaves() {
		final String remoteId = "someRemoteId";
		final String name = "Inbox";

		MockLocalTaskList local = new MockLocalTaskList(name);
		remoteTaskLists.add(new MockRemoteTaskList(remoteId, name));
		localTaskLists.add(local);

		synchronizer.synchronizeWithRemote();

		assertEquals(
				"The remote identifier should have been set on the local task list!",
				remoteId, local.getRemoteIdentifier());
		assertTrue("Expected the local item to have been saved!",
				taskListSaveStrategy.savedLocalItems.contains(local));
	}

	@Test
	public void testTaskListDifferentNamesRemoteAndLocalIfLastUpdatedTimeNotGreaterThanLastSyncTimeSetsLocalNewNameAndSaves() {
		final String remoteId = "someRemoteId";
		final String name = "Inbox";
		final String differentName = "Inbox2";
		final long lastSyncTime = 25L;
		final long lastUpdatedTime = lastSyncTime;

		MockLocalTaskList local = new MockLocalTaskList(differentName, remoteId);
		local.setLastSyncTime(lastSyncTime);
		local.setLastUpdatedTime(lastUpdatedTime);

		remoteTaskLists.add(new MockRemoteTaskList(remoteId, name));
		localTaskLists.add(local);

		synchronizer.synchronizeWithRemote();

		assertEquals(
				"The new name should have been set on the local task list!",
				name, local.getName());

		assertTrue("Expected the local item to have been modified!",
				taskListSaveStrategy.modifiedLocalItems.contains(local));
		assertTrue("Expected the local item to have been saved!",
				taskListSaveStrategy.savedLocalItems.contains(local));
	}

	@Test
	public void testTaskListDifferentNamesRemoteAndLocalIfLastUpdatedTimeNewerThanLastSyncTimeSetsRemoteNewNameAndSaves() {
		final String remoteId = "someRemoteId";
		final String name = "Inbox";
		final String differentName = "Inbox2";
		final long lastSyncTime = 25L;
		final long lastUpdatedTime = 50L;

		MockLocalTaskList local = new MockLocalTaskList(differentName, remoteId);
		local.setLastSyncTime(lastSyncTime);
		local.setLastUpdatedTime(lastUpdatedTime);

		MockRemoteTaskList remote = new MockRemoteTaskList(remoteId, name);
		remoteTaskLists.add(remote);
		localTaskLists.add(local);

		synchronizer.synchronizeWithRemote();

		assertEquals(
				"The new name should have been set on the remote task list!",
				differentName, remote.getName());
		assertTrue("Expected the remote item to have been modified!",
				taskListSaveStrategy.modifiedRemoteItems.contains(remote));
		assertTrue("Expected the remote item to have been saved!",
				taskListSaveStrategy.savedRemoteItems.contains(remote));
	}

	@Test
	public void testTaskListAddedRemoteNotExistingLocalCreatesLocalAndSaves() {
		final String remoteId = "someRemoteId";
		final String name = "Inbox";

		MockRemoteTaskList remote = new MockRemoteTaskList(remoteId, name);
		remoteTaskLists.add(remote);

		synchronizer.synchronizeWithRemote();

		assertEquals("A new local task list should have been created!", 1,
				localTaskLists.size());
		LocalTaskList localTaskList = localTaskLists.iterator().next();
		assertEquals("Incorrect name for the local task list!", name,
				localTaskList.getName());
		assertEquals("Incorrect remote id for the local task list!", remoteId,
				localTaskList.getRemoteIdentifier());

		assertTrue("Expected the local item to have been saved!",
				taskListSaveStrategy.savedLocalItems.contains(localTaskList));
	}

	@Test
	public void testTaskListAddedLocalNotExistingRemoteCreatesRemoteAndSavesThenPutsRemoteIdOnLocal() {
		final String name = "Inbox";
		MockLocalTaskList local = new MockLocalTaskList(name);
		localTaskLists.add(local);

		synchronizer.synchronizeWithRemote();

		assertEquals(
				"The remote identifier should have been set on the local task!",
				MockTaskListStrategy.TASK_LIST_ID, local.getRemoteIdentifier());
	}

	@Test
	public void testTaskListDeletedRemoteDeletesLocalIfLastUpdatedTimeNotGreaterThanSyncTime() {
		final String remoteId = "someRemoteId";
		final String name = "Inbox";
		final long lastSyncTime = 25L;
		final long lastUpdatedTime = lastSyncTime;

		MockLocalTaskList local = new MockLocalTaskList(name, remoteId);
		local.setLastSyncTime(lastSyncTime);
		local.setLastUpdatedTime(lastUpdatedTime);

		localTaskLists.add(local);

		synchronizer.synchronizeWithRemote();

		assertTrue("Expected the local item to have been saved!",
				taskListSaveStrategy.deletedLocalItems.contains(local));
	}

	@Test
	public void testTaskListDeletedLocallyDeletesRemote() {
		final String remoteId = "someRemoteId";
		final String name = "Inbox";

		MockLocalTaskList local = new MockLocalTaskList(name, remoteId);
		local.setDeleted(true);
		localTaskLists.add(local);

		MockRemoteTaskList remote = new MockRemoteTaskList(remoteId, name);
		remoteTaskLists.add(remote);

		synchronizer.synchronizeWithRemote();

		assertTrue("Expected the local item to have been saved!",
				taskListSaveStrategy.deletedLocalItems.contains(local));
		assertTrue("Expected the local item to have been saved!",
				taskListSaveStrategy.deletedRemoteItems.contains(remote));
	}

	@Test
	public void testLocalTaskListsHaveLastSyncTimesSetEvenIfNoChanges() {
		final String remoteId = "someRemoteId";
		final String name = "Inbox";

		MockLocalTaskList local = new MockLocalTaskList(name, remoteId);
		local.setLastSyncTime(0);
		local.setLastUpdatedTime(0);

		MockRemoteTaskList remote = new MockRemoteTaskList(remoteId, name);
		remoteTaskLists.add(remote);
		localTaskLists.add(local);

		synchronizer.synchronizeWithRemote();

		assertTrue("The last sync time should have been set!",
				local.getLastSyncTime() > 0);
		assertTrue("Expected the local item to have been saved!",
				taskListSaveStrategy.savedLocalItems.contains(local));
	}

	@Test
	public void testLocalTaskAddedNotExistRemoteCreatesRemoteAndSetsRemoteIdOnLocal() {
		final String name = "someTask";

		MockLocalTask local = new MockLocalTask(name);
		localTasks.add(local);

		synchronizer.synchronizeWithRemote();

		assertEquals(
				"The remote identifier should have been set on the local task!",
				MockTaskStrategy.TASK_ID, local.getRemoteIdentifier());
	}

	@Test
	public void testRemoteTaskAddedNotExistingLocalCreatesLocalAndSetsRemoteIdOnLocal() {
		final String remoteId = "thisisremote";
		final String name = "someTask";

		MockRemoteTask remote = new MockRemoteTask(remoteId, name);
		remoteTasks.add(remote);

		synchronizer.synchronizeWithRemote();

		assertEquals("A local task should have been created and saved!", 1,
				taskSaveStrategy.savedLocalItems.size());
		LocalTask local = taskSaveStrategy.savedLocalItems.iterator().next();

		assertEquals(
				"The remote identifier should have been set on the local task!",
				remoteId, local.getRemoteIdentifier());
	}

	@Test
	public void testLocalTaskEditedAndNotRemoteUpdatesRemote() {
		final String remoteId = "thisisremote";
		final String name = "someTask";
		final String listName = "listName";

		MockRemoteTask remote = new MockRemoteTask(remoteId, name);
		remote.setListName(listName);
		remote.setLastUpdatedTime(25L);
		remoteTasks.add(remote);

		MockLocalTask local = new MockLocalTask(name, remoteId);
		local.setListName(listName);
		local.setLastUpdatedTime(50L);
		local.setLastSyncTime(25L);
		localTasks.add(local);

		synchronizer.synchronizeWithRemote();

		assertTrue("The remote item should have been modified!",
				taskSaveStrategy.modifiedRemoteItems.contains(remote));
		assertTrue("The remote item should have been saved!",
				taskSaveStrategy.savedRemoteItems.contains(remote));
	}

	@Test
	public void testLocalTaskEditedAndRemoteEditedNewerUpdatesLocal() {
		final String remoteId = "thisisremote";
		final String name = "someTask";
		final String listName = "listName";
		final long localLastUpdated = 50L;

		MockRemoteTask remote = new MockRemoteTask(remoteId, name);
		remote.setListName(listName);
		remote.setLastUpdatedTime(localLastUpdated + 1);
		remoteTasks.add(remote);

		MockLocalTask local = new MockLocalTask(name, remoteId);
		local.setListName(listName);
		local.setLastUpdatedTime(localLastUpdated);
		local.setLastSyncTime(25L);
		localTasks.add(local);

		synchronizer.synchronizeWithRemote();

		assertTrue("The local item should have been modified!",
				taskSaveStrategy.modifiedLocalItems.contains(local));
		assertTrue("The local item should have been saved!",
				taskSaveStrategy.savedLocalItems.contains(local));
	}

	@Test
	public void testTaskDeletedLocallyDeletesRemote() {
		final String remoteId = "someRemoteId";
		final String name = "someTaskName";

		MockLocalTask local = new MockLocalTask(name, remoteId);
		local.setDeleted(true);
		localTasks.add(local);

		MockRemoteTask remote = new MockRemoteTask(remoteId, name);
		remoteTasks.add(remote);

		synchronizer.synchronizeWithRemote();

		assertTrue("Expected the local item to have been deleted!",
				taskSaveStrategy.deletedLocalItems.contains(local));
		assertTrue("Expected the local item to have been deleted!",
				taskSaveStrategy.deletedRemoteItems.contains(remote));
	}

	@Test
	public void testTaskDeletedRemoteDeletesLocalIfLastUpdatedTimeNotGreaterThanSyncTime() {
		final String remoteId = "someRemoteId";
		final String name = "someTaskName";
		final long lastSyncTime = 25L;
		final long lastUpdatedTime = lastSyncTime;

		MockLocalTask local = new MockLocalTask(name, remoteId);
		local.setLastSyncTime(lastSyncTime);
		local.setLastUpdatedTime(lastUpdatedTime);

		localTasks.add(local);

		synchronizer.synchronizeWithRemote();

		assertTrue("Expected the local item to have been deleted!",
				taskSaveStrategy.deletedLocalItems.contains(local));
	}

	@Test
	public void testTaskChangesListRemoteChangesLocalVersion() {
		final String originalListName = "originalList";
		final String newListName = "newList";

		final long lastSyncTime = 1L;
		final String remoteTaskId = "someRemoteTaskId";
		final String taskName = "someTask";
		MockLocalTask local = new MockLocalTask(taskName, remoteTaskId);
		local.setListName(originalListName);
		local.setLastSyncTime(lastSyncTime);
		local.setLastUpdatedTime(lastSyncTime);
		localTasks.add(local);

		MockRemoteTask remote = new MockRemoteTask(remoteTaskId, taskName);
		remote.setListName(newListName);
		remote.setLastUpdatedTime(lastSyncTime + 1);
		remoteTasks.add(remote);

		synchronizer.synchronizeWithRemote();

		assertTrue("Expected the local item to have been modified!",
				taskSaveStrategy.modifiedLocalItems.contains(local));
		assertTrue("Expected the local item to have been saved!",
				taskSaveStrategy.savedLocalItems.contains(local));
		assertEquals("Expected the list name of the task to have changed!",
				newListName, local.getListName());

	}

	@Test
	public void testTaskChangesListLocalChangesRemoteVersion() {
		final String originalListName = "originalList";
		final String newListName = "newList";

		final long lastSyncTime = 1L;
		final String remoteTaskId = "someRemoteTaskId";
		final String taskName = "someTask";
		MockLocalTask local = new MockLocalTask(taskName, remoteTaskId);
		local.setListName(newListName);
		local.setLastSyncTime(lastSyncTime);
		// Was updated to move list, so is greater than last sync
		local.setLastUpdatedTime(lastSyncTime + 1);
		localTasks.add(local);

		MockRemoteTask remote = new MockRemoteTask(remoteTaskId, taskName);
		remote.setListName(originalListName);
		remote.setLastUpdatedTime(lastSyncTime - 1);
		remoteTasks.add(remote);

		synchronizer.synchronizeWithRemote();

		assertTrue("Expected the remote item to have been modified!",
				taskSaveStrategy.modifiedRemoteItems.contains(remote));
		assertTrue("Expected the remote item to have been saved!",
				taskSaveStrategy.savedRemoteItems.contains(remote));
		assertEquals("Expected the list name of the task to have changed!",
				newListName, remote.getListName());
	}

	private class MockBaseSynchronizeStrategy
			extends
			BaseSynchronizeStrategy<MockLocalTask, MockRemoteTask, MockLocalTaskList, MockRemoteTaskList> {
		public MockBaseSynchronizeStrategy() {
			super(taskListSaveStrategy, taskSaveStrategy);
		}

		@Override
		protected List<MockRemoteTaskList> getRemoteTaskLists() {
			return remoteTaskLists;
		}

		@Override
		protected List<MockLocalTaskList> getLocalTaskLists() {
			return localTaskLists;
		}

		@Override
		protected List<MockRemoteTask> getRemoteTasks() {
			return remoteTasks;
		}

		@Override
		protected List<MockLocalTask> getLocalTasks() {
			return localTasks;
		}

		@Override
		protected void handleException(Exception e) {
			throw new IllegalArgumentException(e);
		}

		public void synchronize() {
			synchronizeWithRemote();
		}

		public void synchronizeSynchronously(Runnable afterCompletionAction) {

		}

		public void synchronizeIfScheduledTimeHasElapsed() {

		}

		public void synchronizeIfEnabled(SyncOption pushOnChange) {
			// TODO Auto-generated method stub

		}
	}
}
