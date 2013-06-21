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

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jdom.util.date.DateUtil;

public class DueWithinDatesFilterOption implements FilterOption {
	static final String FILTER_PREFIX = "Due: ";
	static final String THIS_WEEK = "This Week";
	static final String TOMORROW = "Tomorrow";
	static final String TODAY = "Today";
	static final String DUE_TODAY_DISPLAY_STRING = FILTER_PREFIX + TODAY;
	static final String DUE_TOMORROW_DISPLAY_STRING = FILTER_PREFIX + TOMORROW;
	static final String DUE_THIS_WEEK_DISPLAY_STRING = FILTER_PREFIX
			+ THIS_WEEK;
	final Date earliest;
	final Date latest;

	public DueWithinDatesFilterOption(Date earliest, Date latest) {
		this.earliest = earliest;
		this.latest = latest;
	}

	public boolean accept(Task task) {
		Date date = task.getDueDate();

		return (date.equals(earliest) || date.equals(latest) || (date
				.after(earliest) && date.before(latest)));
	}

	@Override
	public String toString() {
		String dateSuffix = null;

		if (latest.equals(earliest)) {
			Date today = DateUtil.getCurrentDateZeroingHoursAndBelow();
			if (today.equals(earliest)) {
				dateSuffix = TODAY;
			} else {
				dateSuffix = TOMORROW;
			}
		} else if (latest.getTime() == (earliest.getTime() + Constants.MILLIS_PER_WEEK)) {
			dateSuffix = THIS_WEEK;
		}

		return FILTER_PREFIX + dateSuffix;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder hcBuilder = new HashCodeBuilder();
		hcBuilder.append(earliest);
		hcBuilder.append(latest);

		return hcBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof DueWithinDatesFilterOption) {
			DueWithinDatesFilterOption other = (DueWithinDatesFilterOption) obj;
			EqualsBuilder eqBuilder = new EqualsBuilder();
			eqBuilder.append(this.earliest, other.earliest);
			eqBuilder.append(this.latest, other.latest);

			return eqBuilder.isEquals();
		}
		return false;
	}

	public static DueWithinDatesFilterOption parse(String string) {
		final String stringWithoutPrefix = string.replaceAll(FILTER_PREFIX, "");
		Date earliest = null;
		Date latest = null;
		if (TODAY.equals(stringWithoutPrefix)) {
			earliest = DateUtil.getCurrentDateZeroingHoursAndBelow();
			latest = (Date) earliest.clone();
		} else if (TOMORROW.equals(stringWithoutPrefix)) {
			earliest = new Date(DateUtil.getCurrentDateZeroingHoursAndBelow()
					.getTime() + Constants.MILLIS_PER_DAY);
			latest = (Date) earliest.clone();
		} else if (THIS_WEEK.equals(stringWithoutPrefix)) {
			earliest = DateUtil.getCurrentDateZeroingHoursAndBelow();
			latest = new Date(DateUtil.getCurrentDateZeroingHoursAndBelow()
					.getTime() + Constants.MILLIS_PER_WEEK);
		}

		return new DueWithinDatesFilterOption(earliest, latest);
	}
}
