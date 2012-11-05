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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.properties.PropertiesUtil;

public class FileApplicationDao extends BaseApplicationDao {

	static final String TASKS_PROPERTIES = "tasks.properties";
	static final String TASK_LISTS_PROPERTIES = "task_lists.properties";
	static final String SETTINGS_FILE = "settings.properties";
	static final String TASKS_KEY = "tasks";
	public static final String DEPENDENCY_SUFFIX = ".dependencies";
	private static final String DESCRIPTION_SUFFIX = ".description";
	private static final String COMPLETED_SUFFIX = ".completed";
	private static final String TASK_LIST_SUFFIX = ".taskList";
	private static final String TASK_LISTS_KEY = "taskLists";
	private static final String TAGS_SUFFIX = ".tags";
	private static final String DUE_DATE_SUFFIX = ".dueDate";
	private static final String SYNC_ACCOUNT_KEY = "syncAccount";
	private static final String LAST_SYNC_TIME_KEY = "lastSyncTime";
	private static final String REMOTE_ID_SUFFIX = ".remoteId";
	private static final String LAST_SYNC_TIME_SUFFIX = ".lastSyncTime";
	private static final String LAST_UPDATED_TIME_SUFFIX = ".lastUpdatedTime";
	private static final String DELETED_SUFFIX = ".deleted";
	private static final String TASK_LIST_REMOTE_ID_SUFFIX = ".remoteIdTaskList";
	private static final String TASK_LIST_LAST_SYNC_TIME_SUFFIX = ".lastSyncTimeTaskList";
	private static final String TASK_LIST_LAST_UPDATED_TIME_SUFFIX = ".lastUpdatedTimeTaskList";
	private static final String TASK_LIST_DELETED_SUFFIX = ".deletedTaskList";
	private static final String ALREADY_DISPLAYED_SYNC_PROMPT = "alreadyDisplayedSyncPrompt";
	private static final String TASK_FIELD_CONFIGURATION = "taskFieldConfiguration";
	private static final String SYNC_OPTIONS_CONFIGURATION = "syncOptionsConfiguration";

	private final File baseDirectory;

	public FileApplicationDao(File baseDirectory) {
		this.baseDirectory = baseDirectory;
	}

	public Set<Task> getTasks() {
		synchronized (FileApplicationDao.class) {
			Set<Task> tasks = new TreeSet<Task>();

			File tasksFile = getOrCreateAndGetFile(TASKS_PROPERTIES);

			if (!tasksFile.exists()) {
				storeTasks(Collections.<Task> emptySet());
			}

			Properties properties = PropertiesUtil
					.readPropertiesFile(tasksFile);
			List<String> taskNames = PropertiesUtil.getPropertyAsList(
					properties, TASKS_KEY);

			for (String taskName : taskNames) {
				String description = properties.getProperty(taskName
						+ DESCRIPTION_SUFFIX);
				String taskList = properties.getProperty(taskName
						+ TASK_LIST_SUFFIX);

				Task task = new Task(taskName, description, taskList);
				task.setCompleted(PropertiesUtil.getBoolean(properties,
						taskName + COMPLETED_SUFFIX, false));
				task.addTags(PropertiesUtil.getPropertyAsSet(properties,
						taskName + TAGS_SUFFIX));
				task.setDueDate(new Date(PropertiesUtil.getLong(properties,
						taskName + DUE_DATE_SUFFIX)));
				task.setRemoteIdentifier(properties.getProperty(taskName
						+ REMOTE_ID_SUFFIX, null));
				task.setLastSyncTime(PropertiesUtil.getLong(properties,
						taskName + LAST_SYNC_TIME_SUFFIX));
				task.setLastUpdatedTime(PropertiesUtil.getLong(properties,
						taskName + LAST_UPDATED_TIME_SUFFIX));
				task.setDeleted(PropertiesUtil.getBoolean(properties, taskName
						+ DELETED_SUFFIX, false));

				tasks.add(task);

			}

			// Now that all tasks have been created, map dependencies
			for (String taskName : taskNames) {
				List<String> dependencyNames = PropertiesUtil
						.getPropertyAsList(properties, taskName
								+ DEPENDENCY_SUFFIX);

				if (!dependencyNames.isEmpty()) {
					Task task = findTaskInCollectionByName(tasks, taskName);
					for (String dependencyName : dependencyNames) {
						Task dependency = findTaskInCollectionByName(tasks,
								dependencyName);
						task.addDependency(dependency);
					}
				}
			}

			return tasks;
		}
	}

	private File getOrCreateAndGetFile(String taskListsProperties) {
		synchronized (FileApplicationDao.class) {
			File file = new File(baseDirectory, taskListsProperties);
			if (!file.exists()) {
				PropertiesUtil.writePropertiesFile(new Properties(), file);
			}
			return file;
		}
	}

	static Task findTaskInCollectionByName(Collection<Task> tasks,
			String taskName) {
		for (Task task : tasks) {
			if (taskName.equals(task.getName())) {
				return task;
			}
		}
		throw new IllegalArgumentException("Unable to find a task by name ["
				+ taskName + "] in the collection!");
	}

	public void storeTasks(Set<Task> tasks) {
		synchronized (FileApplicationDao.class) {
			Properties properties = new Properties();
			storeTaskInformationToProperties(properties, tasks);
			PropertiesUtil.writePropertiesFile(properties, new File(
					baseDirectory, TASKS_PROPERTIES));
		}
	}

	@Override
	public void storeTaskLists(Set<TaskList> taskLists) {
		synchronized (FileApplicationDao.class) {
			Properties properties = new Properties();
			storeTaskListInformationToProperties(properties, taskLists);
			PropertiesUtil.writePropertiesFile(properties, new File(
					baseDirectory, TASK_LISTS_PROPERTIES));
		}
	}

	public Set<TaskList> getTaskLists() {
		synchronized (FileApplicationDao.class) {
			Set<TaskList> taskLists = new TreeSet<TaskList>();

			File taskListsFile = new File(baseDirectory, TASK_LISTS_PROPERTIES);

			if (!taskListsFile.exists()) {
				storeTaskLists(CollectionsUtil.asSet(new TaskList(
						Constants.DEFAULT_LIST)));
			}

			Properties properties = PropertiesUtil.readPropertiesFile(new File(
					baseDirectory, TASK_LISTS_PROPERTIES));

			List<String> taskListNames = PropertiesUtil.getPropertyAsList(
					properties, TASK_LISTS_KEY);

			for (String taskListName : taskListNames) {
				TaskList taskList = new TaskList(taskListName);

				taskList.setRemoteIdentifier(properties.getProperty(
						taskListName + TASK_LIST_REMOTE_ID_SUFFIX, null));
				taskList.setLastSyncTime(PropertiesUtil.getLong(properties,
						taskListName + TASK_LIST_LAST_SYNC_TIME_SUFFIX));
				taskList.setLastUpdatedTime(PropertiesUtil.getLong(properties,
						taskListName + TASK_LIST_LAST_UPDATED_TIME_SUFFIX));
				taskList.setDeleted(PropertiesUtil.getBoolean(properties,
						taskListName + TASK_LIST_DELETED_SUFFIX, false));

				taskLists.add(taskList);
			}
			return taskLists;
		}
	}

	private void storeTaskInformationToProperties(Properties properties,
			Set<Task> tasks) {
		SortedSet<String> taskNames = new TreeSet<String>();
		for (Task task : tasks) {
			String taskName = task.getName();
			taskNames.add(taskName);

			// Description
			properties.setProperty(taskName + DESCRIPTION_SUFFIX,
					task.getDescription());

			// Completed
			properties.setProperty(taskName + COMPLETED_SUFFIX,
					Boolean.toString(task.isCompleted()));

			// Task List
			properties.setProperty(taskName + TASK_LIST_SUFFIX,
					task.getTaskList());

			// Tags
			properties.setProperty(taskName + TAGS_SUFFIX,
					PropertiesUtil.join(task.getTags()));

			// Due Date
			properties.setProperty(taskName + DUE_DATE_SUFFIX, ""
					+ task.getDueDate().getTime());

			// Remote Identifier
			String remoteId = task.getRemoteIdentifier();
			if (remoteId != null) {
				properties.setProperty(taskName + REMOTE_ID_SUFFIX, remoteId);
			}

			// Last Sync Time
			properties.setProperty(taskName + LAST_SYNC_TIME_SUFFIX,
					"" + task.getLastSyncTime());

			// Last Updated Time
			properties.setProperty(taskName + LAST_UPDATED_TIME_SUFFIX, ""
					+ task.getLastUpdatedTime());

			// Deleted
			properties.setProperty(taskName + DELETED_SUFFIX,
					"" + task.isDeleted());

			// Write out dependencies
			Set<Task> dependencies = task.getDependencies();
			if (!dependencies.isEmpty()) {
				List<String> dependencyNames = new ArrayList<String>();
				for (Task dependency : dependencies) {
					dependencyNames.add(dependency.getName());
				}
				properties.setProperty(taskName + DEPENDENCY_SUFFIX,
						PropertiesUtil.join(dependencyNames));
			}
		}

		properties.setProperty(TASKS_KEY, PropertiesUtil.join(taskNames));
	}

	private void storeTaskListInformationToProperties(Properties properties,
			Set<TaskList> taskLists) {
		SortedSet<String> taskListNames = new TreeSet<String>();

		for (TaskList taskList : taskLists) {
			String taskListName = taskList.getName();
			taskListNames.add(taskListName);

			// Remote Identifier
			String remoteId = taskList.getRemoteIdentifier();
			if (remoteId != null) {
				properties.setProperty(taskListName
						+ TASK_LIST_REMOTE_ID_SUFFIX, remoteId);
			}

			// Last Sync Time
			properties.setProperty(taskListName
					+ TASK_LIST_LAST_SYNC_TIME_SUFFIX,
					"" + taskList.getLastSyncTime());

			// Last Updated Time
			properties.setProperty(taskListName
					+ TASK_LIST_LAST_UPDATED_TIME_SUFFIX,
					"" + taskList.getLastUpdatedTime());

			// Deleted
			properties.setProperty(taskListName + TASK_LIST_DELETED_SUFFIX, ""
					+ taskList.isDeleted());
		}
		properties.setProperty(TASK_LISTS_KEY,
				PropertiesUtil.join(taskListNames));
	}

	public void enableSyncAccount(String account) {
		synchronized (FileApplicationDao.class) {
			File settingsFile = getOrCreateAndGetFile(SETTINGS_FILE);
			Properties properties = PropertiesUtil
					.readPropertiesFile(settingsFile);
			properties.setProperty(SYNC_ACCOUNT_KEY, account);
			PropertiesUtil.writePropertiesFile(properties, settingsFile);
		}
	}

	public void disableSyncAccount() {
		synchronized (FileApplicationDao.class) {
			File settingsFile = getOrCreateAndGetFile(SETTINGS_FILE);
			Properties properties = PropertiesUtil
					.readPropertiesFile(settingsFile);
			properties.remove(SYNC_ACCOUNT_KEY);
			PropertiesUtil.writePropertiesFile(properties, settingsFile);
		}
	}

	public String getSyncAccount() {
		synchronized (FileApplicationDao.class) {
			File settingsFile = getOrCreateAndGetFile(SETTINGS_FILE);
			Properties properties = PropertiesUtil
					.readPropertiesFile(settingsFile);
			return properties.getProperty(SYNC_ACCOUNT_KEY);
		}
	}

	public long getLastSyncTime() {
		synchronized (FileApplicationDao.class) {
			File settingsFile = getOrCreateAndGetFile(SETTINGS_FILE);
			Properties properties = PropertiesUtil
					.readPropertiesFile(settingsFile);
			return properties.containsKey(LAST_SYNC_TIME_KEY) ? PropertiesUtil
					.getLong(properties, LAST_SYNC_TIME_KEY) : 0L;
		}
	}

	public void setLastSyncTime(long time) {
		synchronized (FileApplicationDao.class) {
			File settingsFile = getOrCreateAndGetFile(SETTINGS_FILE);
			Properties properties = PropertiesUtil
					.readPropertiesFile(settingsFile);
			properties.setProperty(LAST_SYNC_TIME_KEY, "" + time);
			PropertiesUtil.writePropertiesFile(properties, settingsFile);
		}
	}

	public void updateTaskList(TaskList localTaskList, TaskList updated) {
		synchronized (FileApplicationDao.class) {
			String listName = updated.getName();
			if (!localTaskList.getName().equals(listName)) {
				Set<Task> tasks = getTasks();
				Set<Task> newTasks = new HashSet<Task>();
				for (Task task : tasks) {
					if (listName.equals(task.getListName())) {
						task.setListName(listName);
					}

					tasks.add(task);
				}

				storeTasks(newTasks);
			}

			Set<TaskList> taskLists = getTaskLists();
			taskLists.remove(localTaskList);
			taskLists.add(updated);
			updated.setLastUpdatedTime(System.currentTimeMillis());
			storeTaskLists(taskLists);
		}
	}

	public boolean isAlreadyDisplayedSyncPrompt() {
		synchronized (FileApplicationDao.class) {
			File settingsFile = getOrCreateAndGetFile(SETTINGS_FILE);
			Properties properties = PropertiesUtil
					.readPropertiesFile(settingsFile);
			return PropertiesUtil.getBoolean(properties,
					ALREADY_DISPLAYED_SYNC_PROMPT, false);
		}
	}

	public void setAlreadyDisplayedSyncPrompt(boolean displayed) {
		setSettingsProperty(ALREADY_DISPLAYED_SYNC_PROMPT, "" + displayed);
	}

	public void setTaskFieldConfiguration(int configuration) {
		setSettingsProperty(TASK_FIELD_CONFIGURATION, "" + configuration);
	}

	public Integer getTaskFieldConfiguration() {
		synchronized (FileApplicationDao.class) {
			File settingsFile = getOrCreateAndGetFile(SETTINGS_FILE);
			Properties properties = PropertiesUtil
					.readPropertiesFile(settingsFile);
			if (properties.containsKey(TASK_FIELD_CONFIGURATION)) {
				return PropertiesUtil.getInteger(properties,
						TASK_FIELD_CONFIGURATION);
			} else {
				return null;
			}
		}
	}

	private void setSettingsProperty(String key, String value) {
		synchronized (FileApplicationDao.class) {
			File settingsFile = getOrCreateAndGetFile(SETTINGS_FILE);
			Properties properties = PropertiesUtil
					.readPropertiesFile(settingsFile);
			properties.setProperty(key, value);
			PropertiesUtil.writePropertiesFile(properties, settingsFile);
		}
	}

	public void setSyncOptionsConfiguration(int configuration) {
		setSettingsProperty(SYNC_OPTIONS_CONFIGURATION, "" + configuration);
	}

	public Integer getSyncOptionsConfiguration() {
		synchronized (FileApplicationDao.class) {
			File settingsFile = getOrCreateAndGetFile(SETTINGS_FILE);
			Properties properties = PropertiesUtil
					.readPropertiesFile(settingsFile);
			if (properties.containsKey(SYNC_OPTIONS_CONFIGURATION)) {
				return PropertiesUtil.getInteger(properties,
						SYNC_OPTIONS_CONFIGURATION);
			} else {
				return null;
			}
		}
	}
}
