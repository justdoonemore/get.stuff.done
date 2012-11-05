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

import java.util.Properties;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jdom.get.stuff.done.domain.Constants;
import com.jdom.get.stuff.done.presenter.AboutPresenter;
import com.jdom.get.stuff.done.presenter.AboutView;

public class AboutActivity extends BaseActivity<AboutPresenter> implements
		AboutView {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.about);

		if (Constants.PRO_VERSION) {
			Button upgradeButton = (Button) findViewById(R.id.upgradeButton);
			upgradeButton.setVisibility(View.GONE);
		}
	}

	@Override
	protected AboutPresenter setupPresenter(Properties params) {
		return AboutPresenter.construct(this, params);
	}

	public void setVersion(String version) {
		TextView versionText = (TextView) findViewById(R.id.versionText);
		versionText.setText(version);
	}

	public void setAppName(String appName) {
		TextView appText = (TextView) findViewById(R.id.appNameText);
		appText.setText(appName);
	}

	public void setEdition(String edition) {
		TextView text = (TextView) findViewById(R.id.appEditionText);
		text.setText(edition);
	}

	public void upgrade(View view) {
		getPresenter().displayProVersionPopup();
	}
}
