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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;

import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.model.dao.DaoFactory;
import com.jdom.get.stuff.done.model.impl.TagsModelImpl;

public class TagsModelImplTest extends
		BaseApplicationModelImplTest<TagsModelImpl> {
	@Test
	public void testGetListListItemsRetrievesThemFromTheDao() {
		DaoFactory daoFactory = mock(DaoFactory.class);
		ApplicationDao taskDao = mock(ApplicationDao.class);
		when(contextFactory.getDaoFactory()).thenReturn(daoFactory);
		when(daoFactory.getApplicationDao()).thenReturn(taskDao);

		model.getTagsListItems();

		verify(taskDao, times(1)).getTags();
	}

	@Override
	protected TagsModelImpl getModel() {
		return new TagsModelImpl();
	}
}
