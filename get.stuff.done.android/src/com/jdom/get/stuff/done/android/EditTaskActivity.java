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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.presenter.EditTaskPresenter;
import com.jdom.get.stuff.done.presenter.EditTaskView;

public class EditTaskActivity extends BaseTaskActivity<EditTaskPresenter>
		implements EditTaskView {

	private String originalDueDate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CheckBox completedBox = (CheckBox) findViewById(R.id.completed);
		completedBox.setVisibility(View.VISIBLE);
		TextView completedLabel = (TextView) findViewById(R.id.completedLabel);
		completedLabel.setVisibility(View.VISIBLE);
	}

	@Override
	protected int getLayout() {
		return R.layout.edit_task;
	}

	public void setTaskName(String name) {
		TextView textView = (TextView) findViewById(R.id.addTaskName);
		textView.setText(name);
	}

	public void setTaskDescription(String description) {
		TextView textView = (TextView) findViewById(R.id.addTaskDescription);
		textView.setText(description);
	}

	public void setTaskCompleted(boolean completed) {
		CheckBox completedBox = (CheckBox) findViewById(R.id.completed);
		completedBox.setChecked(completed);
	}

	public boolean getCompleted() {
		CheckBox completedBox = (CheckBox) findViewById(R.id.completed);
		return completedBox.isChecked();
	}

	public void setTags(String tags) {
		TextView textView = (TextView) findViewById(R.id.tags);
		textView.setText(tags);
	}

	public void setCompletedLabel(String label) {
		TextView completedLabel = (TextView) findViewById(R.id.completedLabel);
		completedLabel.setText(label);
	}

	@Override
	protected EditTaskPresenter setupPresenter(Properties params) {
		return EditTaskPresenter.construct(this, params);
	}

	public void setOriginalDueDate(String dueDateString) {
		this.originalDueDate = dueDateString;
		setDueDateText(dueDateString);
		final Spinner spinner = (Spinner) findViewById(R.id.dueDateDropdown);
		spinner.setSelection(dueDateOptions.indexOf(Constants.ORIGINAL));
	}

	@Override
	public void setDueDates(List<String> dueDates) {
		this.dueDateOptions = dueDates;
		final Spinner spinner = (Spinner) findViewById(R.id.dueDateDropdown);
		spinner.setAdapter(new ArrayAdapter<String>(this, SPINNER_ITEM_TYPE,
				dueDateOptions));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == 0) {
					setDueDateText(originalDueDate);
					dueDate = originalDueDate;
				} else if (arg2 == (dueDateOptions.size() - 1)) {
					showDialog(DATE_DIALOG_ID);
				} else {
					getPresenter().dueDateSelection(
							spinner.getSelectedItem().toString());
				}
			};

			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
}
