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
package com.jdom.util.patterns.mvp;

import java.util.Collection;
import java.util.Properties;

public interface ApplicationContextFactory {
	void displayAlert(String msg);

	void displayCollectionOfItemsAsCheckBoxes(Collection<String> collection,
			Collection<String> initialSelections,
			RunnableWithResults<Collection<String>> callback);

	void displayCollectionOfItemsAsRadioButtonGroup(String message,
			Collection<String> collection, String initialSelection,
			RunnableWithResults<String> callback);

	void displayYesNoConfirmation(String title, String msg, Runnable yesAction,
			Runnable noAction);

	void getTextInputForAction(String title, String hintText,
			String doButtonText, String dontButtonText,
			final RunnableWithResults<String> callback);

	void launchView(
			Class<? extends BaseApplicationView<? extends ApplicationContextFactory>> viewInterface);

	void launchView(
			Class<? extends BaseApplicationView<? extends ApplicationContextFactory>> viewInterface,
			Properties params);

	String getVersion();
}
