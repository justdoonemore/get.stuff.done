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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class BaseSynchronizeStrategy<LOCAL_TASK extends LocalTask, REMOTE_TASK extends RemoteTask, LOCAL_TASK_LIST extends LocalTaskList, REMOTE_TASK_LIST extends RemoteTaskList>
		implements SynchronizeStrategy {
	private final SynchableItemStrategy<LOCAL_TASK_LIST, REMOTE_TASK_LIST> taskListSynchableItemStrategy;

	private final SynchableItemStrategy<LOCAL_TASK, REMOTE_TASK> taskSynchableItemStrategy;

	public BaseSynchronizeStrategy(
			SynchableItemStrategy<LOCAL_TASK_LIST, REMOTE_TASK_LIST> taskListStrategy,
			SynchableItemStrategy<LOCAL_TASK, REMOTE_TASK> taskStrategy) {
		this.taskListSynchableItemStrategy = taskListStrategy;
		this.taskSynchableItemStrategy = taskStrategy;
	}

	public void synchronizeWithRemote() {
		try {
			List<REMOTE_TASK_LIST> remoteTaskLists = getRemoteTaskLists();
			List<LOCAL_TASK_LIST> localTaskLists = getLocalTaskLists();
			synchronizeSynchableItems(localTaskLists, remoteTaskLists,
					taskListSynchableItemStrategy);

			List<REMOTE_TASK> remoteTasks = getRemoteTasks();
			List<LOCAL_TASK> localTasks = getLocalTasks();
			synchronizeSynchableItems(localTasks, remoteTasks,
					taskSynchableItemStrategy);
		} catch (IOException e) {
			handleException(e);
		}
	}

	private <LOCAL_TYPE extends LocalSynchableItem, REMOTE_TYPE extends RemoteSynchableItem> void synchronizeSynchableItems(
			List<LOCAL_TYPE> localItems, List<REMOTE_TYPE> remoteItems,
			SynchableItemStrategy<LOCAL_TYPE, REMOTE_TYPE> strategy)
			throws IOException {
		List<REMOTE_TYPE> remoteItemsToSave = new ArrayList<REMOTE_TYPE>();

		for (REMOTE_TYPE remote : remoteItems) {
			String remoteId = remote.getId();
			LOCAL_TYPE local = findLocalSynchableItemWithRemoteId(localItems,
					remoteId);

			// No local version exists via remoteId
			if (local == null) {
				local = findLocalSynchableItemWithSameName(localItems,
						remote.getName());
				// Doesn't exist locally yet, new list from remote
				if (local == null) {
					local = strategy.createLocalItem(remote);
					local.setRemoteIdentifier(remote.getId());
					localItems.add(local);
				}
				// Found local list with same name that doesn't have the
				// remote identifier set to the same value
				else {
					// TODO: What if it has a different remote identifier?
					local.setRemoteIdentifier(remoteId);
				}
			}
			// Local version exists but is flagged for deletion
			else if (local.isDeleted()) {
				strategy.deleteRemoteSynchableItem(remote);
				strategy.deleteLocalSynchableItem(local);
			}
			// Local version exists
			else {
				if (strategy.itemsAreDifferent(remote, local)) {
					if (local.getLastUpdatedTime() <= local.getLastSyncTime()) {
						strategy.modifyLocalItem(remote, local);
					} else {
						strategy.modifyRemoteItem(local, remote);
						remoteItemsToSave.add(remote);
					}
				}
			}
		}

		Iterator<LOCAL_TYPE> iter = localItems.iterator();
		while (iter.hasNext()) {
			LOCAL_TYPE local = iter.next();
			local.checkForSettingDefaultValues();
			String remoteIdentifier = local.getRemoteIdentifier();
			// The local item doesn't have a remote identifier, must have
			// been created locally
			if (remoteIdentifier == null) {
				REMOTE_TYPE remote = strategy.createRemoteItem(local);
				local.setRemoteIdentifier(remote.getId());
			} else {
				REMOTE_TYPE remote = findRemoteSynchableItemWithRemoteId(
						remoteItems, remoteIdentifier);
				// The local item has a remote identifier, so the remote
				// version
				// must have been deleted.
				if (remote == null) {
					strategy.deleteLocalSynchableItem(local);
					iter.remove();
				}
				// Local version has been modified since the last sync
				else if (local.getLastUpdatedTime() > local.getLastSyncTime()) {
					// Remote was updated more recently than local, use its
					// version
					if (remote.getLastUpdatedTime() > local
							.getLastUpdatedTime()) {
						strategy.modifyLocalItem(remote, local);
					}
					// Local was updated more recently, use its version
					else {
						remote = strategy.modifyRemoteItem(local, remote);
						remoteItemsToSave.add(remote);
					}
				}
			}
		}

		long currentTime = System.currentTimeMillis();
		for (LOCAL_TYPE local : localItems) {
			local.setLastSyncTime(currentTime);
			strategy.saveLocalSynchableItem(local);
		}

		for (REMOTE_TYPE remote : remoteItemsToSave) {
			strategy.saveRemoteSynchableItem(remote);
		}
	}

	private <REMOTE_TYPE extends RemoteSynchableItem> REMOTE_TYPE findRemoteSynchableItemWithRemoteId(
			List<REMOTE_TYPE> remoteItems, String remoteIdentifier) {
		for (REMOTE_TYPE remote : remoteItems) {
			if (remote.getId().equals(remoteIdentifier)) {
				return remote;
			}
		}

		return null;
	}

	protected <LOCAL_TYPE extends LocalSynchableItem> LOCAL_TYPE findLocalSynchableItemWithRemoteId(
			List<LOCAL_TYPE> localItems, String remoteId) {
		for (LOCAL_TYPE local : localItems) {
			if (remoteId.equals(local.getRemoteIdentifier())) {
				return local;
			}
		}

		return null;
	}

	private <LOCAL_TYPE extends LocalSynchableItem> LOCAL_TYPE findLocalSynchableItemWithSameName(
			List<LOCAL_TYPE> localItems, String name) {
		for (LOCAL_TYPE local : localItems) {
			if (name.equals(local.getName())) {
				return local;
			}
		}

		return null;
	}

	protected abstract List<REMOTE_TASK_LIST> getRemoteTaskLists()
			throws IOException;

	protected abstract List<LOCAL_TASK_LIST> getLocalTaskLists();

	protected abstract List<REMOTE_TASK> getRemoteTasks() throws IOException;

	protected abstract List<LOCAL_TASK> getLocalTasks();

	/**
	 * This method must be implemented in a way to notify the user of some
	 * action they can take to fix synching. No synching will be performed until
	 * the problems encountered in this exception are resolved.
	 * 
	 * @param e
	 *            the exception
	 */
	protected abstract void handleException(Exception e);
}
