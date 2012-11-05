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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.model.SettingsModel;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.model.impl.SettingsModelImpl;
import com.jdom.util.patterns.mvp.ActionConfiguration;
import com.jdom.util.patterns.mvp.RunnableWithResults;
import com.jdom.util.patterns.observer.Subject;

public class SettingsPresenter extends
		BasePresenter<SettingsModel, SettingsView> {

	static final String TITLE = "Settings";

	public static SettingsPresenter construct(SettingsView view,
			Properties params) {
		return construct(new SettingsModelImpl(), view, params);
	}

	static SettingsPresenter construct(SettingsModel model, SettingsView view,
			Properties params) {
		SettingsPresenter presenter = new SettingsPresenter(model, view, params);
		model.addObserver(presenter);
		return presenter;
	}

	Runnable disableSyncAction = new Runnable() {
		public void run() {
			disableSync();
		}
	};

	Runnable selectTaskFieldsAction = new Runnable() {
		public void run() {
			if (!isProVersion()) {
				displayProVersionPopup();
				return;
			}
			selectTaskFields();
		}
	};

	Runnable selectSyncOptionsAction = new Runnable() {
		public void run() {
			if (!isProVersion()) {
				displayProVersionPopup();
				return;
			}
			selectSyncOptions();
		}
	};

	RunnableWithResults<Collection<String>> taskFieldsSelectionAction = new RunnableWithResults<Collection<String>>() {
		public void callback(Collection<String> results) {
			setTaskFields(results);
		}
	};

	RunnableWithResults<Collection<String>> syncOptionsSelectionAction = new RunnableWithResults<Collection<String>>() {
		public void callback(Collection<String> results) {
			setSyncOptions(results);
		}
	};

	private SettingsPresenter(SettingsModel model, SettingsView view,
			Properties params) {
		super(model, view, params);
	}

	public void update(Subject subject) {
		view.setOptions(getListItems());
	}

	private List<ActionConfiguration> getListItems() {
		ActionConfiguration taskEntryFields = new ActionConfiguration(
				"Task Fields", selectTaskFieldsAction);

		ActionConfiguration syncOptions = new ActionConfiguration(
				"Sync Options", selectSyncOptionsAction);

		String syncAccount = view.getApplicationContextFactory()
				.getDaoFactory().getApplicationDao().getSyncAccount();
		String displayText = (syncAccount == null) ? Constants.ENABLE_SYNC
				: Constants.DISABLE_SYNC;
		Runnable action = (syncAccount == null) ? enableSyncAction
				: disableSyncAction;

		ActionConfiguration enableOrDisableSync = new ActionConfiguration(
				displayText, action);

		return Arrays.asList(taskEntryFields, syncOptions, enableOrDisableSync);
	}

	@Override
	public void instanceInit() {
	}

	@Override
	String getTitle() {
		return TITLE;
	}

	protected void disableSync() {
		ApplicationDao dao = view.getApplicationContextFactory()
				.getDaoFactory().getApplicationDao();
		dao.disableSyncAccount();
		update(model);
	}

	protected void selectTaskFields() {
		List<String> availableTaskFields = new ArrayList<String>();
		List<String> alreadySelectedTaskFields = new ArrayList<String>();

		for (TaskField taskField : TaskField.values()) {
			availableTaskFields.add(taskField.toString());
		}

		ApplicationDao dao = view.getApplicationContextFactory()
				.getDaoFactory().getApplicationDao();
		Integer taskFieldConfiguration = dao.getTaskFieldConfiguration();

		// Be default add all fields
		if (taskFieldConfiguration == null) {
			alreadySelectedTaskFields.addAll(availableTaskFields);
		} else {
			// Otherwise only use the configured ones
			for (TaskField taskField : TaskField.values()) {
				if (taskField.isEnabled(taskFieldConfiguration)) {
					alreadySelectedTaskFields.add(taskField.toString());
				}
			}
		}

		view.getApplicationContextFactory()
				.displayCollectionOfItemsAsCheckBoxes(availableTaskFields,
						alreadySelectedTaskFields, taskFieldsSelectionAction);
	}

	protected void selectSyncOptions() {
		List<String> availableOptions = new ArrayList<String>();
		List<String> alreadySelectedOptions = new ArrayList<String>();

		for (SyncOption option : SyncOption.values()) {
			availableOptions.add(option.toString());
		}

		ApplicationDao dao = view.getApplicationContextFactory()
				.getDaoFactory().getApplicationDao();
		Integer taskFieldConfiguration = dao.getSyncOptionsConfiguration();

		// Be default add all fields
		if (taskFieldConfiguration == null) {
			alreadySelectedOptions.addAll(availableOptions);
		} else {
			// Otherwise only use the configured ones
			for (SyncOption option : SyncOption.values()) {
				if (option.isEnabled(taskFieldConfiguration)) {
					alreadySelectedOptions.add(option.toString());
				}
			}
		}

		view.getApplicationContextFactory()
				.displayCollectionOfItemsAsCheckBoxes(availableOptions,
						alreadySelectedOptions, syncOptionsSelectionAction);
	}

	private void setTaskFields(Collection<String> results) {
		int configuration = 0;
		for (String string : results) {
			TaskField taskField = TaskField.parse(string);
			configuration |= taskField.getBitPattern();
		}

		ApplicationDao dao = view.getApplicationContextFactory()
				.getDaoFactory().getApplicationDao();
		dao.setTaskFieldConfiguration(configuration);
	}

	private void setSyncOptions(Collection<String> results) {
		int configuration = 0;
		for (String string : results) {
			SyncOption taskField = SyncOption.parse(string);
			configuration |= taskField.getBitPattern();
		}

		ApplicationDao dao = view.getApplicationContextFactory()
				.getDaoFactory().getApplicationDao();
		dao.setSyncOptionsConfiguration(configuration);
	}
}
