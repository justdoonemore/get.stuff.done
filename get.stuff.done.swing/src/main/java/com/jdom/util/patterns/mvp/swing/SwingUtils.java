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
package com.jdom.util.patterns.mvp.swing;

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.jdom.util.patterns.mvp.MenuItemConfiguration;

public final class SwingUtils {

	private SwingUtils() {

	}

	public static ActionListener getMenuItemConfigurationActionListener(
			final MenuItemConfiguration menuItem) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				menuItem.getClickAction().run();
			}
		};
	}

	public static <T> T findChildByName(Container container,
			Class<T> returnType, String name) {

		if (name.equals(container.getName())) {
			return returnType.cast(container);
		}

		for (Component component : container.getComponents()) {
			if (name.equals(component.getName())) {
				return returnType.cast(component);
			} else if (component instanceof Container) {
				T recursiveResult = findChildByName((Container) component,
						returnType, name);

				if (recursiveResult != null) {
					return returnType.cast(recursiveResult);
				}
			}
		}

		throw new IllegalArgumentException(
				"Did not find child component by name [" + name
						+ "] in the specified container!");
	}

	public static ActionListener getActionListenerForRunnable(
			final Runnable runnable) {
		return new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runnable.run();
			}
		};
	}

}
