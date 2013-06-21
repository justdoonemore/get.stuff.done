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
package com.jdom.junit.utils;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;

public class AssertUtil {

	public static <T> void assertEquals(Collection<T> expected,
			Collection<T> actual) {
		Assert.assertEquals(
				"The two collections do not have the same number of items!",
				expected.size(), actual.size());
		Assert.assertTrue(
				"The actual collection didn't contain all of the items from the expected!",
				actual.containsAll(expected));
	}

	public static <T> void assertEqualsAndHashcode(T objectUnderTest,
			T equalObject, T notEqualObject) {
		List<T> equalObjects = new ArrayList<T>();
		equalObjects.add(equalObject);

		List<T> notEqualObjects = new ArrayList<T>();
		notEqualObjects.add(notEqualObject);

		assertEqualsAndHashcode(objectUnderTest, equalObjects, notEqualObjects);
	}

	public static <T> void assertEqualsAndHashcode(T objectUnderTest,
			Collection<T> equalObjects, Collection<T> notEqualObjects) {
		for (T equalObject : equalObjects) {
			assertThat(
					"The object under test should have been equal to the specified object!",
					objectUnderTest, equalTo(equalObject));
			assertThat(
					"The object under test should have had the same hashcode as the specified object!",
					objectUnderTest.hashCode(), equalTo(equalObject.hashCode()));
		}

		for (T notEqualObject : notEqualObjects) {
			assertThat(
					"The object under test should not have been equal to the specified object!",
					objectUnderTest, is(not(equalTo(notEqualObject))));
			assertThat(
					"The object under test should not have had the same hashcode as the specified object!",
					objectUnderTest.hashCode(),
					is(not(equalTo(notEqualObject.hashCode()))));
		}

		assertFalse(
				"The object under test should not have been equal to a random string!",
				objectUnderTest.equals("somestring"));
	}

	public static <T> void assertProperPrivateConstructor(Class<T> classToTest) {
		Constructor<T> constructor;
		try {
			constructor = classToTest.getDeclaredConstructor();

			assertFalse(
					"The constructor should not have been accesible directory!",
					constructor.isAccessible());
			constructor.setAccessible(true);
			constructor.newInstance();
		} catch (Exception e) {
			throw new IllegalStateException(
					"Exception encountered testing the private constructor!", e);
		}
	}
}
