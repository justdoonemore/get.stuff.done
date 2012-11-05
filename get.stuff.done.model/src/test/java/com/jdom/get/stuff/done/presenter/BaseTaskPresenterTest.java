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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollectionOf;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.Test;

import com.jdom.get.stuff.done.domain.Task;
import com.jdom.get.stuff.done.model.BaseTaskModel;
import com.jdom.get.stuff.done.presenter.BaseTaskPresenter;
import com.jdom.get.stuff.done.presenter.BaseTaskView;
import com.jdom.util.collections.CollectionsUtil;
import com.jdom.util.patterns.mvp.ActionConfiguration;
import com.jdom.util.patterns.mvp.RunnableWithResults;

public abstract class BaseTaskPresenterTest<MODEL extends BaseTaskModel, VIEW extends BaseTaskView, PRESENTER extends BaseTaskPresenter<MODEL, VIEW>>
		extends AbstractPresenterTest<MODEL, VIEW, PRESENTER> {

	@Test
	public void testSetsNameLabelToViewOnInit() {
		presenter.init();

		verify(view, times(1)).setNameLabel(BaseTaskPresenter.NAME_LABEL);
	}

	@Test
	public void testSetsDescriptionLabelToViewOnInit() {
		presenter.init();

		verify(view, times(1)).setDescriptionLabel(
				BaseTaskPresenter.DESCRIPTION_LABEL);
	}

	@Test
	public void testSetsDueDateLabelToViewOnInit() {
		presenter.init();

		verify(view, times(1))
				.setDueDateLabel(BaseTaskPresenter.DUE_DATE_LABEL);
	}

	@Test
	public void testSetsTaskListLabelToViewOnInit() {
		presenter.init();

		verify(view, times(1)).setTaskListLabel(
				BaseTaskPresenter.TASK_LIST_LABEL);
	}

	@Test
	public void testSetsTagsLabelToViewOnInit() {
		presenter.init();

		verify(view, times(1)).setTagsLabel(BaseTaskPresenter.TAGS_LABEL);
	}

	@Test
	public void testSetsButtonToSelectDependenciesToViewOnInit() {
		presenter.init();

		verify(view, times(1)).setDependencySelectionButton(
				new ActionConfiguration(BaseTaskPresenter.SELECT_DEPENDENCIES,
						presenter.selectDependenciesAction));
	}

	@Test
	public void testSelectDependenciesActionWillPassAvailableDependenciesAndRunnableToFactoryMethod() {
		Set<Task> availableDeps = CollectionsUtil.asSet(new Task("task1"),
				new Task("task2"));
		when(model.getAvailableDependencies()).thenReturn(availableDeps);

		presenter.selectDependenciesAction.run();

		verify(model, times(1)).getAvailableDependencies();
		verify(contextFactory).displayCollectionOfItemsAsCheckBoxes(
				anyCollectionOf(String.class), anyCollectionOf(String.class),
				any(RunnableWithResults.class));
	}
}
