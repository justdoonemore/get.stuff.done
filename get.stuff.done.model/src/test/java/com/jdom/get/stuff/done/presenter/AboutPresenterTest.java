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
package com.jdom.get.stuff.done.presenter;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.jdom.get.stuff.done.model.AboutModel;
import com.jdom.get.stuff.done.presenter.AboutPresenter;
import com.jdom.get.stuff.done.presenter.AboutView;

@RunWith(PowerMockRunner.class)
public class AboutPresenterTest extends
		AbstractPresenterTest<AboutModel, AboutView, AboutPresenter> {

	private final String version = "1.0";

	@Override
	public void setUp() {
		super.setUp();
		when(contextFactory.getVersion()).thenReturn(version);
	}

	@Test
	public void testSetsVersionInformationToViewOnInit() {
		presenter.init();

		verify(view, times(1)).setVersion(version);
	}

	@Override
	protected Class<AboutModel> getModelClass() {
		return AboutModel.class;
	}

	@Override
	protected Class<AboutView> getViewClass() {
		return AboutView.class;
	}

	@Override
	protected AboutPresenter getPresenter(AboutModel model, AboutView view) {
		return AboutPresenter.construct(model, view, new Properties());
	}
}
