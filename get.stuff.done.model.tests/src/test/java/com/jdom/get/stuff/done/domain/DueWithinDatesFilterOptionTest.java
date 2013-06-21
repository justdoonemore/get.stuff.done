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

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.DueWithinDatesFilterOption;
import com.jdom.get.stuff.done.domain.FilterOption;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.junit.utils.AssertUtil;
import com.jdom.util.date.DateUtil;

@Ignore("It appears there are test failures due to daylight savings time?")
public class DueWithinDatesFilterOptionTest {

	@Test
	public void testEqualsAndHashcodeContract() {
		Date earliest = new Date();
		Date latest = new Date(earliest.getTime() + Constants.MILLIS_PER_DAY);
		DueWithinDatesFilterOption objectUnderTest = new DueWithinDatesFilterOption(
				earliest, latest);

		DueWithinDatesFilterOption equals = new DueWithinDatesFilterOption(
				earliest, latest);
		DueWithinDatesFilterOption notEquals1 = new DueWithinDatesFilterOption(
				earliest, earliest);
		DueWithinDatesFilterOption notEquals2 = new DueWithinDatesFilterOption(
				latest, latest);
		DueWithinDatesFilterOption notEquals3 = new DueWithinDatesFilterOption(
				earliest, new Date(latest.getTime() + 1));

		AssertUtil.assertEqualsAndHashcode(objectUnderTest,
				Arrays.asList(equals),
				Arrays.asList(notEquals1, notEquals2, notEquals3));
	}

	@Test
	public void testDueTodayDisplayString() {
		assertEquals("Incorrect display string found!",
				DueWithinDatesFilterOption.DUE_TODAY_DISPLAY_STRING,
				FilterOption.DUE_TODAY.toString());
	}

	@Test
	public void testDueTomorrowDisplayString() {
		assertEquals("Incorrect display string found!",
				DueWithinDatesFilterOption.DUE_TOMORROW_DISPLAY_STRING,
				FilterOption.DUE_TOMORROW.toString());
	}

	@Test
	public void testDueThisWeekDisplayString() {
		assertEquals("Incorrect display string found!",
				DueWithinDatesFilterOption.DUE_THIS_WEEK_DISPLAY_STRING,
				FilterOption.DUE_THIS_WEEK.toString());
	}

	@Test
	public void testDueTodayParsing() {
		Date today = DateUtil.getCurrentDateZeroingHoursAndBelow();

		DueWithinDatesFilterOption filter = DueWithinDatesFilterOption
				.parse(DueWithinDatesFilterOption.DUE_TODAY_DISPLAY_STRING);
		assertEquals("The earliest date was incorrect!", today, filter.earliest);
		assertEquals("The latest date was incorrect!", today, filter.latest);
	}

	@Test
	public void testDueTomorrowParsing() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.getCurrentDateZeroingHoursAndBelow());
		cal.add(Calendar.DATE, 1);
		Date tomorrow = cal.getTime();

		DueWithinDatesFilterOption filter = DueWithinDatesFilterOption
				.parse(DueWithinDatesFilterOption.DUE_TOMORROW_DISPLAY_STRING);
		assertEquals("The earliest date was incorrect!", tomorrow,
				filter.earliest);
		assertEquals("The latest date was incorrect!", tomorrow, filter.latest);
	}

	@Test
	public void testDueThisWeekParsing() {
		Date today = DateUtil.getCurrentDateZeroingHoursAndBelow();

		Calendar cal = Calendar.getInstance();
		cal.setTime(today);
		cal.add(Calendar.DATE, 7);

		Date nextWeek = cal.getTime();

		DueWithinDatesFilterOption filter = DueWithinDatesFilterOption
				.parse(DueWithinDatesFilterOption.DUE_THIS_WEEK_DISPLAY_STRING);
		assertEquals("The earliest date was incorrect!", today, filter.earliest);
		assertEquals("The latest date was incorrect!", nextWeek, filter.latest);
	}

	@Test
	public void testAcceptsTaskWithDueDateSameAsEarliest() {
		Date earliest = DateUtil.getCurrentDateZeroingHoursAndBelow();
		Date latest = new Date(earliest.getTime() + Constants.MILLIS_PER_WEEK);

		DueWithinDatesFilterOption filter = new DueWithinDatesFilterOption(
				earliest, latest);

		Task task = new Task("someTask");
		task.setDueDate(earliest);

		assertTrue("Should have accepted the task!", filter.accept(task));
	}

	@Test
	public void testAcceptsTaskWithDueDateSameAsLatest() {
		Date earliest = DateUtil.getCurrentDateZeroingHoursAndBelow();
		Date latest = new Date(earliest.getTime() + Constants.MILLIS_PER_WEEK);

		DueWithinDatesFilterOption filter = new DueWithinDatesFilterOption(
				earliest, latest);

		Task task = new Task("someTask");
		task.setDueDate(latest);

		assertTrue("Should have accepted the task!", filter.accept(task));
	}

	@Test
	public void testAcceptsTaskWithDueDateInBetweenEarliestAndLatest() {
		Date earliest = DateUtil.getCurrentDateZeroingHoursAndBelow();
		Date latest = new Date(earliest.getTime() + Constants.MILLIS_PER_WEEK);

		DueWithinDatesFilterOption filter = new DueWithinDatesFilterOption(
				earliest, latest);

		Task task = new Task("someTask");
		task.setDueDate(new Date((earliest.getTime() + latest.getTime()) / 2));

		assertTrue("Should have accepted the task!", filter.accept(task));
	}

	@Test
	public void testDoesNotAcceptTaskWithDueDateRightBeforeEarliest() {
		Date earliest = DateUtil.getCurrentDateZeroingHoursAndBelow();
		Date latest = new Date(earliest.getTime() + Constants.MILLIS_PER_WEEK);

		DueWithinDatesFilterOption filter = new DueWithinDatesFilterOption(
				earliest, latest);

		Task task = new Task("someTask");
		task.setDueDate(new Date(earliest.getTime() - 1));

		assertFalse("Should not have accepted the task!", filter.accept(task));
	}

	@Test
	public void testDoesNotAcceptTaskWithDueDateRightAfterLatest() {
		Date earliest = DateUtil.getCurrentDateZeroingHoursAndBelow();
		Date latest = new Date(earliest.getTime() + Constants.MILLIS_PER_WEEK);

		DueWithinDatesFilterOption filter = new DueWithinDatesFilterOption(
				earliest, latest);

		Task task = new Task("someTask");
		task.setDueDate(new Date(latest.getTime() + 1));

		assertFalse("Should not have accepted the task!", filter.accept(task));
	}
}
