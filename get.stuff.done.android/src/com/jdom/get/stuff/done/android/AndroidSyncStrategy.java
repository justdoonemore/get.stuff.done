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
package com.jdom.get.stuff.done.android;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang.StringUtils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.extensions.android2.auth.GoogleAccountManager;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.apache.ApacheHttpTransport;
import com.google.api.client.http.json.JsonHttpRequest;
import com.google.api.client.http.json.JsonHttpRequestInitializer;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.Tasks.Builder;
import com.google.api.services.tasks.TasksRequest;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.presenter.SyncOption;
import com.jdom.get.stuff.done.sync.GoogleSynchronizeStrategy;
import com.jdom.get.stuff.done.sync.SynchronizeStrategy;

public class AndroidSyncStrategy extends GoogleSynchronizeStrategy implements
		SynchronizeStrategy {

	private static final String CLASS_NAME = AndroidSyncStrategy.class
			.getName();

	private static AtomicBoolean running = new AtomicBoolean(false);

	private final Activity activity;

	private final AndroidApplicationContextFactory contextFactory;

	public AndroidSyncStrategy(Activity activity,
			AndroidApplicationContextFactory androidApplicationContextFactory) {
		super(androidApplicationContextFactory.getDaoFactory()
				.getApplicationDao());
		this.activity = activity;
		this.contextFactory = androidApplicationContextFactory;
	}

	public void synchronize() {
		synchronize(false, null);
	}

	public void synchronizeSynchronously(Runnable afterCompletionAction) {
		synchronize(true, afterCompletionAction);
	}

	public void synchronizeIfScheduledTimeHasElapsed() {
		long lastSyncTime = contextFactory.getDaoFactory().getApplicationDao()
				.getLastSyncTime();
		long timeSinceLastSync = System.currentTimeMillis() - lastSyncTime;
		boolean syncRequired = Constants.SYNC_FREQUENCY < timeSinceLastSync;
		Log.d(CLASS_NAME, "Sync required: " + syncRequired + ".  Last synced ["
				+ timeSinceLastSync + "] ms ago.");
		if (syncRequired) {
			synchronizeIfEnabled(SyncOption.PERIODICALLY);
		}
	}

	private void synchronize(boolean synchronously,
			final Runnable afterCompletionAction) {
		// Shortcut if there is no account configured for syncing
		if (contextFactory.getDaoFactory().getApplicationDao().getSyncAccount() == null) {
			return;
		}

		if (!running.compareAndSet(false, true)) {
			return;
		}

		Thread t = new Thread() {
			@Override
			public void run() {
				try {
					synchronizeWithRemote();
				} finally {
					if (afterCompletionAction != null) {
						afterCompletionAction.run();
					}
				}
			}
		};
		t.start();
	}

	@Override
	public void synchronizeWithRemote() {
		long start = System.currentTimeMillis();
		Log.i(CLASS_NAME, "Started syncing with remote server at "
				+ new SimpleDateFormat().format(new Date()));
		try {
			Tasks service = getTasksService();
			ApplicationDao dao = contextFactory.getDaoFactory()
					.getApplicationDao();

			Map<String, List<com.google.api.services.tasks.model.Task>> listNameToTasks = new HashMap<String, List<com.google.api.services.tasks.model.Task>>();
			Map<String, String> listNameToRemoteIdentifier = new HashMap<String, String>();
			Map<String, TaskList> listNameToTaskList = new HashMap<String, TaskList>();
			Map<String, String> taskIdToListId = new HashMap<String, String>();
			Map<String, String> taskNameToRemoteIdentifier = new HashMap<String, String>();

			long portionStart = System.currentTimeMillis();
			TaskLists taskLists = service.tasklists().list().execute();
			AndroidSyncStrategy.logTimeTaken("Retrieving task lists",
					portionStart);
			TaskList defaultTaskList = service.tasklists()
					.get(Constants.GOOGLE_DEFAULT_LIST_REMOTE_ID).execute();
			Set<com.jdom.get.stuff.done.domain.TaskList> localTaskLists = dao
					.getTaskLists();
			Set<Task> localTasks = dao.getTasks();
			List<TaskList> googleTaskLists = taskLists.getItems();

			for (TaskList googleTaskList : googleTaskLists) {
				String googleListTitle = googleTaskList.getTitle();
				String remoteId = googleTaskList.getId();
				if (StringUtils.isEmpty(googleListTitle)) {
					continue;
				} else if (googleListTitle.equals(defaultTaskList.getTitle())) {
					remoteId = Constants.GOOGLE_DEFAULT_LIST_REMOTE_ID;
				}
				listNameToRemoteIdentifier.put(googleListTitle, remoteId);
				listNameToTaskList.put(googleListTitle, googleTaskList);
				List<com.google.api.services.tasks.model.Task> googleTasksForList = new ArrayList<com.google.api.services.tasks.model.Task>();

				portionStart = System.currentTimeMillis();
				List<com.google.api.services.tasks.model.Task> googleTasks = service
						.tasks().list(remoteId).execute().getItems();
				AndroidSyncStrategy.logTimeTaken("Retrieving tasks for list "
						+ googleListTitle, portionStart);
				if (googleTasks == null || googleTasks.isEmpty()) {
					continue;
				}

				for (com.google.api.services.tasks.model.Task googleTask : googleTasks) {
					String taskTitle = googleTask.getTitle();
					if (StringUtils.isEmpty(taskTitle)) {
						continue;
					}
					taskNameToRemoteIdentifier.put(taskTitle,
							googleTask.getId());
					taskIdToListId.put(googleTask.getId(), remoteId);
					googleTasksForList.add(googleTask);
				}
				listNameToTasks.put(googleListTitle, googleTasksForList);
			}

			// Check for any lists that were renamed remotely
			for (String listName : listNameToRemoteIdentifier.keySet()) {
				String remoteId = listNameToRemoteIdentifier.get(listName);

				com.jdom.get.stuff.done.domain.TaskList localVersion = findLocalTaskListWithRemoteId(
						remoteId, localTaskLists);
				// No local version, create one
				if (localVersion == null) {
					long localStart = System.currentTimeMillis();
					com.jdom.get.stuff.done.domain.TaskList localTaskList = new com.jdom.get.stuff.done.domain.TaskList(
							listName);
					localTaskList.setRemoteIdentifier(remoteId);
					localTaskLists.add(localTaskList);
					dao.addTaskList(localTaskList);
					AndroidSyncStrategy.logTimeTaken(
							"Create local list from remote", localStart);
				} else if (!Constants.DEFAULT_LIST.equals(localVersion
						.getName()) && !localVersion.getName().equals(listName)) {
					long lastSyncTime = dao.getLastSyncTime();
					// If hasn't been updated since last sync, then was renamed
					// remotely
					if (localVersion.getLastUpdatedTime() <= lastSyncTime) {
						long localStart = System.currentTimeMillis();
						com.jdom.get.stuff.done.domain.TaskList newVersion = localVersion
								.clone();
						newVersion.setName(listName);
						dao.updateTaskList(localVersion, newVersion);
						AndroidSyncStrategy.logTimeTaken(
								"Update local list from remote", localStart);
					} else {
						long localStart = System.currentTimeMillis();
						// If has been updated since last sync, then was renamed
						// locally
						TaskList googleTaskList = listNameToTaskList
								.get(listName);
						googleTaskList.setTitle(localVersion.getName());
						service.tasklists().update(remoteId, googleTaskList)
								.execute();
						AndroidSyncStrategy.logTimeTaken(
								"Update remote list from local", localStart);
					}
				}
			}

			Iterator<com.jdom.get.stuff.done.domain.TaskList> iter = localTaskLists
					.iterator();
			List<com.jdom.get.stuff.done.domain.TaskList> updatedList = new ArrayList<com.jdom.get.stuff.done.domain.TaskList>();
			while (iter.hasNext()) {
				com.jdom.get.stuff.done.domain.TaskList localTaskList = iter
						.next();
				String remoteId = localTaskList.getRemoteIdentifier();
				// Any locally created lists that need a remote version created
				if (remoteId == null) {
					TaskList remote = new TaskList();
					remote.setTitle(localTaskList.getName());
					TaskList created = service.tasklists().insert(remote)
							.execute();
					com.jdom.get.stuff.done.domain.TaskList updated = localTaskList
							.clone();
					updated.setRemoteIdentifier(created.getId());
					dao.updateTaskList(localTaskList, updated);
					updatedList.add(updated);
					iter.remove();
				}
				// Any local task list with remote identifier that doesn't have
				// remote version were deleted remotely
				else if (!listNameToRemoteIdentifier.containsValue(remoteId)) {
					long localStart = System.currentTimeMillis();
					dao.deleteTaskList(localTaskList);
					iter.remove();
					AndroidSyncStrategy.logTimeTaken(
							"Delete local task list because remote was",
							localStart);
				}
				// Flagged for deletion, remove remote version and local
				else if (localTaskList.isDeleted()) {
					long localStart = System.currentTimeMillis();
					service.tasklists().delete(remoteId).execute();
					dao.deleteTaskList(localTaskList);
					iter.remove();
					AndroidSyncStrategy.logTimeTaken(
							"Delete remote task list because local was",
							localStart);
				}
			}

			localTaskLists.addAll(updatedList);

			// Now check for tasks
			for (String listName : listNameToTasks.keySet()) {
				List<com.google.api.services.tasks.model.Task> googleTasksForList = listNameToTasks
						.get(listName);
				long lastSyncTime = dao.getLastSyncTime();

				for (com.google.api.services.tasks.model.Task googleTask : googleTasksForList) {
					String remoteId = googleTask.getId();

					com.jdom.get.stuff.done.domain.Task localVersion = findLocalTaskWithRemoteId(
							remoteId, localTasks);
					// No local version, create one
					if (localVersion == null) {
						long localStart = System.currentTimeMillis();
						localVersion = createLocalTask(googleTask, listName);
						localTasks.add(localVersion);
						dao.addTask(localVersion);
						AndroidSyncStrategy.logTimeTaken(
								"Create local task from remote", localStart);
					} else if (localVersion.getLastUpdatedTime() > lastSyncTime) {
						long localStart = System.currentTimeMillis();
						// If has been updated since last sync, then was
						// changed locally
						String originalRemoteId = localVersion
								.getRemoteIdentifier();
						Task updated = createRemoteTask(service, dao,
								localVersion, listNameToRemoteIdentifier);
						localVersion.setRemoteIdentifier(updated
								.getRemoteIdentifier());
						taskNameToRemoteIdentifier.put(localVersion.getName(),
								updated.getRemoteIdentifier());

						service.tasks()
								.delete(listNameToRemoteIdentifier
										.get(listName),
										originalRemoteId).execute();
						AndroidSyncStrategy.logTimeTaken(
								"Update remote task from local", localStart);
					}
					// If hasn't been updated since last sync, then may
					// have been changed remotely
					else if (!localVersion.equals(googleTask, listName)) {
						long localStart = System.currentTimeMillis();
						com.jdom.get.stuff.done.domain.Task newVersion = createLocalTask(
								googleTask, listName);
						dao.updateTask(localVersion, newVersion);
						AndroidSyncStrategy.logTimeTaken(
								"Update local task from remote", localStart);
					}
				}
			}

			Iterator<com.jdom.get.stuff.done.domain.Task> taskIter = dao
					.getTasks().iterator();
			List<com.jdom.get.stuff.done.domain.Task> taskUpdatedList = new ArrayList<com.jdom.get.stuff.done.domain.Task>();
			while (taskIter.hasNext()) {
				com.jdom.get.stuff.done.domain.Task localTask = taskIter
						.next();
				String remoteId = localTask.getRemoteIdentifier();
				// If remoteId is null, then this is a local task that needs a
				// remote created
				if (remoteId == null) {
					long localStart = System.currentTimeMillis();
					Task updated = createRemoteTask(service, dao, localTask,
							listNameToRemoteIdentifier);

					taskUpdatedList.add(updated);
					taskIter.remove();
					AndroidSyncStrategy.logTimeTaken(
							"Create remote task from local", localStart);
				}
				// Any local task with remote identifier that doesn't have
				// remote version were deleted remotely
				else if (!taskNameToRemoteIdentifier.containsValue(remoteId)) {
					long localStart = System.currentTimeMillis();
					dao.deleteTask(localTask);
					taskIter.remove();
					AndroidSyncStrategy.logTimeTaken(
							"Delete local task because remote was", localStart);
				}
				// Flagged for deletion, remove remote version and local
				else if (localTask.isDeleted()) {
					long localStart = System.currentTimeMillis();
					service.tasks()
							.delete(taskIdToListId.get(remoteId), remoteId)
							.execute();
					dao.deleteTask(localTask);
					taskIter.remove();
					AndroidSyncStrategy.logTimeTaken(
							"Delete remote task because local was", localStart);
				}
			}

			localTasks.addAll(taskUpdatedList);

			// Update the last sync time
			dao.setLastSyncTime(System.currentTimeMillis());
		} catch (Exception e) {
			handleException(e);
		} finally {
			AndroidSyncStrategy.logTimeTaken(
					"Finished syncing with remote server", start);
			running.set(false);
		}
	}

	private static void logTimeTaken(String action, long portionStart) {
		Log.d(CLASS_NAME, action + " ["
				+ (System.currentTimeMillis() - portionStart) + "] ms.");
	}

	private Task createLocalTask(
			com.google.api.services.tasks.model.Task googleTask, String listName) {
		Boolean deleted = Boolean.TRUE.equals(googleTask.getDeleted());

		Task localVersion = new com.jdom.get.stuff.done.domain.Task(
				googleTask.getTitle(), googleTask.getNotes());
		localVersion.setDeleted(deleted);
		localVersion.setListName(listName);
		localVersion.setCompleted(Constants.GOOGLE_COMPLETED_STRING
				.equals(googleTask.getStatus()));
		localVersion.setDescription(googleTask.getNotes());
		DateTime due = googleTask.getDue();
		if (due != null) {
			long utcTime = due.getValue();
			utcTime -= TimeZone.getDefault().getOffset(utcTime);
			localVersion.setDueDate(new Date(utcTime));
		}
		localVersion.setRemoteIdentifier(googleTask.getId());

		return localVersion;
	}

	private Task createRemoteTask(Tasks service, ApplicationDao dao,
			Task localTask, Map<String, String> listNameToRemoteIdentifier)
			throws IOException {
		com.google.api.services.tasks.model.Task newGoogleTask = new com.google.api.services.tasks.model.Task();
		newGoogleTask.setTitle(localTask.getName());
		newGoogleTask.setDeleted(localTask.isDeleted());
		newGoogleTask
				.setStatus(localTask.isCompleted() ? Constants.GOOGLE_COMPLETED_STRING
						: Constants.GOOGLE_INCOMPLETE_STRING);
		newGoogleTask.setNotes(localTask.getDescription());
		long time = localTask.getDueDate().getTime();
		time -= TimeZone.getDefault().getOffset(time);
		newGoogleTask.setDue(new DateTime(false, time, 0));

		String listName = localTask.getListName();
		String remoteId = listNameToRemoteIdentifier.get(listName);
		if (Constants.DEFAULT_LIST.equals(listName)) {
			remoteId = Constants.GOOGLE_DEFAULT_LIST_REMOTE_ID;
		}
		com.google.api.services.tasks.model.Task created = service.tasks()
				.insert(remoteId, newGoogleTask).execute();
		com.jdom.get.stuff.done.domain.Task updated = localTask.clone();
		localTask.setRemoteIdentifier(created.getId());
		updated.setRemoteIdentifier(created.getId());
		dao.updateTask(localTask, updated);

		return updated;
	}

	private Task findLocalTaskWithRemoteId(String remoteId, Set<Task> localTasks) {
		for (Task task : localTasks) {
			if (remoteId.equals(task.getRemoteIdentifier())) {
				return task;
			}
		}
		return null;
	}

	private com.jdom.get.stuff.done.domain.TaskList findLocalTaskListWithRemoteId(
			String remoteId,
			Set<com.jdom.get.stuff.done.domain.TaskList> localTaskLists) {
		for (com.jdom.get.stuff.done.domain.TaskList taskList : localTaskLists) {
			if (remoteId.equals(taskList.getRemoteIdentifier())) {
				return taskList;
			}
		}
		return null;
	}

	@Override
	protected List<TaskList> getRemoteTaskListsInternal() throws IOException {
		TaskLists taskLists = getTasksService().tasklists().list().execute();
		return taskLists.getItems();
	}

	@Override
	protected Tasks getTasksService() {
		HttpTransport transport = new ApacheHttpTransport();

		GoogleTasksInitializer initializer = new GoogleTasksInitializer(
				getToken());

		Builder builder = Tasks.builder(transport, new JacksonFactory());
		builder.setApplicationName(Constants.APP_NAME);
		builder.setHttpRequestInitializer(initializer);
		builder.setJsonHttpRequestInitializer(initializer);

		return builder.build();
	}

	private Account getAccount() {
		Account[] accounts = AccountManager.get(activity).getAccountsByType(
				Constants.GOOGLE_ACCOUNT_PREFIX);
		for (Account account : accounts) {
			if (account.name.equals(contextFactory.getDaoFactory()
					.getApplicationDao().getSyncAccount())) {
				return account;
			}
		}
		return null;
	}

	private String getToken() {
		long start = System.currentTimeMillis();
		SharedPreferences prefs = activity.getSharedPreferences(
				Constants.AUTH_TOKEN_TYPE, Activity.MODE_PRIVATE);
		String token = prefs.getString(Constants.AUTH_TOKEN_TYPE, null);
		long lastTokenRetrievalTime = prefs.getLong(
				Constants.TOKEN_RETRIEVAL_TIME, 0);

		long timeSinceRetrievedLastToken = System.currentTimeMillis()
				- lastTokenRetrievalTime;
		long timeStillValid = Constants.TOKEN_VALID_DURATION
				- timeSinceRetrievedLastToken;
		if (token == null || timeStillValid < 1) {
			long portionStart = System.currentTimeMillis();
			Log.d(CLASS_NAME, "Token is no longer valid.");
			GoogleAccountManager accountManager = new GoogleAccountManager(
					activity);
			Account account = getAccount();

			accountManager.manager.invalidateAuthToken(account.type, token);
			AccountManagerFuture<Bundle> future = accountManager.manager
					.getAuthToken(account, Constants.AUTH_TOKEN_TYPE, null,
							activity, null, null);
			try {
				Bundle result = future.getResult();
				token = result.getString(AccountManager.KEY_AUTHTOKEN);

				Editor editor = prefs.edit();
				editor.putString(Constants.AUTH_TOKEN_TYPE, token);
				editor.putLong(Constants.TOKEN_RETRIEVAL_TIME,
						System.currentTimeMillis());
				editor.commit();

			} catch (Exception e) {
				handleException(new IOException(e));
			}
			AndroidSyncStrategy.logTimeTaken("Retrieving new token",
					portionStart);
		} else {
			Log.d(CLASS_NAME, "Token is still valid for [" + timeStillValid
					+ "] ms.");
		}

		AndroidSyncStrategy.logTimeTaken("Retrieving token", start);
		return token;
	}

	@Override
	protected void handleException(Exception e) {
		Log.e(CLASS_NAME, "Caught exception while syncing!", e);
	}

	private static final class GoogleTasksInitializer extends
			GoogleAccessProtectedResource implements JsonHttpRequestInitializer {
		public GoogleTasksInitializer(String accessToken) {
			super(accessToken);
		}

		public void initialize(JsonHttpRequest request) throws IOException {
			TasksRequest tasksRequest = (TasksRequest) request;
			tasksRequest.setPrettyPrint(true);
			tasksRequest.setKey(Constants.API_KEY);
		}
	}

	public void synchronizeIfEnabled(SyncOption syncOption) {
		Integer syncOptionConfiguration = contextFactory.getDaoFactory()
				.getApplicationDao().getSyncOptionsConfiguration();
		if (syncOption.isEnabled(syncOptionConfiguration)) {
			Log.d(CLASS_NAME, "Synchronizing because [" + syncOption
					+ "] is enabled.");
			synchronize();
		} else {
			Log.d(CLASS_NAME, "Not synchronizing because [" + syncOption
					+ "] is disabled.");
		}
	}
}
