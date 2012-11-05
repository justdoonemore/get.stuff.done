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

import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.util.patterns.mvp.BaseApplicationModel;
import com.jdom.util.patterns.mvp.BaseApplicationPresenter;
import com.jdom.util.patterns.mvp.RunnableWithResults;

public abstract class BasePresenter<MODEL extends BaseApplicationModel<ContextFactory>, VIEW extends BaseView>
		extends BaseApplicationPresenter<ContextFactory, MODEL, VIEW> {

	static final String NO_VALID_SYNC_ACCOUNTS = "There are no valid accounts to use for syncing tasks.  The application will be used in local storage only mode.";

	static final String PLEASE_SELECT_SYNC_ACCOUNT = "These accounts are available to sync with Google Tasks:";

	Runnable aboutAction = new Runnable() {
		public void run() {
			view.getApplicationContextFactory().launchView(AboutView.class);
		}
	};

	Runnable homeAction = new Runnable() {
		public void run() {
			view.close();
		}
	};

	Runnable addTaskAction = new Runnable() {
		public void run() {
			RunnableWithResults<String> runnable = new RunnableWithResults<String>() {
				public void callback(String result) {
					if (!StringUtils.isEmpty(result)) {
						Task task = new Task(result);
						ContextFactory contextFactory = view
								.getApplicationContextFactory();
						contextFactory.getDaoFactory().getApplicationDao()
								.addTask(task);
						contextFactory.getSyncStrategy().synchronizeIfEnabled(
								SyncOption.LOCAL_CHANGE);
						update(model);
					}
				}
			};
			view.getApplicationContextFactory().getTextInputForAction(
					"Add Task", "Name", "Add", "Cancel", runnable);
		}
	};

	Runnable enableSyncAction = new Runnable() {
		public void run() {
			enableSync();
		}
	};

	final RunnableWithResults<String> selectedSyncAccountRunnable = new RunnableWithResults<String>() {
		public void callback(String result) {
			enableSyncAccount(result);
		}
	};

	BasePresenter(MODEL model, VIEW view, Properties params) {
		super(model, view, params);
	}

	@Override
	public final void init() {
		view.setTitleBarTitle(getTitle());
		view.setTitleBarHomeAction(homeAction);
		view.setTitleBarAboutAction(aboutAction);
		view.setTitleBarAddTaskAction(addTaskAction);

		view.getApplicationContextFactory().getSyncStrategy()
				.synchronizeIfScheduledTimeHasElapsed();

		instanceInit();

		update(model);
	}

	protected void enableSync() {
		ContextFactory contextFactory = view.getApplicationContextFactory();
		Set<String> availableSyncAccounts = contextFactory
				.getAvailableSyncAccounts();

		if (availableSyncAccounts.isEmpty()) {
			contextFactory.displayAlert(NO_VALID_SYNC_ACCOUNTS);
			return;
		}

		String initialSelection = availableSyncAccounts.iterator().next();
		contextFactory.displayCollectionOfItemsAsRadioButtonGroup(
				PLEASE_SELECT_SYNC_ACCOUNT, availableSyncAccounts,
				initialSelection, selectedSyncAccountRunnable);
	}

	private void enableSyncAccount(String result) {
		ApplicationDao dao = view.getApplicationContextFactory()
				.getDaoFactory().getApplicationDao();
		dao.enableSyncAccount(result);
		update(model);
	}

	public void displayProVersionPopup() {
		ContextFactory contextFactory = view.getApplicationContextFactory();
		contextFactory.displayProVersionPromo(Constants.APP_NAME_PRO,
				Constants.PRO_VERSION_LINK);
	}

	public static boolean isProVersion() {
		return Constants.PRO_VERSION;
	}

	public static String getAppName() {
		return Constants.APP_NAME;
	}

	public static String getEdition() {
		return Constants.PRO_VERSION ? Constants.PRO : Constants.FREE;
	}

	abstract String getTitle();

	abstract void instanceInit();
}
