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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.presenter.BaseTaskPresenter;
import com.jdom.get.stuff.done.presenter.TaskField;
import com.jdom.util.patterns.mvp.ActionConfiguration;

public abstract class BaseTaskActivity<PRESENTER extends BaseTaskPresenter<?, ?>>
		extends BaseActivity<PRESENTER> {

	static final int DATE_DIALOG_ID = 0;
	private List<String> taskLists;
	protected List<String> dueDateOptions;
	protected String dueDate;

	// the callback received when the user "sets" the date in the dialog
	private final DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			Calendar cal = Calendar.getInstance();
			cal.set(year, monthOfYear, dayOfMonth);
			dueDate = new SimpleDateFormat(Constants.DUE_DATE_PATTERN)
					.format(cal.getTime());
			setDueDateText(dueDate);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(getLayout());

		filterOutUnusedTaskFields();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			Calendar cal = Calendar.getInstance();
			return new DatePickerDialog(this, mDateSetListener,
					cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
					cal.get(Calendar.DAY_OF_MONTH));
		}
		return null;
	}

	protected abstract int getLayout();

	public void setButtonOptions(List<ActionConfiguration> buttonOptions) {
		for (ActionConfiguration configuration : buttonOptions) {
			String displayText = configuration.getDisplayText();
			Button button = (Button) findViewById(R.id.okButton);
			if ("Cancel".equals(displayText)) {
				button = (Button) findViewById(R.id.cancelButton);
			}

			button.setText(displayText);
			button.setOnClickListener(getOnClickListenerForRunnable(configuration
					.getClickAction()));
		}
	}

	public String getTaskName() {
		TextView textView = (TextView) findViewById(R.id.addTaskName);
		return textView.getText().toString();
	}

	public String getDescription() {
		TextView textView = (TextView) findViewById(R.id.addTaskDescription);
		return textView.getText().toString();
	}

	public void setTaskLists(List<String> taskLists) {
		this.taskLists = taskLists;
		Spinner spinner = (Spinner) findViewById(R.id.listsDropdown);
		spinner.setAdapter(new ArrayAdapter<String>(this, SPINNER_ITEM_TYPE,
				taskLists));
	}

	public String getTaskList() {
		Spinner spinner = (Spinner) findViewById(R.id.listsDropdown);
		return spinner.getSelectedItem().toString();
	}

	public void setSelectedTaskList(String taskList) {
		Spinner spinner = (Spinner) findViewById(R.id.listsDropdown);
		spinner.setSelection(this.taskLists.indexOf(taskList));
	}

	public String getTags() {
		TextView textView = (TextView) findViewById(R.id.tags);
		return textView.getText().toString();
	}

	public String getDueDate() {
		TextView dueDateAsString = (TextView) findViewById(R.id.dueDateText);
		return dueDateAsString.getText().toString();
	}

	public void setDueDate(String dueDate) {
		Spinner spinner = (Spinner) findViewById(R.id.dueDateDropdown);
		spinner.setSelection(dueDateOptions.indexOf(dueDate));
	}

	public void setDependencySelectionButton(
			ActionConfiguration actionConfiguration) {
		Button button = (Button) findViewById(R.id.selectDependencies);
		button.setText(actionConfiguration.getDisplayText());
		button.setOnClickListener(getOnClickListenerForRunnable(actionConfiguration
				.getClickAction()));
	}

	public void setDueDates(List<String> dueDates) {
		this.dueDateOptions = dueDates;
		final Spinner spinner = (Spinner) findViewById(R.id.dueDateDropdown);
		spinner.setAdapter(new ArrayAdapter<String>(this, SPINNER_ITEM_TYPE,
				dueDateOptions));
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if (arg2 == (dueDateOptions.size() - 1)) {
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

	public void setDueDateText(String dueDate) {
		TextView dueDateAsString = (TextView) findViewById(R.id.dueDateText);
		dueDateAsString.setText(dueDate);
	}

	private void filterOutUnusedTaskFields() {
		ApplicationDao dao = getApplicationContextFactory().getDaoFactory()
				.getApplicationDao();
		Integer taskFieldConfiguration = dao.getTaskFieldConfiguration();

		if (taskFieldConfiguration != null) {
			for (TaskField taskField : TaskField.values()) {
				if (!taskField.isEnabled(taskFieldConfiguration)) {
					List<View> views = new ArrayList<View>();
					switch (taskField) {
					case DESCRIPTION:
						views.add(findViewById(R.id.addTaskDescription));
						break;
					case DUE_DATE:
						views.add(findViewById(R.id.dueDateDropdown));
						views.add(findViewById(R.id.dueDateLabel));
						views.add(findViewById(R.id.dueDateLayout));
						views.add(findViewById(R.id.dueDateText));
						break;
					case LIST:
						views.add(findViewById(R.id.listsDropdown));
						break;
					case TAGS:
						views.add(findViewById(R.id.tags));
						break;
					}
					for (View view : views) {
						view.setVisibility(View.GONE);
					}
				}
			}
		}
	}

	// TODO: Maybe standardize the hint capability into swing as well?
	public void setNameLabel(String label) {
	}

	public void setDescriptionLabel(String label) {
	}

	public void setDueDateLabel(String label) {
	}

	public void setTaskListLabel(String label) {
	}

	public void setTagsLabel(String label) {
	}
}
