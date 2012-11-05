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

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.domain.ListFilterOption;
import com.jdom.get.stuff.done.domain.TaskList;
import com.jdom.get.stuff.done.model.ListsModel;
import com.jdom.get.stuff.done.model.impl.ListsModelImpl;
import com.jdom.get.stuff.done.presenter.converter.TaskListToListNameConverter;
import com.jdom.util.patterns.mvp.ActionConfiguration;
import com.jdom.util.patterns.mvp.MenuItemConfiguration;
import com.jdom.util.patterns.observer.Subject;

public class ListsPresenter extends BasePresenter<ListsModel, ListsView> {

	private static final TaskListToListNameConverter LIST_TO_LIST_NAME_CONVERTER = new TaskListToListNameConverter();
	static final String TITLE = "Lists";

	public static ListsPresenter construct(ListsView view, Properties params) {
		return construct(new ListsModelImpl(), view, params);
	}

	static ListsPresenter construct(ListsModel model, ListsView view,
			Properties params) {
		ListsPresenter presenter = new ListsPresenter(model, view, params);
		model.addObserver(presenter);
		return presenter;
	}

	final Runnable listSelectedRunnable = new Runnable() {
		public void run() {
			listSelected();
		}
	};
	final Runnable addListRunnable = new Runnable() {
		public void run() {
			addList();
		}
	};
	final Runnable deleteListRunnable = new Runnable() {
		public void run() {
			deleteList();
		}
	};

	private ListsPresenter(ListsModel model, ListsView view, Properties params) {
		super(model, view, params);
	}

	@Override
	public void instanceInit() {
		view.setAddListActionConfiguration(new ActionConfiguration("Add",
				addListRunnable));
		view.setRightClickMenuItems(Arrays.asList(new MenuItemConfiguration(
				"Delete", deleteListRunnable)));
	}

	public void update(Subject subject) {
		view.setListListItems(
				convertListOfListItemsToListOfString(model.getListListItems()),
				listSelectedRunnable);
	}

	private void listSelected() {
		String selectedList = view.getSelectedList();
		Properties params = new Properties();
		params.setProperty(Constants.FILTER_OPTION, new ListFilterOption(
				selectedList).toString());
		view.getApplicationContextFactory().launchView(TasksView.class, params);
	}

	private void addList() {
		model.addList(view.getListName());
		view.setListName("");
	}

	private void deleteList() {
		model.deleteList(view.getSelectedList());
	}

	static List<String> convertListOfListItemsToListOfString(
			Set<TaskList> itemOptions) {
		return LIST_TO_LIST_NAME_CONVERTER.convert(itemOptions);
	}

	@Override
	String getTitle() {
		return TITLE;
	}
}
