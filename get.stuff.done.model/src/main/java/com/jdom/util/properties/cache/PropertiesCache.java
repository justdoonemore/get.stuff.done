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
package com.jdom.util.properties.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jdom.util.properties.PropertyLookup;

public final class PropertiesCache {
	private static Properties properties = new Properties();

	public static void configure(String filename) throws IOException {
		properties.clear();

		InputStream is = PropertiesCache.class.getResourceAsStream(filename);

		if (is != null) {
			properties.load(is);
		} else {
			throw new FileNotFoundException(
					"Unable to find file designated by [" + filename + "]!");
		}
	}

	public static String getString(PropertyLookup key) {
		return properties.getProperty(key.getLookup());
	}

	public static int getInteger(PropertyLookup key) {
		String property = getString(key);
		return Integer.parseInt(property);
	}

	public static double getDouble(PropertyLookup key) {
		String property = getString(key);
		return Double.parseDouble(property);
	}

	public static String[] getStringArray(PropertyLookup key) {
		String property = getString(key);

		String[] values = property.split(",");
		return values;
	}

	/**
	 * Package level method for use in testing which allows a property to be
	 * set.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public static void setProperty(String key, String value) {
		properties.setProperty(key, value);
	}
}
