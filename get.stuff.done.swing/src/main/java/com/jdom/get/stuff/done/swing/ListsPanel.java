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
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.presenter.ListsPresenter;
import com.jdom.get.stuff.done.presenter.ListsView;
import com.jdom.util.patterns.mvp.ActionConfiguration;
import com.jdom.util.patterns.mvp.MenuItemConfiguration;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplication;
import com.jdom.util.patterns.mvp.swing.SwingUtils;
import com.jdom.util.swing.LeftClickActionRunner;
import com.jdom.util.swing.RightClickPopupMenuListener;

public class ListsPanel extends BasePanel<ListsPresenter> implements ListsView {
	private static final long serialVersionUID = -8248339878623647827L;

	private final JPanel listsPanel = new JPanel();

	private final JPanel addListPanel = new JPanel();

	private final JTextField listName = new JTextField(25);

	private final JButton addListButton = new JButton();

	private List<MenuItemConfiguration> rightClickMenuItems;

	public ListsPanel(SwingDashboardApplication main, Properties params) {
		super(main, params);

		addListPanel.add(listName);
		addListPanel.add(addListButton);
		this.add(listsPanel);
		this.add(addListPanel);

		getPresenter().init();
	}

	public void setListListItems(final List<String> listListItems,
			final Runnable listSelectedAction) {
		listsPanel.removeAll();
		final JList list = new JList(new Vector<String>(listListItems));
		list.setName("lists");
		listsPanel.add(new JScrollPane(list));
		list.addMouseListener(new LeftClickActionRunner(list,
				listSelectedAction));
		list.addMouseListener(new RightClickPopupMenuListener(list,
				rightClickMenuItems));
	}

	public String getSelectedList() {
		JList list = SwingUtils.findChildByName(listsPanel, JList.class,
				"lists");
		return list.getSelectedValue() + "";
	}

	public void setAddListActionConfiguration(
			final ActionConfiguration addListActionConfiguration) {
		this.addListButton.setText(addListActionConfiguration.getDisplayText());
		this.addListButton.addActionListener(SwingUtils
				.getActionListenerForRunnable(addListActionConfiguration
						.getClickAction()));
	}

	public String getListName() {
		return listName.getText();
	}

	public void setListName(String name) {
		listName.setText(name);
	}

	public void setRightClickMenuItems(
			List<MenuItemConfiguration> rightClickMenuItems) {
		this.rightClickMenuItems = rightClickMenuItems;
	}

	@Override
	protected ListsPresenter setupPresenter(Properties params) {
		return ListsPresenter.construct(this, params);
	}

	@Override
	protected ContextFactory setupApplicationContextFactory(
			SwingDashboardApplication application) {
		return new SwingApplicationContextFactory(application);
	}
}
