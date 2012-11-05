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
package com.jdom.util.string;

public class StringUtil {

    private static final String EMPTY_STRING = "";

    /**
     * Tests whether a string is empty or null.
     * 
     * @param string
     *            the string
     * @return true if the string is null or empty
     */
    public static boolean isEmpty(String string) {
        return (string == null) || (EMPTY_STRING.equals(string));
    }

    public static String titleCase(String string) {
        StringBuilder retVal = new StringBuilder("" + Character.toUpperCase(string.charAt(0)));

        if (string.length() > 1) {
            retVal.append(string.substring(1).toLowerCase());
        }

        return retVal.toString();
    }

}
