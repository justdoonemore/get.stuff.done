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
package com.jdom.get.stuff.done.presenter;

public enum TaskField {
	DESCRIPTION("Description", 1), LIST("List", 2), DUE_DATE("Due Date", 4), TAGS(
			"Tags", 8);

	private String displayText;
	private int bitPattern;

	private TaskField(String displayText, int bitPattern) {
		this.displayText = displayText;
		this.bitPattern = bitPattern;
	}

	public int getBitPattern() {
		return bitPattern;
	}

	@Override
	public String toString() {
		return displayText;
	}

	public static TaskField parse(String string) {
		for (TaskField taskField : TaskField.values()) {
			if (taskField.toString().equals(string)) {
				return taskField;
			}
		}

		return null;
	}

	public boolean isEnabled(Integer configuration) {
		// if the configuration is null, then by default we consider it to be
		// enabled
		return configuration == null
				|| (bitPattern == (configuration & bitPattern));
	}
}
