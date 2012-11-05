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
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.model.dao.DaoFactory;
import com.jdom.get.stuff.done.sync.SynchronizeStrategy;
import com.jdom.util.patterns.mvp.ApplicationContextFactory;
import com.jdom.util.patterns.mvp.BaseApplicationView;
import com.jdom.util.patterns.mvp.RunnableWithResults;

public class AndroidApplicationContextFactory implements ContextFactory {

	private static final String OK_BUTTON_TEXT = "OK";
	private final Activity activity;
	private final SynchronizeStrategy syncStrategy;

	public AndroidApplicationContextFactory(Activity activity) {
		this.activity = activity;
		boolean syncEnabled = this.getDaoFactory().getApplicationDao()
				.getSyncAccount() != null;
		syncStrategy = (syncEnabled) ? new AndroidSyncStrategy(activity, this)
				: SynchronizeStrategy.NULL_OBJECT;
	}

	public void displayAlert(String arg0) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false)
				.setPositiveButton(OK_BUTTON_TEXT,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						}).setMessage(arg0);
		builder.show();
	}

	public DaoFactory getDaoFactory() {
		return new AndroidDaoFactory(activity.getApplicationContext());
	}

	public void displayCollectionOfItemsAsCheckBoxes(
			final Collection<String> collection,
			final Collection<String> initialSelections,
			final RunnableWithResults<Collection<String>> callback) {

		final String[] array = collection
				.toArray(new String[collection.size()]);
		boolean[] checked = new boolean[collection.size()];
		for (int i = 0; i < array.length; i++) {
			String value = array[i];
			checked[i] = initialSelections.contains(value);
		}

		final List<String> selectedItems = new ArrayList<String>();
		for (String initialSelection : initialSelections) {
			selectedItems.add(initialSelection);
		}

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false)
				.setPositiveButton(OK_BUTTON_TEXT,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								callback.callback(selectedItems);
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						})
				.setMultiChoiceItems(array, checked,
						new DialogInterface.OnMultiChoiceClickListener() {
							public void onClick(DialogInterface dialog,
									int which, boolean isChecked) {
								String value = array[which];

								if (isChecked && !selectedItems.contains(value)) {
									selectedItems.add(value);
								} else {
									selectedItems.remove(value);
								}
							}
						});
		builder.show();
	}

	public void displayYesNoConfirmation(String title, String msg,
			final Runnable yesAction, final Runnable noAction) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setCancelable(false)
				.setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								yesAction.run();
							}
						})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						noAction.run();
					}
				}).setMessage(msg).setTitle(title);
		builder.show();

	}

	public void launchView(
			Class<? extends BaseApplicationView<? extends ApplicationContextFactory>> viewInterface) {
		launchView(viewInterface, new Properties());
	}

	public void launchView(
			Class<? extends BaseApplicationView<? extends ApplicationContextFactory>> viewInterface,
			Properties params) {
		String implementationPackage = AndroidApplicationContextFactory.class
				.getPackage().getName();
		String implementationName = viewInterface.getSimpleName().replace(
				"View", "Activity");
		Class<?> activityClass;
		try {
			activityClass = Class.forName(implementationPackage + "."
					+ implementationName);
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(
					"Unable to find activity class for the specified view interface!",
					e);
		}
		Intent intent = new Intent(activity.getApplicationContext(),
				activityClass);
		// Pass any parameters specified
		intent.putExtra(Constants.PARAMETERS, params);

		activity.startActivity(intent);
	}

	public String getVersion() {
		try {
			PackageInfo packageInfo = activity.getPackageManager()
					.getPackageInfo(activity.getPackageName(), 0);
			return packageInfo.versionName;
		} catch (NameNotFoundException e) {
			return "error getting version";
		}

	}

	public void getTextInputForAction(String title, String hintText,
			String doButtonText, String dontButtonText,
			final RunnableWithResults<String> callback) {

		final EditText textView = new EditText(activity);
		textView.setHint(hintText);

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setView(textView);
		builder.setTitle(title);
		builder.setCancelable(false)
				.setPositiveButton(doButtonText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								callback.callback(textView.getText().toString());
							}
						})
				.setNegativeButton(dontButtonText,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						});
		builder.show();

	}

	public void displayCollectionOfItemsAsRadioButtonGroup(String message,
			Collection<String> collection, String initialSelection,
			final RunnableWithResults<String> callback) {
		final String[] array = collection
				.toArray(new String[collection.size()]);
		int selectedItemIndex = -1;
		if (initialSelection != null) {
			for (int i = 0; i < array.length; i++) {
				if (array[i].equals(initialSelection)) {
					selectedItemIndex = i;
				}
			}
		}

		final List<String> selectedItems = new ArrayList<String>();
		if (initialSelection != null) {
			selectedItems.add(initialSelection);
		}

		ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_list_item_single_choice, array);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		if (!StringUtils.isEmpty(message)) {
			builder.setTitle(message);
		}

		builder.setPositiveButton(OK_BUTTON_TEXT,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						if (!selectedItems.isEmpty()) {
							callback.callback(selectedItems.iterator().next());
						}
					}
				})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
							}
						})
				.setSingleChoiceItems(arrayAdapter, selectedItemIndex,
						new OnClickListener() {
							public void onClick(DialogInterface arg0, int arg1) {
								String value = array[arg1];
								selectedItems.clear();
								selectedItems.add(value);
							}
						}).show();
	}

	public Set<String> getAvailableSyncAccounts() {
		AccountManager accountManager = AccountManager.get(activity);
		Account[] accounts = accountManager.getAccountsByType("com.google");

		Set<String> accountSet = new TreeSet<String>();
		for (Account account : accounts) {
			accountSet.add(account.name);
		}
		return accountSet;
	}

	public SynchronizeStrategy getSyncStrategy() {
		return syncStrategy;
	}

	public void displayProVersionPromo(String proAppName, String proVersionLink) {
		final String text = "For the cost of a soda, $0.99, you can enable the following features:\n"
				+ "- No advertisements\n"
				+ "- Sync frequency/action configuration\n"
				+ "- Configure the fields displayed in the add/edit task page.\n\n"
				+ "Click the OK button to load the pro version in the market viewer, or Cancel to stay with the free version.";

		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Upgrade");
		builder.setMessage(text);
		builder.setPositiveButton(OK_BUTTON_TEXT,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent(Intent.ACTION_VIEW);
						intent.setData(Uri.parse(Constants.PRO_VERSION_LINK));
						activity.startActivity(intent);
					}
				}).setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
					}
				});

		builder.show();
	}
}
