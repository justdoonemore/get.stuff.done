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

import java.util.List;
import java.util.Set;

import com.jdom.get.stuff.done.domain.ListItemConfiguration;
import com.jdom.util.mvp.api.MenuItemConfiguration;

public interface TasksView extends BaseView {
	void setTasks(List<ListItemConfiguration> tasks, boolean[] completedStatus);

	void setRightClickMenuItems(
			List<MenuItemConfiguration> selectedItemMenuItems);

	Set<String> getSelectedTasks();

	void setFilterOptions(List<String> listOfFilterOptions,
			Runnable changedAction);

	String getSelectedFilter();

	void setSelectedFilterOption(String filter);

	void setSortOptions(List<String> sortOptions, Runnable changedAction);

	String getSelectedSort();

	void setSelectedSortOption(String sort);

	void setMultipleItemsSelectedButtonOptions(
			List<MenuItemConfiguration> multipleItemsSelectedButtonOptions);
}
