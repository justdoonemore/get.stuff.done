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
package com.jdom.util.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;

import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import com.jdom.util.patterns.mvp.MenuItemConfiguration;
import com.jdom.util.patterns.mvp.swing.SwingUtils;

public class RightClickPopupMenuListener extends MouseAdapter {
	private final JList list;

	private final Collection<MenuItemConfiguration> menuItems;

	public RightClickPopupMenuListener(final JList list,
			Collection<MenuItemConfiguration> menuItems) {
		this.list = list;
		this.menuItems = menuItems;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (SwingUtilities.isRightMouseButton(e)) {
			list.setSelectedIndex(list.locationToIndex(e.getPoint()));
			displayMenu(e);
		}
	}

	private void displayMenu(MouseEvent e) {
		JPopupMenu popupMenu = new JPopupMenu();
		for (final MenuItemConfiguration menuItemConfiguration : menuItems) {
			JMenuItem menuItem = new JMenuItem(
					menuItemConfiguration.getDisplayText());
			menuItem.addActionListener(SwingUtils
					.getActionListenerForRunnable(menuItemConfiguration
							.getClickAction()));
			popupMenu.add(menuItem);
		}

		popupMenu.show(e.getComponent(), e.getX(), e.getY());
	}

}
