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

import java.util.List;
import java.util.Properties;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.presenter.DashboardPresenter;
import com.jdom.get.stuff.done.presenter.DashboardView;
import com.jdom.util.patterns.mvp.MenuItemConfiguration;

public class DashboardActivity extends BaseActivity<DashboardPresenter>
		implements DashboardView {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dashboard);
	}

	@Override
	protected DashboardPresenter setupPresenter(Properties params) {
		return DashboardPresenter.construct(this, params);
	}

	public void setDashboardOptions(List<MenuItemConfiguration> menuItems) {
		for (final MenuItemConfiguration menuItem : menuItems) {
			final Runnable runnable = menuItem.getClickAction();
			OnClickListener clickListener = new OnClickListener() {
				public void onClick(View arg0) {
					runnable.run();
				}
			};

			Button button = null;

			String displayText = menuItem.getDisplayText();
			if (Constants.ADD_TASK.equals(displayText)) {
				button = (Button) findViewById(R.id.home_btn_addTask);
			} else if (Constants.DUE_TODAY.equals(displayText)) {
				button = (Button) findViewById(R.id.home_btn_today);
			} else if (Constants.DUE_TOMORROW.equals(displayText)) {
				button = (Button) findViewById(R.id.home_btn_tomorrow);
			} else if (Constants.DUE_THIS_WEEK.equals(displayText)) {
				button = (Button) findViewById(R.id.home_btn_thisWeek);
			} else if (Constants.TASKS.equals(displayText)) {
				button = (Button) findViewById(R.id.home_btn_manageTasks);
			} else if (Constants.LISTS.equals(displayText)) {
				button = (Button) findViewById(R.id.home_btn_lists);
			} else if (Constants.TAGS.equals(displayText)) {
				button = (Button) findViewById(R.id.home_btn_tags);
			} else if (Constants.SETTINGS.equals(displayText)) {
				button = (Button) findViewById(R.id.home_btn_settings);
			}

			button.setText(displayText);
			button.setOnClickListener(clickListener);
		}
	}

	@Override
	public void close() {
		// Does nothing for dashboard
	}
}
