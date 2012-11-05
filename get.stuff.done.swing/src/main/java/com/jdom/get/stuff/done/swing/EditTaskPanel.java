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

import java.util.Properties;

import javax.swing.JCheckBox;
import javax.swing.JLabel;

import com.jdom.get.stuff.done.presenter.EditTaskPresenter;
import com.jdom.get.stuff.done.presenter.EditTaskView;
import com.jdom.util.patterns.mvp.swing.SwingDashboardApplication;

public class EditTaskPanel extends BaseTaskPanel<EditTaskPresenter> implements
		EditTaskView {

	private static final long serialVersionUID = -2213713615614757572L;

	private final JCheckBox completedCheckbox = new JCheckBox();

	private final JLabel completedLabel = new JLabel();

	public EditTaskPanel(SwingDashboardApplication main, Properties params) {
		super(main, params);
		taskAttributesPanel.add(completedLabel);
		taskAttributesPanel.add(completedCheckbox);

		getPresenter().init();
	}

	public void setTaskName(String name) {
		this.taskName.setText(name);
	}

	public void setTaskDescription(String description) {
		this.description.setText(description);
	}

	public void setTaskCompleted(boolean completed) {
		completedCheckbox.setSelected(completed);

	}

	public boolean getCompleted() {
		return completedCheckbox.isSelected();
	}

	public void setTags(String tagsLine) {
		this.tagsField.setText(tagsLine);
	}

	public void setCompletedLabel(String label) {
		completedLabel.setText(label);
	}

	@Override
	protected EditTaskPresenter setupPresenter(Properties params) {
		return EditTaskPresenter.construct(this, params);
	}

	public void setDueDateText(String dueDate) {
		// TODO Auto-generated method stub

	}

	public void setOriginalDueDate(String dueDateString) {
		// TODO Auto-generated method stub

	}
}
