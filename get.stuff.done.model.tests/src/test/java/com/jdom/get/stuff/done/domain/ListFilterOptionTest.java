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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.jdom.get.stuff.done.domain.ListFilterOption;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.junit.utils.AssertUtil;

public class ListFilterOptionTest {

	@Test
	public void testToStringStatesTheListName() {
		ListFilterOption filterOption = new ListFilterOption("list1");
		assertEquals("Didn't find the correct toString() value!",
				"List Filter: list1", filterOption.toString());
	}

	@Test
	public void testAcceptOnlyAcceptsTasksWithAppropriateList() {
		Task task = new Task("one", "desc", "Inbox");
		Task task2 = new Task("zelast", "desc2", "Miscellaneous");
		Task task3 = new Task("also", "desc3", "Miscellaneous");

		ListFilterOption filterOption = new ListFilterOption("Miscellaneous");

		assertFalse("Should not have accepted the list!",
				filterOption.accept(task));
		assertTrue("Should have accepted the list!", filterOption.accept(task2));
		assertTrue("Should have accepted the list!", filterOption.accept(task3));
	}

	@Test
	public void testEqualsAndHashcodeContract() {
		ListFilterOption objectToTest = new ListFilterOption("test");
		ListFilterOption equal = new ListFilterOption("test");
		ListFilterOption notEqual = new ListFilterOption("test2");

		AssertUtil.assertEqualsAndHashcode(objectToTest, equal, notEqual);
	}
}
