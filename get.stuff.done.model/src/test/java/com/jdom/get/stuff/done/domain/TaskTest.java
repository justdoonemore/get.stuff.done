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

import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;

import com.jdom.get.stuff.done.domain.Task;

public class TaskTest {

	@Test
	public void testToStringIsTheNameAndDescription() {
		Task task = new Task("name", "description");

		assertEquals("The toString() does not appear to be correct!",
				"name::description", task.toString());
	}

	@Test
	public void testTwoTasksWithTheSameNameHaveTheSameHashCode() {
		assertEquals(
				"The two objects with the same name should have the same hashcode!",
				new Task("task1", "description1").hashCode(), new Task("task1",
						"differentdescription").hashCode());
	}

	@Test
	public void testTwoTasksWithTheSameNameAreEqual() {
		assertEquals(
				"The two objects with the same name should have been equal!",
				new Task("task1", "description1"), new Task("task1",
						"differentdescription"));
	}

	@Test
	public void testTwoTasksWithDifferentNameSameDescriptionHaveDifferentHashCodes() {
		assertFalse(
				"The two objects with different names should have different hashcodes!",
				new Task("task1", "description1").hashCode() == new Task(
						"task2", "description1").hashCode());
	}

	@Test
	public void testTwoTasksWithDifferentNameSameDescriptionAreNotEqual() {
		assertFalse(
				"The two objects with different names should have not been equal!",
				new Task("task1", "description1").equals(new Task("task2",
						"description1")));
	}

	@Test
	public void testTaskIsNotEqualToAnotherObjectType() {
		assertFalse("Tasks should never be equal to any other object type!",
				new Task("task1", "description1").equals("task1"));
	}

	@Test
	public void testTaskWithNullDependenciesToConstructorDoesNotThrowAnException() {
		new Task("task1", "desc", (List<Task>) null);
	}

	@Test
	public void testAddedTagIsRetrievable() {
		Task task = new Task("task1", "desc");
		task.addTag("tag");
		assertEquals("Didn't find the tag on the task!", 1, task.getTags()
				.size());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCannotAddToTagsCollectionDirectly() {
		Task task = new Task("task1", "desc");
		task.getTags().add("blah");
	}

	@Test(expected = UnsupportedOperationException.class)
	public void testCannotAddToDependenciesDirectly() {
		Task task = new Task("task1", "desc");
		task.getDependencies().add(new Task("task2"));
	}

	@Test
	public void testThatTagsAreAlwaysLowercased() {
		Task task = new Task("task1");
		task.addTag("ShouldBeLowercase");

		assertEquals("The tag should have been lowercased!",
				"shouldbelowercase", task.getTags().iterator().next());
	}

	@Test
	public void testThatDueDateIsSetToDefaultConstantWhenNotSpecified() {
		Task task = new Task("task1");
		assertEquals(
				"Due date should have been the latest date as possible by default!",
				Task.DEFAULT_DUE_DATE, task.getDueDate());
	}

	@Test
	public void testThatDueDateIsNotDefaultWhenSpecified() {
		Date due = new Date();
		Task task = new Task("task1");
		task.setDueDate(due);

		assertEquals(
				"Due date should have been the latest date as possible by default!",
				due, task.getDueDate());
	}

	@Test
	public void testThatMultipleTagsAreAlwaysLowercased() {
		Task task = new Task("task1");
		task.addTags(Arrays.asList("ShouldBeLowercase", "AlsoShouldBe"));

		Iterator<String> iterator = task.getTags().iterator();
		assertEquals("The tag should have been lowercased!",
				"shouldbelowercase", iterator.next());
		assertEquals("The tag should have been lowercased!", "alsoshouldbe",
				iterator.next());
	}
}
