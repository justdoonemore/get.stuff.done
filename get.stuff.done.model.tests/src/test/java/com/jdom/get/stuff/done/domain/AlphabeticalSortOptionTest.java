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

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

import com.jdom.get.stuff.done.domain.AlphabeticalSortOption;
import com.jdom.get.stuff.done.domain.Task;

public class AlphabeticalSortOptionTest {

	@Test
	public void testReverseAlphabeticalWillSortByReverseName() {
		Task task = new Task("one");
		Task task2 = new Task("two");

		Set<Task> tasks = new TreeSet<Task>(
				AlphabeticalSortOption.REVERSE_ALPHABETICAL);
		tasks.add(task);
		tasks.add(task2);

		Iterator<Task> iterator = tasks.iterator();
		assertEquals(
				"Didn't find the tasks in the reverse alphabetical order by name!",
				task2, iterator.next());
		assertEquals(
				"Didn't find the tasks in the reverse alphabetical order by name!",
				task, iterator.next());
	}

	@Test
	public void testAlphabeticalWillSortByName() {
		Task task = new Task("one");
		Task task2 = new Task("two");

		Set<Task> tasks = new TreeSet<Task>(AlphabeticalSortOption.ALPHABETICAL);
		tasks.add(task);
		tasks.add(task2);

		Iterator<Task> iterator = tasks.iterator();
		assertEquals("Didn't find the tasks in alphabetical order by name!",
				task, iterator.next());
		assertEquals("Didn't find the tasks in alphabetical order by name!",
				task2, iterator.next());
	}

	@Test
	public void testReturnsItselfForComparator() {
		assertSame("Should have returned itself as the comparator!",
				AlphabeticalSortOption.ALPHABETICAL,
				AlphabeticalSortOption.ALPHABETICAL.getComparator());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testParseThrowsExceptionForUnknownStringValue() {
		AlphabeticalSortOption.parse("unknown");
	}

	@Test
	public void testValueOfReturnsValue() {
		assertEquals(AlphabeticalSortOption.ALPHABETICAL,
				AlphabeticalSortOption.valueOf("ALPHABETICAL"));
	}
}
