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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Map;
import java.util.Properties;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.util.patterns.mvp.BaseApplicationPresenter;
import com.jdom.util.patterns.mvp.BaseApplicationView;

public abstract class BaseActivity<PRESENTER extends BaseApplicationPresenter<ContextFactory, ?, ?>>
		extends Activity implements BaseApplicationView<ContextFactory> {
	public class DefaultExceptionHandler implements UncaughtExceptionHandler {

		public void uncaughtException(Thread arg0, Throwable arg1) {
			Log.e(BaseActivity.class.getName(), "Uncaught exception!", arg1);
		}

	}

	static final int LIST_ITEM_TYPE = android.R.layout.simple_list_item_1;
	protected static final int SPINNER_ITEM_TYPE = LIST_ITEM_TYPE;

	private PRESENTER presenter;

	private ContextFactory contextFactory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		contextFactory = new AndroidApplicationContextFactory(this);

		// Pass any parameters passed in with the bundle
		Bundle extras = getIntent().getExtras();
		Properties params = new Properties();
		if (extras != null) {
			params.putAll((Map<?, ?>) extras.get(Constants.PARAMETERS));
		}

		this.presenter = setupPresenter(params);

		Thread.setDefaultUncaughtExceptionHandler(new DefaultExceptionHandler());
	}

	@Override
	protected void onResume() {
		super.onResume();

		presenter.init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.synchronize:
			final ProgressDialog pd = ProgressDialog.show(this, "",
					"Syncing..", true);

			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					try {
						if (pd.isShowing()) {
							pd.dismiss();
						}

					} catch (Exception e) {
						Log.e(BaseActivity.class.getName(),
								"Exception dismissing progress dialog!", e);
					}
					presenter.update(null);
				}
			};

			Runnable afterCompletion = new Runnable() {
				public void run() {
					handler.sendEmptyMessage(0);
				}
			};

			this.getApplicationContextFactory().getSyncStrategy()
					.synchronizeSynchronously(afterCompletion);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onClickHome(View v) {
		goHome(this);
	}

	public void onClickAbout(View v) {
		startActivity(new Intent(getApplicationContext(), AboutActivity.class));
	}

	public void goHome(Context context) {
		final Intent intent = new Intent(context, DashboardActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		context.startActivity(intent);
	}

	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Send a message to the debug log and display it using Toast.
	 */
	public void trace(String msg) {
		Log.d("Get Stuff Done", msg);
		toast(msg);
	}

	protected abstract PRESENTER setupPresenter(Properties params);

	public ContextFactory getApplicationContextFactory() {
		return contextFactory;
	}

	PRESENTER getPresenter() {
		return presenter;
	}

	public void close() {
		goHome(this);
	}

	public void setTitleBarTitle(String title) {
		TextView titleText = (TextView) findViewById(R.id.title_text);
		titleText.setText(title);
	}

	public void setTitleBarAboutAction(Runnable action) {
		ImageButton button = (ImageButton) findViewById(R.id.title_about);
		button.setOnClickListener(getOnClickListenerForRunnable(action));
	}

	public void setTitleBarHomeAction(Runnable action) {
		ImageButton button = (ImageButton) findViewById(R.id.title_home);
		button.setOnClickListener(getOnClickListenerForRunnable(action));
	}

	public void setTitleBarAddTaskAction(Runnable action) {
		ImageButton button = (ImageButton) findViewById(R.id.title_add_task);
		button.setOnClickListener(getOnClickListenerForRunnable(action));
	}

	protected OnClickListener getOnClickListenerForRunnable(
			final Runnable clickAction) {
		return new OnClickListener() {
			public void onClick(View v) {
				clickAction.run();
			}
		};
	}
}