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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.jdom.get.stuff.done.presenter.ListsPresenter;
import com.jdom.get.stuff.done.presenter.ListsView;
import com.jdom.util.patterns.mvp.ActionConfiguration;
import com.jdom.util.patterns.mvp.MenuItemConfiguration;

public class ListsActivity extends BaseActivity<ListsPresenter> implements
		ListsView {

	private String selectedItem;
	private List<MenuItemConfiguration> rightClickMenuItems;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.lists);
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
	protected ListsPresenter setupPresenter(Properties params) {
		return ListsPresenter.construct(this, params);
	}

	public void setListListItems(final List<String> listListItems,
			final Runnable listSelectedAction) {
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				BaseActivity.LIST_ITEM_TYPE, listListItems);

		ListView listView = (ListView) findViewById(R.id.listOfLists);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedItem = listListItems.get(arg2);
				listSelectedAction.run();
			}
		});
		adapter.notifyDataSetChanged();

	}

	public String getSelectedList() {
		return selectedItem;
	}

	public void setAddListActionConfiguration(
			ActionConfiguration addListActionConfiguration) {
		Button button = (Button) findViewById(R.id.addButton);
		button.setText(addListActionConfiguration.getDisplayText());
		button.setOnClickListener(getOnClickListenerForRunnable(addListActionConfiguration
				.getClickAction()));
	}

	public String getListName() {
		TextView listText = (TextView) findViewById(R.id.listName);
		return listText.getText().toString();
	}

	public void setListName(String name) {
		TextView listText = (TextView) findViewById(R.id.listName);
		listText.setText(name);
	}

	public void setRightClickMenuItems(
			final List<MenuItemConfiguration> rightClickMenuItems) {
		this.rightClickMenuItems = rightClickMenuItems;
		ListView listView = (ListView) findViewById(R.id.listOfLists);
		listView.setLongClickable(true);
		registerForContextMenu(listView);
	}
}
