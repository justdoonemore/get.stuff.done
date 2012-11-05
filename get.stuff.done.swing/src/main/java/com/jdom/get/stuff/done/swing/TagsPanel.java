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

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.presenter.TagsPresenter;
import com.jdom.get.stuff.done.presenter.TagsView;
import com.jdom.util.patterns.mvp.ActionConfiguration;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplication;
import com.jdom.util.patterns.mvp.swing.SwingUtils;

public class TagsPanel extends BasePanel<TagsPresenter> implements TagsView {
	private static final long serialVersionUID = -8643197284288789483L;

	private final JPanel tagsPanel = new JPanel();

	private final JPanel buttonsPanel = new JPanel();

	public TagsPanel(SwingDashboardApplication main, Properties params) {
		super(main, params);

		this.add(tagsPanel);
		this.add(buttonsPanel);

		getPresenter().init();
	}

	public void setTagsListItems(Set<String> tags, final Runnable runnable) {
		tagsPanel.removeAll();
		JList list = new JList(new Vector<String>(tags));
		list.setName("tags");
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				runnable.run();
			}
		});

		tagsPanel.add(new JScrollPane(list));
	}

	public void setNonListItemActions(
			List<ActionConfiguration> nonListItemActions) {
		for (final ActionConfiguration config : nonListItemActions) {
			JButton button = new JButton(config.getDisplayText());
			button.addActionListener(SwingUtils
					.getActionListenerForRunnable(config.getClickAction()));
			buttonsPanel.add(button);
		}
	}

	public String getSelectedTag() {
		JList list = SwingUtils.findChildByName(tagsPanel, JList.class, "tags");
		return list.getSelectedValue() + "";
	}

	@Override
	protected TagsPresenter setupPresenter(Properties params) {
		return TagsPresenter.construct(this, params);
	}

	@Override
	protected ContextFactory setupApplicationContextFactory(
			SwingDashboardApplication application) {
		return new SwingApplicationContextFactory(application);
	}
}
