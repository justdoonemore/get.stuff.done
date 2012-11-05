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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.ListItemConfiguration;
import com.jdom.get.stuff.done.presenter.TasksPresenter;
import com.jdom.get.stuff.done.presenter.TasksView;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.patterns.mvp.MenuItemConfiguration;

public class TasksActivity extends BaseActivity<TasksPresenter> implements
		TasksView {
	private List<String> sortOptions;
	private List<String> filterOptions;
	private String selectedItem;
	private Set<String> selectedItems;
	private List<MenuItemConfiguration> rightClickMenuItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.manage_tasks);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		for (int i = 0; i < rightClickMenuItems.size(); i++) {
			MenuItemConfiguration menuItemConfiguration = rightClickMenuItems
					.get(i);
			menu.add(Menu.NONE, i, Menu.NONE,
					menuItemConfiguration.getDisplayText());
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		View view = info.targetView;
		if (view instanceof TextView) {
			TextView textView = (TextView) view;
			selectedItem = textView.getText().toString();
			rightClickMenuItems.get(item.getItemId()).getClickAction().run();
			return true;
		}
		selectedItem = null;
		return false;
	}

	@Override
	protected TasksPresenter setupPresenter(Properties params) {
		return TasksPresenter.construct(this, params);
	}

	public void setTasks(List<ListItemConfiguration> tasks,
			final boolean[] completed) {
		selectedItem = null;
		selectedItems = new HashSet<String>();
		final List<String> items = new ArrayList<String>();
		for (ListItemConfiguration item : tasks) {
			items.add(item.getDisplayText());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_checked, items) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View view = super.getView(position, convertView, parent);

				TextView tv = (TextView) view;
				if (completed[position]) {
					tv.setPaintFlags(tv.getPaintFlags()
							| Paint.STRIKE_THRU_TEXT_FLAG);
				} else {
					tv.setPaintFlags(tv.getPaintFlags()
							& ~(Paint.STRIKE_THRU_TEXT_FLAG));
				}

				return view;
			}
		};

		final ListView listView = (ListView) findViewById(R.id.taskList);
		listView.setAdapter(adapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String item = items.get(position);
				if (selectedItems.contains(item)) {
					selectedItems.remove(item);
				} else {
					selectedItems.add(item);
				}
			}
		});
		adapter.notifyDataSetChanged();

		registerForContextMenu(listView);
	}

	public void setRightClickMenuItems(
			List<MenuItemConfiguration> selectedItemMenuItems) {
		this.rightClickMenuItems = selectedItemMenuItems;
	}

	public Set<String> getSelectedTasks() {
		if (selectedItem != null) {
			return CollectionsUtil.asSet(selectedItem);
		} else {
			return selectedItems;
		}
	}

	public void setFilterOptions(List<String> filterOptions,
			final Runnable changedAction) {
		this.filterOptions = filterOptions;
		Spinner spinner = (Spinner) findViewById(R.id.filterOptions);
		spinner.setAdapter(new ArrayAdapter<String>(this, SPINNER_ITEM_TYPE,
				filterOptions));

		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				changedAction.run();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	public String getSelectedFilter() {
		Spinner spinner = (Spinner) findViewById(R.id.filterOptions);
		return spinner.getSelectedItem().toString();
	}

	public void setSelectedFilterOption(String filter) {
		Spinner spinner = (Spinner) findViewById(R.id.filterOptions);
		spinner.setSelection(this.filterOptions.indexOf(filter));
	}

	public void setSortOptions(List<String> sortOptions,
			final Runnable changedAction) {
		this.sortOptions = sortOptions;
		Spinner spinner = (Spinner) findViewById(R.id.sortOptions);
		spinner.setAdapter(new ArrayAdapter<String>(this, SPINNER_ITEM_TYPE,
				sortOptions));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				changedAction.run();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}

	public String getSelectedSort() {
		Spinner spinner = (Spinner) findViewById(R.id.sortOptions);
		return spinner.getSelectedItem().toString();
	}

	public void setSelectedSortOption(String sort) {
		Spinner spinner = (Spinner) findViewById(R.id.sortOptions);
		spinner.setSelection(this.sortOptions.indexOf(sort));
	}

	public void setMultipleItemsSelectedButtonOptions(
			List<MenuItemConfiguration> multipleItemsSelectedButtonOptions) {
		for (final MenuItemConfiguration menuItem : multipleItemsSelectedButtonOptions) {
			int buttonId = Constants.TOGGLE_COMPLETE.equals(menuItem
					.getDisplayText()) ? R.id.toggleCompleteButton
					: R.id.deleteButton;
			Button button = (Button) findViewById(buttonId);
			button.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					menuItem.getClickAction().run();
				}
			});
		}

	}
}
