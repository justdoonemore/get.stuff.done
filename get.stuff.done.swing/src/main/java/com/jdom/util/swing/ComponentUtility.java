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

import java.awt.Component;
import java.awt.Container;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.text.JTextComponent;

import com.jdom.util.properties.PropertyLookup;
import com.jdom.util.properties.cache.PropertiesCache;

public final class ComponentUtility {

    public static JButton getButton(String displayText, ActionListener actionListener) {
        return getButton(displayText, actionListener, null);
    }

    public static JButton getButton(String displayText, ActionListener actionListener, String name) {
        JButton button = new JButton(displayText);
        button.setActionCommand(displayText);
        button.addActionListener(actionListener);

        if (name != null && !"".equals(name)) {
            button.setName(name);
        }

        return button;
    }

    public static boolean findComponent(Container container, Component component) {
        for (Component comp : container.getComponents()) {
            if (comp == component) {
                return true;
            } else if (comp instanceof Container) {
                Container inner = (Container) comp;
                if (findComponent(inner, component)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static <T> List<T> findComponentsByType(Container container, Class<T> clazz) {

        List<T> componentsByType = new ArrayList<T>();

        for (Component comp : container.getComponents()) {
            if (clazz.isAssignableFrom(comp.getClass())) {
                componentsByType.add(clazz.cast(comp));
            } else if (comp instanceof Container) {
                Container inner = (Container) comp;
                componentsByType.addAll(ComponentUtility.findComponentsByType(inner, clazz));
            }
        }

        return componentsByType;
    }

    public static <T extends Component> T findComponentByName(Container container, String name, Class<T> clazz) {
        List<T> components = findComponentsByType(container, clazz);

        Iterator<T> iter = components.iterator();

        while (iter.hasNext()) {

            T possibleResult = iter.next();

            if (!name.equals(possibleResult.getName())) {
                iter.remove();
            }
        }

        if (components.isEmpty()) {
            throw new SwingException("Unable to find a component with name [" + name + "] and type [" + clazz.getName()
                    + "]");
        } else if (components.size() > 1) {
            throw new SwingException("Found multiple components with the same name and type!");
        }

        return components.get(0);
    }

    public static void resizeApp(JFrame app) {
        if (app != null) {
            app.pack();
        }
    }

    public static void click(Container container, PropertyLookup buttonLookup) {
        JButton button = ComponentUtility.findComponentByName(container, PropertiesCache.getString(buttonLookup),
                JButton.class);
        button.doClick();
    }

    public static void enter(Container container, PropertyLookup fieldLookup, String textToEnter) {
        JTextComponent textComponent = ComponentUtility.findComponentByName(container, PropertiesCache
                .getString(fieldLookup), JTextComponent.class);
        textComponent.setText(textToEnter);
    }

    public static int getRowsInTable(Container container, PropertyLookup tableLookup) {
        JTable table = ComponentUtility.findComponentByName(container, PropertiesCache.getString(tableLookup),
                JTable.class);

        return table.getRowCount();
    }

    /**
     * Gets the data from a row (starting at 1) in the table.
     * 
     * @param container
     *            the container
     * @param tableLookup
     *            the lookup for the table
     * @param row
     *            the row, starting at 0
     * @return the data
     */
    public static List<String> getRowDataInTable(Container container, PropertyLookup tableLookup, int row) {
        JTable table = ComponentUtility.findComponentByName(container, PropertiesCache.getString(tableLookup),
                JTable.class);

        int columnCount = table.getColumnCount();
        int rowCount = table.getRowCount();

        if (row > rowCount - 1) {
            throw new SwingException("Attempted to reference a row out of bounds of the table!");
        }

        Vector<String> data = new Vector<String>(columnCount);

        for (int i = 0; i < columnCount; i++) {
            data.add((String) table.getValueAt(row, i));
        }

        return data;
    }

    public static <T> void select(Container container, PropertyLookup selectorLookup, T item) {
        JComboBox selector = ComponentUtility.findComponentByName(container, PropertiesCache.getString(selectorLookup),
                JComboBox.class);

        for (int i = 0; i < selector.getItemCount(); i++) {
            if (item.equals(selector.getItemAt(i))) {
                selector.setSelectedItem(item);
                return;
            }
        }

        throw new SwingException("Item [" + item + "] does not exist in the selector!");
    }
}
