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
package com.jdom.util.compare;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * This is a utility class to performs comparisons on variables.
 * 
 * @author djohnson
 * 
 */
public class CompareUtil {

    public static final int LESS_THAN = -1;

    public static final int EQUAL = 0;

    public static final int GREATER_THAN = 1;

    private CompareUtil() {
        // Private constructor
    }

    /**
     * Compares two string variables for equality. Different case returns false.
     * 
     * @param first
     *            The first string variable
     * @param second
     *            The second string variable
     * @return True if both variables are null, or if they contain the same value. Different case
     *         returns false.
     */
    public static boolean isEqual(String first, String second) {
        return CompareUtil.isEqual(first, second, false);
    }

    /**
     * Compares two string variables for equality.
     * 
     * @param first
     *            The first string variable
     * @param second
     *            The second string variable
     * @param ignoreCase
     *            Whether or not to ignore the case of the strings
     * @return True if both variables are null, or if they contain the same value
     */
    public static boolean isEqual(String first, String second, boolean ignoreCase) {
        boolean equal = false;

        if ((first == null) && (second == null)) {
            equal = true;
        } else if ((first != null) && (second != null)) {
            if (ignoreCase) {
                equal = first.equalsIgnoreCase(second);
            } else {
                equal = first.equals(second);
            }
        }

        return equal;
    }

    /**
     * Compares two dates.
     * 
     * @param first
     *            the first date
     * @param second
     *            the second date
     * @return true if both variables are null, or are determined equal by the equals method.
     */
    public static boolean isEqual(Date first, Date second) {
        return first.equals(second);
    }

    /**
     * Compares two objects.
     * 
     * @param first
     *            the first object
     * @param second
     *            the second object
     * @return true if both variables are null, or are determined equal by the equals method.
     */
    public static boolean isEqual(Object first, Object second) {
        boolean equal = false;

        if ((first == null) && (second == null)) {
            equal = true;
        } else if ((first != null) && (second != null)) {
            equal = first.equals(second);
        }

        return equal;
    }

    /**
     * Test the equality of two collections.
     * 
     * @param <T>
     *            the type of object the collections contain
     * @param collection1
     *            collection
     * @param collection2
     *            collection
     * @return whether they are equal
     */
    public static <T> boolean isEqual(Collection<T> collection1, Collection<T> collection2) {
        boolean retVal = false;

        if (collection1 == null && collection2 == null) {
            retVal = true;
        } else if (collection1 != null && collection2 != null) {
            Collection<T> coll1 = new ArrayList<T>(collection1);
            Collection<T> coll2 = new ArrayList<T>(collection2);
            retVal = (coll1.containsAll(coll2) && coll2.containsAll(coll1));
        }

        return retVal;
    }
}
