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
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.Properties;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

import com.jdom.get.stuff.done.model.SettingsModel;
import com.jdom.get.stuff.done.presenter.BasePresenter;
import com.jdom.get.stuff.done.presenter.SettingsPresenter;
import com.jdom.get.stuff.done.presenter.SettingsView;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.patterns.mvp.ActionConfiguration;
import com.jdom.util.patterns.mvp.RunnableWithResults;

@RunWith(PowerMockRunner.class)
public class SettingsPresenterTest extends
		AbstractPresenterTest<SettingsModel, SettingsView, SettingsPresenter> {

	@Test
	public void testSetsActionConfigurationsOnInit() {
		when(dao.getSyncAccount()).thenReturn("blah@gmail.com");

		presenter.init();

		verify(view).setOptions(Mockito.anyListOf(ActionConfiguration.class));
	}

	@Test
	public void testEnableSyncLooksUpAccountsFromContextFactoryAndPromptsUserIfAtLeastOneAccountWasFound() {
		Set<String> availableAccounts = CollectionsUtil
				.asSet("blah@google.com");
		when(contextFactory.getAvailableSyncAccounts()).thenReturn(
				availableAccounts);

		presenter.enableSyncAction.run();

		verify(contextFactory, never()).displayAlert(
				SettingsPresenter.NO_VALID_SYNC_ACCOUNTS);
		verify(contextFactory).displayCollectionOfItemsAsRadioButtonGroup(
				BasePresenter.PLEASE_SELECT_SYNC_ACCOUNT, availableAccounts,
				availableAccounts.iterator().next(),
				presenter.selectedSyncAccountRunnable);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testEnableSyncLooksUpAccountsFromContextFactoryAndAlertsUserIfNoValidAccountsWereFound() {
		Set<String> availableAccounts = Collections.emptySet();
		when(contextFactory.getAvailableSyncAccounts()).thenReturn(
				availableAccounts);

		presenter.enableSyncAction.run();

		verify(contextFactory).displayAlert(
				SettingsPresenter.NO_VALID_SYNC_ACCOUNTS);
		verify(contextFactory, never())
				.displayCollectionOfItemsAsRadioButtonGroup(
						same(BasePresenter.PLEASE_SELECT_SYNC_ACCOUNT),
						anySetOf(String.class), anyString(),
						any(RunnableWithResults.class));
	}

	@Test
	public void testEnableSyncAccountInvokesDao() {
		final String account = "blah@google.com";
		presenter.selectedSyncAccountRunnable.callback(account);
		verify(dao).enableSyncAccount(account);
	}

	@Override
	protected Class<SettingsModel> getModelClass() {
		return SettingsModel.class;
	}

	@Override
	protected Class<SettingsView> getViewClass() {
		return SettingsView.class;
	}

	@Override
	protected SettingsPresenter getPresenter(SettingsModel model,
			SettingsView view) {
		return SettingsPresenter.construct(model, view, new Properties());
	}
}
