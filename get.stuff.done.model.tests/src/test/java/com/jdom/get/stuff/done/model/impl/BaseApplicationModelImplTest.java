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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import org.junit.Before;
import org.junit.Ignore;
import org.mockito.Mockito;

import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.model.dao.DaoFactory;
import com.jdom.get.stuff.done.sync.SynchronizeStrategy;
import com.jdom.util.mvp.api.BaseApplicationModelImpl;
import com.jdom.util.mvp.api.MenuItemConfiguration;

@Ignore
public abstract class BaseApplicationModelImplTest<MODEL extends BaseApplicationModelImpl<ContextFactory>> {

	protected final ContextFactory contextFactory = Mockito
			.mock(ContextFactory.class);
	protected final ApplicationDao dao = Mockito.mock(ApplicationDao.class);
	protected final DaoFactory daoFactory = Mockito.mock(DaoFactory.class);
	protected final SynchronizeStrategy syncStrategy = Mockito
			.mock(SynchronizeStrategy.class);

	@Before
	public void setUp() {
		Mockito.when(contextFactory.getDaoFactory()).thenReturn(daoFactory);
		Mockito.when(daoFactory.getApplicationDao()).thenReturn(dao);
		Mockito.when(contextFactory.getSyncStrategy()).thenReturn(syncStrategy);
	}

	protected final MODEL model = getModel();
	{
		model.setApplicationContextFactory(contextFactory);
	}

	public static void assertProperMenuItemConfiguration(
			MenuItemConfiguration menuItemConfiguration,
			String expectedDisplayText, Runnable expectedClickAction) {
		assertEquals("Incorrect display text found for the menu item!",
				expectedDisplayText, menuItemConfiguration.getDisplayText());
		assertSame("Did not find the proper click action!",
				expectedClickAction, menuItemConfiguration.getClickAction());
	}

	protected abstract MODEL getModel();
}
