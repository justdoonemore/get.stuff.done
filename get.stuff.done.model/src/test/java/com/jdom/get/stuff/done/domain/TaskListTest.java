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

import org.junit.Test;

import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.domain.TaskList;

public class TaskListTest {
	@Test
	public void testToStringIsTheName() {
		TaskList TaskList = new TaskList("name");

		assertEquals("The toString() does not appear to be correct!", "name",
				TaskList.toString());
	}

	@Test
	public void testTwoTaskListsWithTheSameNameHaveTheSameHashCode() {
		assertEquals(
				"The two objects with the same name should have the same hashcode!",
				new TaskList("TaskList1").hashCode(),
				new TaskList("TaskList1").hashCode());
	}

	@Test
	public void testTwoTaskListsWithTheSameNameAreEqual() {
		assertEquals(
				"The two objects with the same name should have been equal!",
				new TaskList("TaskList1"), new TaskList("TaskList1"));
	}

	@Test
	public void testTwoTaskListsWithDifferentNameHaveDifferentHashCodes() {
		assertFalse(
				"The two objects with different names should have different hashcodes!",
				new TaskList("TaskList1").hashCode() == new TaskList(
						"TaskList2").hashCode());
	}

	@Test
	public void testTwoTaskListsWithDifferentNameAreNotEqual() {
		assertFalse(
				"The two objects with different names should have different hashcodes!",
				new TaskList("TaskList1").equals(new TaskList("TaskList2")));
	}

	@Test
	public void testTaskListIsNotEqualToAnotherTypeOfObject() {
		assertFalse("TaskList should not be equal to another type of object!",
				new TaskList("name").equals(new Task("name")));
	}
}
