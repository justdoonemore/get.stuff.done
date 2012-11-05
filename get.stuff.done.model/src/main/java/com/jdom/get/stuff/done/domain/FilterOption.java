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

import com.jdom.util.date.DateUtil;

public interface FilterOption {
	FilterOption ACCEPT_ALL = new FilterOption() {
		public boolean accept(Task task) {
			return true;
		}

		@Override
		public String toString() {
			return "All";
		}
	};

	FilterOption DUE_TODAY = new DueWithinDatesFilterOption(
			DateUtil.getCurrentDateZeroingHoursAndBelow(),
			DateUtil.getCurrentDateZeroingHoursAndBelow());

	FilterOption DUE_TOMORROW = new DueWithinDatesFilterOption(new Date(
			DateUtil.getCurrentDateZeroingHoursAndBelow().getTime()
					+ Constants.MILLIS_PER_DAY), new Date(DateUtil
			.getCurrentDateZeroingHoursAndBelow().getTime()
			+ Constants.MILLIS_PER_DAY));

	FilterOption DUE_THIS_WEEK = new DueWithinDatesFilterOption(
			DateUtil.getCurrentDateZeroingHoursAndBelow(), new Date(DateUtil
					.getCurrentDateZeroingHoursAndBelow().getTime()
					+ Constants.MILLIS_PER_WEEK));

	boolean accept(Task task);
}