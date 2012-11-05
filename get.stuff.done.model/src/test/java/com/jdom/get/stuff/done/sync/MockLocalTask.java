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
package com.jdom.get.stuff.done.sync;

import com.jdom.get.stuff.done.sync.LocalTask;

public class MockLocalTask extends MockLocalSynchableItem implements LocalTask {
	private String listName;

	public MockLocalTask(String name) {
		this(name, null);
	}

	public MockLocalTask(String name, String remoteId) {
		super(name, remoteId);
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getListName() {
		return listName;
	}

	public void checkForSettingDefaultValues() {

	}
}