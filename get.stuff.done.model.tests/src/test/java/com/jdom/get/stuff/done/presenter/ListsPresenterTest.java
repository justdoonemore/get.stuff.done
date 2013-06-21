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

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.ListsModel;
import com.jdom.get.stuff.done.presenter.ListsPresenter;
import com.jdom.get.stuff.done.presenter.ListsView;
import com.jdom.get.stuff.done.presenter.TasksView;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.mvp.api.ActionConfiguration;
import com.jdom.util.mvp.api.MenuItemConfiguration;

public class ListsPresenterTest extends
		AbstractPresenterTest<ListsModel, ListsView, ListsPresenter> {

	@Test
	public void testSetsAvailableListsFromModelToViewOnInit() {
		Set<TaskList> itemOptions = CollectionsUtil.asSet(
				new TaskList("Inbox"), new TaskList("All Tasks"));

		when(model.getListListItems()).thenReturn(itemOptions);

		presenter.init();

		verify(view, times(1)).setListListItems(
				Mockito.anyListOf(String.class),
				Mockito.same(presenter.listSelectedRunnable));
	}

	@Test
	public void testListSelectedRetrievesFromViewAndSendsToModel() {
		final String listName = "Inbox";

		when(view.getSelectedList()).thenReturn(listName);

		presenter.listSelectedRunnable.run();

		verify(view, times(1)).getSelectedList();
		verify(contextFactory, times(1)).launchView(
				Matchers.same(TasksView.class), Matchers.any(Properties.class));
	}

	@Test
	public void testListOfListItemsIsConvertedToStringListCorrectly() {
		Set<TaskList> itemOptions = CollectionsUtil.asSet(
				new TaskList("Inbox"), new TaskList("All Tasks"));

		List<String> converted = ListsPresenter
				.convertListOfListItemsToListOfString(itemOptions);

		assertTrue("Didn't find the list title in the converted collection!",
				converted.contains("Inbox"));
		assertTrue("Didn't find the list title in the converted collection!",
				converted.contains("All Tasks"));
	}

	@Test
	public void testSetsAddListActionToViewOnInit() {
		ActionConfiguration addListActionConfiguration = new ActionConfiguration(
				"Add", presenter.addListRunnable);

		presenter.init();

		verify(view, times(1)).setAddListActionConfiguration(
				addListActionConfiguration);
	}

	@Test
	public void testSetsRightClickMenuItemsToViewOnInit() {
		List<MenuItemConfiguration> rightClickMenuItems = Arrays
				.asList(new MenuItemConfiguration("Delete",
						presenter.deleteListRunnable));

		presenter.init();

		verify(view, times(1)).setRightClickMenuItems(rightClickMenuItems);
	}

	@Test
	public void testAddListActionWillRetrieveNameFromView() {
		presenter.addListRunnable.run();

		verify(view, times(1)).getListName();
	}

	@Test
	public void testAddListActionWillSendNameToModel() {
		final String newListName = "newList";
		when(view.getListName()).thenReturn(newListName);

		presenter.addListRunnable.run();

		verify(model, times(1)).addList(newListName);
	}

	@Test
	public void testAddListActionWillSetListNameToEmptyString() {
		final String newListName = "newList";
		when(view.getListName()).thenReturn(newListName);

		presenter.addListRunnable.run();

		verify(view, times(1)).setListName("");
	}

	@Test
	public void testDeleteListActionWillRetrieveNameFromView() {
		presenter.deleteListRunnable.run();

		verify(view, times(1)).getSelectedList();
	}

	@Test
	public void testDeleteListActionWillSendNameToModel() {
		final String name = "list";
		when(view.getSelectedList()).thenReturn(name);

		presenter.deleteListRunnable.run();

		verify(model, times(1)).deleteList(name);
	}

	static void assertProperMenuItemConfiguration(
			ActionConfiguration actionConfiguration,
			String expectedDisplayText, Runnable expectedClickAction) {
		Assert.assertEquals("Incorrect display text found!",
				expectedDisplayText, actionConfiguration.getDisplayText());
		Assert.assertSame("Incorrect click action found!", expectedClickAction,
				actionConfiguration.getClickAction());
	}

	@Override
	protected Class<ListsModel> getModelClass() {
		return ListsModel.class;
	}

	@Override
	protected Class<ListsView> getViewClass() {
		return ListsView.class;
	}

	@Override
	protected ListsPresenter getPresenter(ListsModel model, ListsView view) {
		return ListsPresenter.construct(model, view, new Properties());
	}

}
