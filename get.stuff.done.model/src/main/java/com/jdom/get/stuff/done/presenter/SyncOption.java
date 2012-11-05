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

public enum SyncOption {
	STARTUP("Startup", 1), SHUTDOWN("Shutdown", 2), PERIODICALLY(
			"Periodically", 4), LOCAL_CHANGE("Local Change", 8);

	private String displayText;
	private int bitPattern;

	private SyncOption(String displayText, int bitPattern) {
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

	public static SyncOption parse(String string) {
		for (SyncOption syncOption : SyncOption.values()) {
			if (syncOption.toString().equals(string)) {
				return syncOption;
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
