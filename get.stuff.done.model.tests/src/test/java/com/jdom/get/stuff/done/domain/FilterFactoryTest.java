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
package com.jdom.get.stuff.done.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

import java.util.Date;

import org.junit.Test;

import com.jdom.get.stuff.done.domain.DueWithinDatesFilterOption;
import com.jdom.get.stuff.done.domain.FilterFactory;
import com.jdom.get.stuff.done.domain.FilterOption;
import com.jdom.get.stuff.done.domain.ListFilterOption;
import com.jdom.get.stuff.done.domain.TagFilterOption;
import com.jdom.junit.utils.AssertUtil;
import com.jdom.util.date.DateUtil;

public class FilterFactoryTest {

	@Test
	public void testPrivateConstructor() {
		AssertUtil.assertProperPrivateConstructor(FilterFactory.class);
	}

	@Test
	public void testCanCreateFilterForAcceptAll() {
		assertSame("The filter doesn't appear to be the proper value!",
				FilterOption.ACCEPT_ALL,
				FilterFactory.create(FilterOption.ACCEPT_ALL.toString()));
	}

	@Test
	public void testCanCreateTagFilter() {
		TagFilterOption expected = new TagFilterOption("tag1");

		assertEquals("The filter doesn't appear to be the proper value!",
				expected, FilterFactory.create("Tag Filter: tag1"));
	}

	@Test
	public void testCanCreateListFilter() {
		ListFilterOption expected = new ListFilterOption("Miscellaneous");

		assertEquals("The filter doesn't appear to be the proper value!",
				expected, FilterFactory.create("List Filter: Miscellaneous"));
	}

	@Test
	public void testCanCreateDueDateFilter() {
		Date today = DateUtil.getCurrentDateZeroingHoursAndBelow();
		DueWithinDatesFilterOption expected = new DueWithinDatesFilterOption(
				today, today);

		assertEquals("The filter doesn't appear to be the proper value!",
				expected, FilterFactory.create("Due: Today"));
	}
}
