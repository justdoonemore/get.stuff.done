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

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import org.apache.commons.io.FileUtils;

public abstract class AbstractFixture<T> {

    private static final double DOUBLE_FACTOR = .543;

    private static final int INT_FACTOR = 3;

    private static final Random RAND = new Random();

    private static File streamTempDir;

    /**
     * Get the default instance. Distinct, but equal each time.
     * 
     * @return the newly constructed instance.
     */
    public T getInstance() {
        return getInstance(0);
    }

    /**
     * Get a random instance.
     * 
     * @return the random instance, distinct from
     */
    public T getRandomInstance() {
        int salt;

        // Just make sure RAND doesn't accidentally output zero.
        do {
            salt = RAND.nextInt();
        } while (salt == 0);

        return getInstance(salt);
    }

    /**
     * Get an instance, salted by the provided argument. Invocations with the same argument return
     * distinct, but equal objects (i.e. obj1!=obj2, but obj1.equals(obj2)).
     * 
     * @param salt
     *            the randomizer to use
     * @return the newly created instance
     */
    public abstract T getInstance(int salt);

    /**
     * Get a randomized value.
     * 
     * @param value
     *            the value to randomize
     * @param salt
     *            the randomizer to use
     * @return the randomized value
     */
    public static Long getSaltedValue(Long value, int salt) {
        Long retValue;

        if (salt == 0) {
            retValue = value;
        } else {
            retValue = new Long(value * salt * salt);
        }

        return retValue;
    }

    /**
     * Get a randomized value.
     * 
     * @param value
     *            the value to randomize
     * @param salt
     *            the randomizer to use
     * @return the randomized value
     */
    public static long getSaltedValue(long value, int salt) {
        Long retValue;

        if (salt == 0) {
            retValue = value;
        } else {
            retValue = value * salt * salt;
        }

        return retValue;
    }

    /**
     * Get a randomized value.
     * 
     * @param value
     *            the value to randomize
     * @param salt
     *            the randomizer to use
     * @return the randomized value
     */
    public static float getSaltedValue(float value, int salt) {
        float retValue;

        if (salt == 0) {
            retValue = value;
        } else {
            retValue = value + salt;
        }

        return retValue;
    }

    /**
     * Get a randomized value.
     * 
     * @param value
     *            the value to randomize
     * @param salt
     *            the randomizer to use
     * @return the randomized value
     */
    public static int getSaltedValue(int value, int salt) {
        int retValue;

        if (salt == 0) {
            retValue = value;
        } else {
            retValue = value + salt;
        }

        return retValue;
    }

    /**
     * Get a randomized value.
     * 
     * @param value
     *            the value to randomize
     * @param salt
     *            the randomizer to use
     * @return the randomized value
     */
    public static Double getSaltedValue(Double value, int salt) {
        Double retValue;

        if (salt == 0) {
            retValue = value;
        } else {
            retValue = new Double(value.doubleValue() * salt * salt / 2 + DOUBLE_FACTOR);
        }

        return retValue;
    }

    /**
     * Get a randomized value.
     * 
     * @param value
     *            the value to randomize
     * @param salt
     *            the randomizer to use
     * @return the randomized value
     */
    public static String getSaltedValue(String value, int salt) {
        String retValue;

        if (salt == 0) {
            retValue = value;
        } else {
            retValue = salt + value.substring(String.valueOf(salt).length());
        }

        return retValue;
    }

    /**
     * Get a randomized value.
     * 
     * @param value
     *            the value to randomize
     * @param salt
     *            the randomizer to use
     * @return the randomized value
     */
    public static Integer getSaltedValue(Integer value, int salt) {
        Integer retValue;

        if (salt == 0) {
            retValue = value;
        } else {
            retValue = new Integer(value.intValue() * salt * salt + INT_FACTOR);
        }

        return retValue;
    }

    /**
     * Get a randomized value.
     * 
     * @param value
     *            the value to randomize
     * @param salt
     *            the randomizer to use
     * @return the randomized value
     */
    public static Boolean getSaltedValue(Boolean value, int salt) {
        boolean retValuePrimative = value.booleanValue();

        if (salt % 2 != 0) {
            retValuePrimative = !retValuePrimative;
        }

        return new Boolean(retValuePrimative);
    }

    /**
     * Get a randomized value.
     * 
     * @param value
     *            the value to randomize
     * @param salt
     *            the randomizer to use
     * @return the randomized value
     */
    public static Character getSaltedValue(Character value, int salt) {
        Character retValue;

        if (salt == 0) {
            retValue = value;
        } else {
            retValue = new Character(String.valueOf(salt).charAt(0));
        }

        return retValue;
    }

    /**
     * Get a randomized value.
     * 
     * @param value
     *            the value to randomize
     * @param salt
     *            the randomizer to use
     * @return the randomized value
     */
    public static Date getSaltedValue(Date value, int salt) {
        Date retValue;

        if (salt == 0) {
            retValue = value;
        } else {
            Calendar cal = Calendar.getInstance();
            cal.setTime(value);
            cal.add(Calendar.HOUR, salt);
            retValue = cal.getTime();
        }

        return retValue;
    }

    /**
     * Convenience method for creating and obtaining a reference to the stream temporary dir, which
     * is a concatenation of the value from the property java.io.tmpdir and the stream name, as
     * reported by mil.af.junit.utils.StreamUtil.
     * 
     * @return the stream temp dir
     */
    public static File setupTempDir() {
        File result;

        if (streamTempDir != null && streamTempDir.exists()) {
            result = streamTempDir;
        } else {
            result = new File(System.getProperty("java.io.tmpdir"));
            result.mkdirs();
            streamTempDir = result;
        }

        return result;

    }

    /**
     * Setup the directory the test class should use.
     * 
     * @param testClass
     *            The test class to create a directory for
     * @return The directory created for the test class
     */
    public static File setupTestClassDir(Class<?> testClass) {
        setupTempDir();

        File dir = new File(streamTempDir, testClass.getSimpleName());

        // Delete any preexisting version
        try {
            FileUtils.deleteDirectory(dir);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // Make the directory
        dir.mkdirs();

        return dir;
    }

}
