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

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Properties;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.jdom.get.stuff.done.model.ContextFactory;
import com.jdom.get.stuff.done.model.dao.ApplicationDao;
import com.jdom.get.stuff.done.model.dao.DaoFactory;
import com.jdom.get.stuff.done.presenter.BasePresenter;
import com.jdom.get.stuff.done.presenter.BaseView;
import com.jdom.get.stuff.done.sync.SynchronizeStrategy;
import com.jdom.util.patterns.mvp.BaseApplicationModel;
import com.jdom.util.patterns.mvp.BaseApplicationPresenter;
import com.jdom.util.reflection.ReflectionUtils;

@Ignore
public abstract class AbstractPresenterTest<MODEL extends BaseApplicationModel<ContextFactory>, VIEW extends BaseView, PRESENTER extends BasePresenter<MODEL, VIEW>> {

	protected final MODEL model = mock(getModelClass());

	protected final VIEW view = mock(getViewClass());

	protected final PRESENTER presenter = getPresenter(model, view);

	protected final ContextFactory contextFactory = Mockito
			.mock(ContextFactory.class);

	protected final DaoFactory daoFactory = Mockito.mock(DaoFactory.class);

	protected final ApplicationDao dao = Mockito.mock(ApplicationDao.class);

	protected final SynchronizeStrategy syncStrategy = Mockito
			.mock(SynchronizeStrategy.class);

	@Before
	public void setUp() {
		when(view.getApplicationContextFactory()).thenReturn(contextFactory);
		when(contextFactory.getSyncStrategy()).thenReturn(syncStrategy);
		when(contextFactory.getDaoFactory()).thenReturn(daoFactory);
		when(daoFactory.getApplicationDao()).thenReturn(dao);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testConstructionWithoutExplicitConstructorHasNotNullModel()
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		Method method = ReflectionUtils.getMethod(presenter.getClass(),
				"construct", getViewClass(), Properties.class);
		PRESENTER oneArgPresenter = (PRESENTER) ReflectionUtils.invoke(method,
				presenter.getClass(), view, new Properties());
		MODEL model = ReflectionUtils.getField(BaseApplicationPresenter.class,
				oneArgPresenter, "model", getModelClass());
		assertNotNull("Model should have not been null!", model);
	}

	@Test
	public void testOnConstructPresenterRegistersAsObserverOfModel() {
		verify(model, times(1)).addObserver(presenter);
	}

	@Test
	public void testApplicationContextFactoryReturnedByViewIsSetOnModelDuringConstruct() {
		ContextFactory applicationContextFactory = Mockito
				.mock(ContextFactory.class);
		when(view.getApplicationContextFactory()).thenReturn(
				applicationContextFactory);

		getPresenter(model, view);

		verify(model, times(1)).setApplicationContextFactory(
				applicationContextFactory);
	}

	@Test
	public void testSetsTitleBarTitleOnInit() {
		presenter.init();

		verify(view, times(1)).setTitleBarTitle(anyString());
	}

	@Test
	public void testSetsTitleBarAboutActionOnInit() {
		presenter.init();

		verify(view, times(1)).setTitleBarAboutAction(presenter.aboutAction);
	}

	@Test
	public void testSetsTitleBarHomeActionOnInit() {
		presenter.init();

		verify(view, times(1)).setTitleBarHomeAction(presenter.homeAction);
	}

	@Test
	public void testSetsTitleBarAddTaskActionOnInit() {
		presenter.init();

		verify(view, times(1))
				.setTitleBarAddTaskAction(presenter.addTaskAction);
	}

	// TODO: This should be uncommented if the functionality is added
	// @Test
	// public void testInvokesCheckForServerUpdatesOnInit() {
	// presenter.init();
	//
	// verify(syncStrategy).synchronize();
	// }

	protected abstract Class<MODEL> getModelClass();

	protected abstract Class<VIEW> getViewClass();

	protected abstract PRESENTER getPresenter(MODEL model, VIEW view);
}
