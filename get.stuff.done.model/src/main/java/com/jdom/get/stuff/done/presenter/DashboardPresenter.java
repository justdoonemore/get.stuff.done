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
import java.util.Date;
import java.util.List;
import java.util.Properties;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.DueWithinDatesFilterOption;
import com.jdom.get.stuff.done.model.DashboardModel;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.model.impl.DashboardModelImpl;
import com.jdom.util.date.DateUtil;
import com.jdom.util.mvp.api.MenuItemConfiguration;
import com.jdom.util.mvp.api.RunnableWithResults;
import com.jdom.util.mvp.api.Subject;

public class DashboardPresenter extends
		BasePresenter<DashboardModel, DashboardView> {
	private final Date today = DateUtil.getCurrentDateZeroingHoursAndBelow();
	private final Date tomorrow = new Date(today.getTime()
			+ Constants.MILLIS_PER_DAY);
	private final Date nextWeek = new Date(today.getTime()
			+ Constants.MILLIS_PER_WEEK);
	final Runnable dueTodayClickAction = getDueWithinDatesFilteredRunnable(
			today, today);
	final Runnable dueTomorrowClickAction = getDueWithinDatesFilteredRunnable(
			tomorrow, tomorrow);
	final Runnable dueNextWeekClickAction = getDueWithinDatesFilteredRunnable(
			today, nextWeek);
	final Runnable addTaskClickAction = new Runnable() {
		public void run() {
			view.getApplicationContextFactory().launchView(AddTaskView.class);
		}
	};

	final Runnable tasksClickAction = new Runnable() {
		public void run() {
			view.getApplicationContextFactory().launchView(TasksView.class);
		}
	};
	final Runnable listsClickAction = new Runnable() {
		public void run() {
			view.getApplicationContextFactory().launchView(ListsView.class);
		}
	};
	final Runnable tagsClickAction = new Runnable() {
		public void run() {
			view.getApplicationContextFactory().launchView(TagsView.class);
		}
	};
	final Runnable settingsClickAction = new Runnable() {
		public void run() {
			view.getApplicationContextFactory().launchView(SettingsView.class);
		}
	};
	final RunnableWithResults<String> syncAccountRunnable = new RunnableWithResults<String>() {
		public void callback(String result) {
			view.getApplicationContextFactory().getDaoFactory()
					.getApplicationDao().enableSyncAccount(result);
		}
	};

	public static DashboardPresenter construct(DashboardView view,
			Properties params) {
		return construct(new DashboardModelImpl(), view, params);
	}

	static DashboardPresenter construct(DashboardModel model,
			DashboardView view, Properties params) {
		DashboardPresenter presenter = new DashboardPresenter(model, view,
				params);
		model.addObserver(presenter);

		return presenter;
	}

	private DashboardPresenter(DashboardModel model, DashboardView view,
			Properties params) {
		super(model, view, params);
	}

	@Override
	public void instanceInit() {

	}

	public void update(Subject subject) {
		view.setDashboardOptions(getDashboardOptions());

		ApplicationDao dao = view.getApplicationContextFactory()
				.getDaoFactory().getApplicationDao();
		if (!dao.isAlreadyDisplayedSyncPrompt()) {
			enableSyncAction.run();
			dao.setAlreadyDisplayedSyncPrompt(true);
		}
	}

	public List<MenuItemConfiguration> getDashboardOptions() {
		List<MenuItemConfiguration> menuItemConfigurations = new ArrayList<MenuItemConfiguration>();

		menuItemConfigurations.add(new MenuItemConfiguration(
				Constants.ADD_TASK, addTaskClickAction));
		menuItemConfigurations.add(new MenuItemConfiguration(
				Constants.DUE_TODAY, dueTodayClickAction));
		menuItemConfigurations.add(new MenuItemConfiguration(
				Constants.DUE_TOMORROW, dueTomorrowClickAction));
		menuItemConfigurations.add(new MenuItemConfiguration(
				Constants.DUE_THIS_WEEK, dueNextWeekClickAction));
		menuItemConfigurations.add(new MenuItemConfiguration(Constants.TASKS,
				tasksClickAction));
		menuItemConfigurations.add(new MenuItemConfiguration(Constants.LISTS,
				listsClickAction));
		menuItemConfigurations.add(new MenuItemConfiguration(Constants.TAGS,
				tagsClickAction));
		menuItemConfigurations.add(new MenuItemConfiguration(
				Constants.SETTINGS, settingsClickAction));

		return menuItemConfigurations;
	}

	private Runnable getDueWithinDatesFilteredRunnable(final Date earliest,
			final Date latest) {
		return new Runnable() {
			public void run() {
				Properties params = new Properties();
				String filterString = new DueWithinDatesFilterOption(earliest,
						latest).toString();
				params.setProperty(Constants.FILTER_OPTION, filterString);

				view.getApplicationContextFactory().launchView(TasksView.class,
						params);
			}
		};
	}

	@Override
	String getTitle() {
		return Constants.APP_NAME;
	}
}
