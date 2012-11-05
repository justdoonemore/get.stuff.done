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
package com.jdom.get.stuff.done.android;

import java.util.Properties;

import com.jdom.get.stuff.done.presenter.AddTaskPresenter;
import com.jdom.get.stuff.done.presenter.AddTaskView;

public class AddTaskActivity extends BaseTaskActivity<AddTaskPresenter>
		implements AddTaskView {

	@Override
	protected AddTaskPresenter setupPresenter(Properties params) {
		return AddTaskPresenter.construct(this, params);
	}

	@Override
	protected int getLayout() {
		return R.layout.add_task;
	}
}
