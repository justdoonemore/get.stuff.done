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

import com.jdom.util.time.TimeConstants;

public interface Constants {
	// Whether this application is the pro version
	boolean PRO_VERSION = false;

	String APP_NAME = "Get Stuff Done";
	String PRO = "Pro";
	String FREE = "Free";
	String APP_NAME_PRO = APP_NAME + " Pro";
	String DEFAULT_LIST = "Miscellaneous";
	String HOME_BUTTON_DISPLAY_TEXT = "Home";
	String TASK_FOR_EDIT = "taskForEdit";
	String FILTER_OPTION = "filterOption";
	String ADD_TASK = "Add Task";
	String DUE_TODAY = "Today";
	String DUE_TOMORROW = "Tomorrow";
	String DUE_THIS_WEEK = "This Week";
	String SPECIFIC = "Specific..";
	String ORIGINAL = "Original";

	String TASKS = "Tasks";
	String LISTS = "Lists";
	String TAGS = "Tags";
	String SETTINGS = "Settings";
	long MILLIS_PER_DAY = 86400000L;
	long MILLIS_PER_WEEK = MILLIS_PER_DAY * 7;
	String DUE_DATE_PATTERN = "MM-dd-yyyy";
	String CONFIRM_TITLE = "Confirm";
	Runnable DO_NOTHING_ACTION = new Runnable() {
		public void run() {
		}
	};
	String PARAMETERS = "parameters";
	String API_KEY = "AIzaSyDh4Pbd_AIHMBtJwCBU2CyixQYg3FjZvOI";
	String AUTH_TOKEN_TYPE = "Manage your tasks";
	long SYNC_FREQUENCY = TimeConstants.MILLIS_PER_MINUTE * 10;
	String TOGGLE_COMPLETE = "Toggle Complete";
	String DELETE = "Delete";
	String DISABLE_SYNC = "Disable Sync";
	String ENABLE_SYNC = "Enable Sync";
	String NEVER = "Never";
	String TODAY = "Today";
	String TOMORROW = "Tomorrow";
	String NEXT_WEEK = "Next Week";
	String TOKEN_RETRIEVAL_TIME = "Token_Retrieval_Time";
	// Cache tokens for this amount of time
	long TOKEN_VALID_DURATION = TimeConstants.MILLIS_PER_MINUTE * 10;
	String GOOGLE_DEFAULT_LIST_REMOTE_ID = "@default";
	String GOOGLE_COMPLETED_STRING = "completed";
	String GOOGLE_INCOMPLETE_STRING = "needsAction";
	String GOOGLE_ACCOUNT_PREFIX = "com.google";
	String PRO_VERSION_LINK = "market://details?id=com.jdom.get.stuff.done.android.pro";
}