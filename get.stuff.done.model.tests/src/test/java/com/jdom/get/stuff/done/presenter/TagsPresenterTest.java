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
import java.util.Set;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

import com.jdom.get.stuff.done.model.TagsModel;
import com.jdom.get.stuff.done.presenter.TagsPresenter;
import com.jdom.get.stuff.done.presenter.TagsView;
import com.jdom.get.stuff.done.presenter.TasksView;
import com.jdom.util.collections.CollectionsUtil;

public class TagsPresenterTest extends
		AbstractPresenterTest<TagsModel, TagsView, TagsPresenter> {

	@Test
	public void testSetsAvailableTagsFromModelToViewOnUpdate() {
		Set<String> itemOptions = CollectionsUtil.asSet("tag1", "tag2");

		when(model.getTagsListItems()).thenReturn(itemOptions);

		presenter.update(model);

		verify(view, times(1)).setTagsListItems(Mockito.eq(itemOptions),
				Mockito.any(Runnable.class));
	}

	@Test
	public void testSetsRunnableToRetrieveSelectedTagToViewOnUpdate() {
		Set<String> itemOptions = CollectionsUtil.asSet("tag1", "tag2");
		when(model.getTagsListItems()).thenReturn(itemOptions);

		presenter.update(model);

		verify(view, times(1)).setTagsListItems(Mockito.eq(itemOptions),
				Mockito.same(presenter.selectedTagRunnable));
	}

	@Test
	public void testTagSelectedInvokesFactoryMethod() {
		final String tagName = "tagSelected";
		when(view.getSelectedTag()).thenReturn(tagName);

		presenter.selectedTagRunnable.run();

		verify(contextFactory, times(1)).launchView(
				Matchers.same(TasksView.class), Matchers.any(Properties.class));
	}

	@Override
	protected Class<TagsModel> getModelClass() {
		return TagsModel.class;
	}

	@Override
	protected Class<TagsView> getViewClass() {
		return TagsView.class;
	}

	@Override
	protected TagsPresenter getPresenter(TagsModel model, TagsView view) {
		return TagsPresenter.construct(model, view, new Properties());
	}
}
