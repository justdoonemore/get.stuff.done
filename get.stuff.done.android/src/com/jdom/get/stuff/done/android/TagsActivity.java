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
import java.util.Properties;
import java.util.Set;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jdom.get.stuff.done.presenter.TagsPresenter;
import com.jdom.get.stuff.done.presenter.TagsView;

public class TagsActivity extends BaseActivity<TagsPresenter> implements
		TagsView {
	private String selectedTag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tags);
	}

	@Override
	protected TagsPresenter setupPresenter(Properties params) {
		return TagsPresenter.construct(this, params);
	}

	public void setTagsListItems(Set<String> set, final Runnable runnable) {
		final ArrayList<String> listOfTags = new ArrayList<String>(set);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				BaseActivity.LIST_ITEM_TYPE, listOfTags);

		ListView listView = (ListView) findViewById(R.id.tags);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				selectedTag = listOfTags.get(arg2);
				runnable.run();
			}
		});
		adapter.notifyDataSetChanged();

	}

	public String getSelectedTag() {
		return selectedTag;
	}
}
