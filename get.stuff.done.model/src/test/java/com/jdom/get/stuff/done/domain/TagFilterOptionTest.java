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

import com.jdom.get.stuff.done.domain.TagFilterOption;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.junit.utils.AssertUtil;

public class TagFilterOptionTest {

	@Test
	public void testToStringStatesTheTagName() {
		TagFilterOption filterOption = new TagFilterOption("tag1");
		assertEquals("Didn't find the correct toString() value!",
				"Tag Filter: tag1", filterOption.toString());
	}

	@Test
	public void testOnlyAcceptsTasksWithTheTag() {
		Task task = new Task("one");
		task.addTag("tag1");
		Task task2 = new Task("task2");
		Task task3 = new Task("zelast");
		task3.addTag("tag1");

		TagFilterOption filterOption = new TagFilterOption("tag1");
		assertTrue("Should have accepted a task with the tag!",
				filterOption.accept(task));
		assertTrue("Should have accepted a task with the tag!",
				filterOption.accept(task3));
		assertFalse("Should not have accepted a task without the tag!",
				filterOption.accept(task2));
	}

	@Test
	public void testEqualsAndHashcodeContract() {
		TagFilterOption objectToTest = new TagFilterOption("test");
		TagFilterOption equal = new TagFilterOption("test");
		TagFilterOption notEqual = new TagFilterOption("test2");

		AssertUtil.assertEqualsAndHashcode(objectToTest, equal, notEqual);
	}
}
