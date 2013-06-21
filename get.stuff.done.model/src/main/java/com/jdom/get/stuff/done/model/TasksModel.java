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
package com.jdom.get.stuff.done.model;

import java.util.List;
import java.util.Set;

import com.jdom.get.stuff.done.domain.FilterOption;
import com.jdom.get.stuff.done.domain.SortOption;
import com.jdom.get.stuff.done.domain.Task;
import com.jdom.util.mvp.api.BaseApplicationModel;

public interface TasksModel extends BaseApplicationModel<ContextFactory> {

	Set<Task> getTasks();

	void deleteTask(String taskName);

	Task toggleTaskCompleted(String taskName);

	void setFilterOption(FilterOption filter);

	FilterOption getFilterOption();

	List<FilterOption> getFilterOptions();

	List<SortOption> getSortOptions();

	void setSortOption(SortOption sortOption);

	SortOption getSortOption();
}