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
package com.jdom.get.stuff.done.swing;

import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.jdom.get.stuff.done.domain.ListItemConfiguration;
import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.presenter.TasksPresenter;
import com.jdom.get.stuff.done.presenter.TasksView;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.patterns.mvp.MenuItemConfiguration;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplication;
import com.jdom.util.patterns.mvp.swing.SwingUtils;
import com.jdom.util.swing.RightClickPopupMenuListener;

public class TasksPanel extends BasePanel<TasksPresenter> implements TasksView {
	private static final long serialVersionUID = -3992056755897851937L;

	private final JComboBox filters = new JComboBox();
	private final JComboBox sorts = new JComboBox();
	private JScrollPane tasksScrollPane = new JScrollPane(new JList());

	private List<MenuItemConfiguration> rightClickMenuItems;

	public TasksPanel(SwingDashboardApplication main, Properties params) {
		super(main, params);

		this.add(sorts);
		this.add(filters);
		this.add(tasksScrollPane);

		getPresenter().init();
	}

	public void setTasks(List<ListItemConfiguration> tasks, boolean[] completed) {
		tasksScrollPane = setTasks(tasksScrollPane, tasks);
	}

	private JScrollPane setTasks(JScrollPane scrollpane,
			List<ListItemConfiguration> tasks) {

		this.remove(scrollpane);

		Vector<String> displayText = new Vector<String>();
		for (ListItemConfiguration itemConfiguration : tasks) {
			displayText.add(itemConfiguration.getDisplayText());
		}

		final JList newList = new JList(displayText);
		newList.setName("tasks");
		newList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scrollpane = new JScrollPane(newList);

		this.add(scrollpane);

		newList.addMouseListener(new RightClickPopupMenuListener(newList,
				rightClickMenuItems));

		return scrollpane;
	}

	public void setRightClickMenuItems(
			List<MenuItemConfiguration> selectedItemMenuItems) {
		this.rightClickMenuItems = selectedItemMenuItems;
	}

	public String getSelectedTask() {
		return SwingUtils
				.findChildByName(tasksScrollPane, JList.class, "tasks")
				.getSelectedValue().toString();
	}

	public void setFilterOptions(List<String> listOfFilterOptions,
			final Runnable changeAction) {
		filters.removeAllItems();
		filters.addActionListener(SwingUtils
				.getActionListenerForRunnable(changeAction));

		for (String item : listOfFilterOptions) {
			filters.addItem(item);
		}
	}

	public String getSelectedFilter() {
		return filters.getSelectedItem().toString();
	}

	public void setSelectedFilterOption(String filter) {
		filters.setSelectedItem(filter);
	}

	public void setSortOptions(List<String> sortOptions,
			final Runnable changeAction) {
		sorts.removeAllItems();
		sorts.addActionListener(SwingUtils
				.getActionListenerForRunnable(changeAction));

		for (String item : sortOptions) {
			sorts.addItem(item);
		}
	}

	public String getSelectedSort() {
		return sorts.getSelectedItem().toString();
	}

	public void setSelectedSortOption(String sort) {
		sorts.setSelectedItem(sort);
	}

	@Override
	protected TasksPresenter setupPresenter(Properties params) {
		return TasksPresenter.construct(this, params);
	}

	@Override
	protected ContextFactory setupApplicationContextFactory(
			SwingDashboardApplication application) {
		return new SwingApplicationContextFactory(application);
	}

	public Set<String> getSelectedTasks() {
		// TODO Auto-generated method stub
		return CollectionsUtil.asSet(getSelectedTask());
	}

	public void setMultipleItemsSelectedButtonOptions(
			List<MenuItemConfiguration> multipleItemsSelectedButtonOptions) {
		// TODO Auto-generated method stub
	}
}
