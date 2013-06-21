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
package com.jdom.util.properties;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public final class PropertiesUtil {

	public static final char SEPARATOR = ',';

	private PropertiesUtil() {

	}

	public static Properties readPropertiesFile(final String string)
			throws IllegalArgumentException {
		InputStream is = new ByteArrayInputStream(string.getBytes());
		return readPropertiesFile(is);
	}

	public static Properties readPropertiesFile(final File file)
			throws IllegalArgumentException {
		InputStream is = null;
		try {
			is = new FileInputStream(file);
			return readPropertiesFile(is);
		} catch (FileNotFoundException e) {
			throw new IllegalArgumentException(e);
		} finally {
			IOUtils.closeQuietly(is);
		}
	}

	public static Properties readPropertiesFile(final InputStream is)
			throws IllegalArgumentException {

		Properties properties = new Properties();

		try {
			properties.load(is);
		} catch (IOException e) {
			throw new IllegalArgumentException(
					"Unable to read the properties file!", e);
		} finally {
			IOUtils.closeQuietly(is);
		}

		return properties;
	}

	public static void writePropertiesFile(Properties properties,
			File outputFile) throws IllegalArgumentException {
		OutputStream os = null;

		try {
			outputFile.delete();
			outputFile.getParentFile().mkdirs();
			outputFile.createNewFile();
			os = new FileOutputStream(outputFile);

			properties.store(os, "");
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

	public static List<String> getPropertyAsList(Properties properties,
			String key) {
		String[] split = StringUtils.split(properties.getProperty(key),
				SEPARATOR);
		return (split != null && split.length > 0) ? Arrays.asList(split)
				: new ArrayList<String>();
	}

	public static Set<String> getPropertyAsSet(Properties properties, String key) {
		return new HashSet<String>(PropertiesUtil.getPropertyAsList(properties,
				key));
	}

	public static boolean getBoolean(Properties properties, String key,
			boolean defaultValue) {
		String stringValue = properties.getProperty(key, "" + defaultValue);
		return Boolean.parseBoolean(stringValue);
	}

	public static int getInteger(Properties properties, String key) {
		return Integer.parseInt(properties.getProperty(key));
	}

	public static <T> String join(Collection<T> collection) {
		return StringUtils.join(collection, SEPARATOR);
	}

	public static long getLong(Properties properties, String key) {
		return Long.parseLong(properties.getProperty(key));
	}
}