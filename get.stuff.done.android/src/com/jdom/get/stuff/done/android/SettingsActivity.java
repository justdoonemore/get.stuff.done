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
import java.util.List;
import java.util.Properties;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jdom.get.stuff.done.presenter.SettingsPresenter;
import com.jdom.get.stuff.done.presenter.SettingsView;
import com.jdom.util.mvp.api.ActionConfiguration;

public class SettingsActivity extends BaseActivity<SettingsPresenter> implements
		SettingsView {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
	}

	@Override
	protected SettingsPresenter setupPresenter(Properties params) {
		return SettingsPresenter.construct(this, params);
	}

	public void setOptions(final List<ActionConfiguration> listItems) {
		List<String> stringItems = new ArrayList<String>();
		for (ActionConfiguration listItem : listItems) {
			stringItems.add(listItem.getDisplayText());
		}

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				BaseActivity.LIST_ITEM_TYPE, stringItems);

		ListView listView = (ListView) findViewById(R.id.options);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				listItems.get(arg2).getClickAction().run();
			}
		});
		adapter.notifyDataSetChanged();
	}
}
