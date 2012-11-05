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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.model.DashboardModel;
import com.jdom.get.stuff.done.model.impl.BaseApplicationModelImplTest;
import com.jdom.get.stuff.done.presenter.BasePresenter;
import com.jdom.get.stuff.done.presenter.DashboardPresenter;
import com.jdom.get.stuff.done.presenter.DashboardView;
import com.jdom.get.stuff.done.presenter.SettingsPresenter;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.patterns.mvp.MenuItemConfiguration;
import com.jdom.util.patterns.mvp.RunnableWithResults;

@RunWith(PowerMockRunner.class)
public class DashboardPresenterTest
		extends
		AbstractPresenterTest<DashboardModel, DashboardView, DashboardPresenter> {

	@Override
	protected Class<DashboardModel> getModelClass() {
		return DashboardModel.class;
	}

	@Override
	protected Class<DashboardView> getViewClass() {
		return DashboardView.class;
	}

	@Override
	protected DashboardPresenter getPresenter(DashboardModel model,
			DashboardView view) {
		return DashboardPresenter.construct(model, view, new Properties());
	}

	@Test
	public void testSetsDashboardOptionsToViewOnInit() {
		presenter.init();

		verify(view, times(1)).setDashboardOptions(
				anyListOf(MenuItemConfiguration.class));
	}

	@Test
	public void testDisplaysSyncAccountPromptIfNeverBeenShown() {
		when(dao.isAlreadyDisplayedSyncPrompt()).thenReturn(false);

		Set<String> availableAccounts = CollectionsUtil
				.asSet("blah@google.com");
		when(contextFactory.getAvailableSyncAccounts()).thenReturn(
				availableAccounts);

		presenter.init();

		verify(contextFactory, never()).displayAlert(
				SettingsPresenter.NO_VALID_SYNC_ACCOUNTS);
		verify(contextFactory).displayCollectionOfItemsAsRadioButtonGroup(
				BasePresenter.PLEASE_SELECT_SYNC_ACCOUNT, availableAccounts,
				availableAccounts.iterator().next(),
				presenter.selectedSyncAccountRunnable);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testDoesNotDisplaySyncAccountPromptIfHasBeenShown() {
		when(dao.isAlreadyDisplayedSyncPrompt()).thenReturn(true);

		Set<String> availableAccounts = CollectionsUtil
				.asSet("blah@google.com");
		when(contextFactory.getAvailableSyncAccounts()).thenReturn(
				availableAccounts);

		presenter.init();

		verify(contextFactory, never()).displayAlert(
				SettingsPresenter.NO_VALID_SYNC_ACCOUNTS);
		verify(contextFactory, never())
				.displayCollectionOfItemsAsRadioButtonGroup(anyString(),
						anyCollectionOf(String.class), anyString(),
						any(RunnableWithResults.class));
	}

	@Test
	public void testMenuItemConfigurationsAreConstructedWithCorrectClickActions() {
		List<MenuItemConfiguration> menuOptions = presenter
				.getDashboardOptions();

		BaseApplicationModelImplTest.assertProperMenuItemConfiguration(
				menuOptions.get(0), Constants.ADD_TASK,
				presenter.addTaskClickAction);
		BaseApplicationModelImplTest.assertProperMenuItemConfiguration(
				menuOptions.get(1), Constants.DUE_TODAY,
				presenter.dueTodayClickAction);
		BaseApplicationModelImplTest.assertProperMenuItemConfiguration(
				menuOptions.get(2), Constants.DUE_TOMORROW,
				presenter.dueTomorrowClickAction);
		BaseApplicationModelImplTest.assertProperMenuItemConfiguration(
				menuOptions.get(3), Constants.DUE_THIS_WEEK,
				presenter.dueNextWeekClickAction);
		BaseApplicationModelImplTest
				.assertProperMenuItemConfiguration(menuOptions.get(4),
						Constants.TASKS, presenter.tasksClickAction);
		BaseApplicationModelImplTest
				.assertProperMenuItemConfiguration(menuOptions.get(5),
						Constants.LISTS, presenter.listsClickAction);
		BaseApplicationModelImplTest.assertProperMenuItemConfiguration(
				menuOptions.get(6), Constants.TAGS, presenter.tagsClickAction);
		BaseApplicationModelImplTest.assertProperMenuItemConfiguration(
				menuOptions.get(7), Constants.SETTINGS,
				presenter.settingsClickAction);
	}
}
