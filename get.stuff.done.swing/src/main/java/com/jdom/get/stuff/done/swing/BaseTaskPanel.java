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

import java.awt.Container;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;

import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.util.patterns.mvp.ActionConfiguration;
import com.jdom.util.patterns.mvp.BaseApplicationPresenter;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplication;
import com.jdom.util.patterns.mvp.swing.SwingUtils;

public abstract class BaseTaskPanel<PRESENTER extends BaseApplicationPresenter<ContextFactory, ?, ?>>
		extends BasePanel<PRESENTER> {
	private static final long serialVersionUID = 356207094268105831L;

	private static final String AVAILABLE_DEPENDENCIES_LIST = "availableDependenciesList";

	protected final JTextField taskName = new JTextField(25);

	protected final JLabel taskNameLabel = new JLabel();

	protected final JTextField description = new JTextField(50);

	protected final JLabel descriptionLabel = new JLabel();

	private final JTextField dueDate = new JTextField(10);

	protected final JLabel dueDateLabel = new JLabel();

	protected final JPanel taskAttributesPanel = new JPanel();

	protected final JPanel buttonsPanel = getButtonsPanel();

	protected final JPanel dependenciesPanel = new JPanel();

	protected final JComboBox taskListList = new JComboBox();

	protected final JLabel taskListLabel = new JLabel();

	protected final JTextField tagsField = new JTextField(25);

	protected final JLabel tagsFieldLabel = new JLabel();

	private List<String> availableDependencies;

	private final JButton selectDependenciesButton = new JButton();

	BaseTaskPanel(SwingDashboardApplication main, Properties params) {
		super(main, params);

		taskAttributesPanel.setLayout(new BoxLayout(taskAttributesPanel,
				BoxLayout.Y_AXIS));
		taskAttributesPanel.add(taskNameLabel);
		taskAttributesPanel.add(taskName);
		taskAttributesPanel.add(descriptionLabel);
		taskAttributesPanel.add(description);
		taskAttributesPanel.add(dueDateLabel);
		taskAttributesPanel.add(dueDate);
		taskAttributesPanel.add(taskListLabel);
		taskAttributesPanel.add(taskListList);
		taskAttributesPanel.add(tagsFieldLabel);
		taskAttributesPanel.add(tagsField);
		taskAttributesPanel.add(selectDependenciesButton);
		this.add(taskAttributesPanel);
		this.add(dependenciesPanel);
		this.add(buttonsPanel);
	}

	protected JPanel getButtonsPanel() {
		JPanel buttonsPanel = new JPanel();
		return buttonsPanel;
	}

	public void setButtonOptions(List<ActionConfiguration> buttonOptions) {
		this.buttonsPanel.removeAll();

		for (final ActionConfiguration buttonConfiguration : buttonOptions) {
			JButton button = new JButton(buttonConfiguration.getDisplayText());
			button.addActionListener(SwingUtils
					.getActionListenerForRunnable(buttonConfiguration
							.getClickAction()));
			buttonsPanel.add(button);
		}
	}

	public String getTaskName() {
		return taskName.getText();
	}

	public String getDescription() {
		return description.getText();
	}

	public List<String> getDependencies() {
		return getSelectedListItems(dependenciesPanel,
				AVAILABLE_DEPENDENCIES_LIST);
	}

	private List<String> getSelectedListItems(Container container,
			String listName) {
		Object[] selectedValues = SwingUtils.findChildByName(container,
				JList.class, listName).getSelectedValues();
		List<String> dependencies = new ArrayList<String>();

		if (selectedValues != null && selectedValues.length > 0) {
			for (int i = 0; i < selectedValues.length; i++) {
				dependencies.add(selectedValues[i].toString());
			}
		}

		return dependencies;
	}

	public void setSelectedDependencies(List<String> dependencies) {
		JList list = SwingUtils.findChildByName(dependenciesPanel, JList.class,
				AVAILABLE_DEPENDENCIES_LIST);

		ListSelectionModel selectionModel = list.getSelectionModel();
		selectionModel.clearSelection();

		for (String dependency : dependencies) {
			int indexOf = availableDependencies.indexOf(dependency);
			selectionModel.addSelectionInterval(indexOf, indexOf);
		}
	}

	public void setAvailableDependencies(List<String> availableDependencies) {
		this.availableDependencies = availableDependencies;
		setListContents(dependenciesPanel, availableDependencies,
				AVAILABLE_DEPENDENCIES_LIST);
	}

	protected void setListContents(JPanel panelContainingList,
			List<String> listContents, String nameForListComponent) {
		panelContainingList.removeAll();
		JList newList = new JList(new Vector<String>(listContents));
		newList.setName(nameForListComponent);
		newList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		dependenciesPanel.add(new JScrollPane(newList));
	}

	public void setTaskLists(List<String> taskLists) {
		for (String taskList : taskLists) {
			taskListList.addItem(taskList);
		}
	}

	public String getTaskList() {
		return taskListList.getSelectedItem().toString();
	}

	public void setSelectedTaskList(String taskList) {
		taskListList.setSelectedItem(taskList);
	}

	public String getTags() {
		return tagsField.getText();
	}

	public String getDueDate() {
		return dueDate.getText();
	}

	public void setDueDate(String due) {
		dueDate.setText(due);
	}

	public void setDueDates(List<String> dueDates) {
		// TODO Auto-generated method stub

	}

	public void setNameLabel(String label) {
		this.taskNameLabel.setText(label);
	}

	public void setDescriptionLabel(String label) {
		this.descriptionLabel.setText(label);

	}

	public void setDueDateLabel(String label) {
		this.dueDateLabel.setText(label);
	}

	public void setTaskListLabel(String label) {
		this.taskListLabel.setText(label);
	}

	public void setTagsLabel(String label) {
		this.tagsFieldLabel.setText(label);
	}

	public void setDependencySelectionButton(
			ActionConfiguration actionConfiguration) {
		selectDependenciesButton.setText(actionConfiguration.getDisplayText());
		selectDependenciesButton.addActionListener(SwingUtils
				.getActionListenerForRunnable(actionConfiguration
						.getClickAction()));
	}

	@Override
	protected ContextFactory setupApplicationContextFactory(
			SwingDashboardApplication application) {
		return new SwingApplicationContextFactory(application);
	}

}
