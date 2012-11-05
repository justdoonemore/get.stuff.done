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
package com.jdom.get.stuff.done.model.impl;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.model.dao.DaoFactory;
import com.jdom.get.stuff.done.model.dao.MockApplicationDao;
import com.jdom.get.stuff.done.model.impl.ListsModelImpl;
import com.jdom.get.stuff.done.presenter.SyncOption;
import com.jdom.get.stuff.done.sync.SynchronizeStrategy;
import com.jdom.util.patterns.observer.Observer;

public class ListsModelImplTest extends
		BaseApplicationModelImplTest<ListsModelImpl> {

	private final DaoFactory daoFactory = mock(DaoFactory.class);
	private final ApplicationDao taskDao = new MockApplicationDao();
	private final SynchronizeStrategy syncStrategy = mock(SynchronizeStrategy.class);

	@Override
	@Before
	public void setUp() {
		when(contextFactory.getDaoFactory()).thenReturn(daoFactory);
		when(contextFactory.getSyncStrategy()).thenReturn(syncStrategy);
		when(daoFactory.getApplicationDao()).thenReturn(taskDao);
	}

	@Test
	public void testGetListListItemsRetrievesThemFromTheDao() {
		ApplicationDao taskDao = mock(ApplicationDao.class);
		when(daoFactory.getApplicationDao()).thenReturn(taskDao);

		model.getListListItems();

		verify(taskDao, times(1)).getTaskLists();
	}

	@Test
	public void testAddListInvokesDao() {
		final String listName = "newList";
		model.addList(listName);

		Set<TaskList> lists = taskDao.getTaskLists();

		assertTrue("Did not find the list that should have been created!",
				lists.contains(new TaskList(listName)));
	}

	@Test
	public void testAddListUpdatesObservers() {
		Observer observer = mock(Observer.class);
		model.addObserver(observer);
		model.addList("newList");

		verify(observer, times(1)).update(model);
	}

	@Test
	public void testAddListInvokesSyncStrategy() {
		final String listName = "newList";
		model.addList(listName);

		verify(syncStrategy).synchronizeIfEnabled(SyncOption.LOCAL_CHANGE);
	}

	@Test
	public void testDeleteListInvokesDao() {
		final String listName = "newList";
		taskDao.addTaskList(new TaskList(listName));
		model.deleteList(listName);

		Set<TaskList> lists = taskDao.getTaskLists();

		assertTrue("Should have found the list that should have been deleted!",
				lists.contains(new TaskList(listName)));
		// TODO: Add test to make sure it's marked deleted
	}

	@Test
	public void testDeleteListInvokesSyncStrategy() {
		final String listName = "newList";
		ApplicationDao dao = mock(ApplicationDao.class);
		when(daoFactory.getApplicationDao()).thenReturn(dao);

		model.deleteList(listName);

		verify(syncStrategy).synchronizeIfEnabled(SyncOption.LOCAL_CHANGE);
	}

	@Test
	public void testDeleteListCatchingIllegalArgumentExceptionDisplayMessageToUser() {
		model.deleteList(Constants.DEFAULT_LIST);

		verify(contextFactory, times(1)).displayAlert(
				ListsModelImpl.CANT_DELETE_DEFAULT_LIST);
	}

	@Test
	public void testDeleteListUpdatesObservers() {
		Observer observer = mock(Observer.class);
		model.addObserver(observer);
		model.deleteList("newList");

		verify(observer, times(1)).update(model);
	}

	@Override
	protected ListsModelImpl getModel() {
		return new ListsModelImpl();
	}
}
