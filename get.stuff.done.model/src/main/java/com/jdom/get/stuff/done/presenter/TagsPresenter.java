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

import java.util.Properties;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.TagFilterOption;
import com.jdom.get.stuff.done.model.TagsModel;
import com.jdom.get.stuff.done.model.impl.TagsModelImpl;
import com.jdom.util.patterns.observer.Subject;

public class TagsPresenter extends BasePresenter<TagsModel, TagsView> {

	static final String TITLE = "Tags";

	public static TagsPresenter construct(TagsView view, Properties params) {
		return construct(new TagsModelImpl(), view, params);
	}

	static TagsPresenter construct(TagsModel model, TagsView view,
			Properties params) {
		TagsPresenter presenter = new TagsPresenter(model, view, params);
		model.addObserver(presenter);
		return presenter;
	}

	Runnable selectedTagRunnable = new Runnable() {
		public void run() {
			tagSelected();
		}
	};

	private TagsPresenter(TagsModel model, TagsView view, Properties params) {
		super(model, view, params);
	}

	public void update(Subject subject) {
		view.setTagsListItems(model.getTagsListItems(), selectedTagRunnable);
	}

	@Override
	public void instanceInit() {
	}

	private void tagSelected() {
		String selectedTag = view.getSelectedTag();
		Properties params = new Properties();
		params.setProperty(Constants.FILTER_OPTION, new TagFilterOption(
				selectedTag).toString());
		view.getApplicationContextFactory().launchView(TasksView.class, params);
	}

	@Override
	String getTitle() {
		return TITLE;
	}
}
