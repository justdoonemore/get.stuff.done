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
package com.jdom.util.date;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static final DateFormat MMDDYYYYFormat = new SimpleDateFormat(
			"MM/dd/yyyy");

	public static final DateFormat MMDDFormat = new SimpleDateFormat("MM/dd");

	private DateUtil() {
		// Private constructor
	}

	/**
	 * Converts a date object into MM/dd/YYYY.
	 * 
	 * @param date
	 *            the date
	 * @return
	 */
	public static String convertDateToMMDDYYYY(Date date) {
		return MMDDYYYYFormat.format(date);
	}

	/**
	 * Converts a string object in the form MM/dd/YYYY into a date object.
	 * 
	 * @param date
	 *            the string date
	 * @return the date object
	 * @throws ParseException
	 */
	public static Date convertMMDDYYYYToDate(String date) throws ParseException {
		return MMDDYYYYFormat.parse(date);
	}

	public static String convertDateToMMDD(Date date) {
		return MMDDFormat.format(date);
	}

	public static Date getCurrentDateZeroingHoursAndBelow() {
		return getDateZeroingHoursAndBelow(new Date());
	}

	public static Date getDateZeroingHoursAndBelow(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		Date today = cal.getTime();
		return today;
	}
}
