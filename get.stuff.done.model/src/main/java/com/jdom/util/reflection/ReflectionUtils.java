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
package com.jdom.util.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * A utility class of basic reflection operations. </pre>
 */
public final class ReflectionUtils {
	private static final String EMPTY_STRING = "";
	/* PROPERTY_NAME_IS_NULL_OR_EMTY */
	private static final String PROPERTY_NAME_IS_NULL_OR_EMPTY = "Parameter (String propertyName) is null or empty.";

	/**
	 * Default Constructor.
	 */
	private ReflectionUtils() {

	}

	/**
	 * Creates a getter name by concatenating get with a capitalized version of
	 * the property name. Uses standard camel case.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the getter name of the property
	 */
	public static String getBooleanGetterName(String propertyName) {
		propertyName = propertyName.trim();

		if ((propertyName == null) || (EMPTY_STRING.equals(propertyName))) {
			throw new IllegalArgumentException(PROPERTY_NAME_IS_NULL_OR_EMPTY);
		}

		String capitalProperty = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1);
		return "is" + capitalProperty;
	}

	/**
	 * Creates a getter name by concatenating get with a capitalized version of
	 * the property name. Uses standard camel case.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the getter name of the property
	 */
	public static String getGetterName(String propertyName) {
		propertyName = propertyName.trim();

		if ((propertyName == null) || (EMPTY_STRING.equals(propertyName))) {
			throw new IllegalArgumentException(PROPERTY_NAME_IS_NULL_OR_EMPTY);
		}

		String capitalProperty = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1);
		return "get" + capitalProperty;
	}

	/**
	 * Creates a setter name by concatenating set with a capitalized version of
	 * the property name. Uses standard camel case.
	 * 
	 * @param propertyName
	 *            the property name
	 * @return the setter name of the property
	 */
	public static String getSetterName(String propertyName) {
		propertyName = propertyName.trim();

		if ((propertyName == null) || (EMPTY_STRING.equals(propertyName))) {
			throw new IllegalArgumentException(PROPERTY_NAME_IS_NULL_OR_EMPTY);
		}

		String capitalProperty = propertyName.substring(0, 1).toUpperCase()
				+ propertyName.substring(1);
		return "set" + capitalProperty;
	}

	/**
	 * Gets the type of the property.
	 * 
	 * @param objectClass
	 *            The Class of object in which the property presides
	 * @param propertyName
	 *            the property name of which to find the type
	 * @return the type of the property
	 * @throws ReflectionException
	 *             On error
	 */
	public static Class<?> getType(Class<? extends Object> objectClass,
			String propertyName) throws ReflectionException {
		if (objectClass == null) {
			throw new IllegalArgumentException(
					"Parameter (Class<? extends Object> objectClass) is null.");
		}

		if ((propertyName == null) || (EMPTY_STRING.equals(propertyName))) {
			throw new IllegalArgumentException(PROPERTY_NAME_IS_NULL_OR_EMPTY);
		}

		propertyName = propertyName.trim();

		try {
			Method method = null;
			try {
				String getterName = getGetterName(propertyName);
				method = getMethod(objectClass, getterName);
			} catch (ReflectionException e) {
				String getterName = getBooleanGetterName(propertyName);
				method = getMethod(objectClass, getterName);
			}

			if (method == null) {
				throw new ReflectionException(String.format(
						"Could not find getter for: [%s]", propertyName), null);
			}

			return method.getReturnType();
		} catch (Exception e) {
			throw new ReflectionException(e);
		}
	}

	/**
	 * Checks if a method name starts with get.
	 * 
	 * @param methodName
	 *            the method name
	 * @return true or false
	 */
	public static boolean isGetter(String methodName) {
		methodName = methodName.trim();

		if ((methodName == null) || (EMPTY_STRING.equals(methodName))) {
			throw new IllegalArgumentException(
					"Parameter (String methodName) is null or empty.");
		}

		return !"getClass".equals(methodName)
				&& (methodName.startsWith("get") || methodName.startsWith("is"));
	}

	/**
	 * Tries to parse a String into the desired object.
	 * 
	 * @param <T>
	 *            the returned object.
	 * @param returnType
	 *            The object type desired
	 * @param value
	 *            The String representation of the object
	 * @return an object representation of the string if successful. Otherwise
	 *         will return a String
	 */
	@SuppressWarnings({ "unchecked", "deprecation" })
	public static <T> T parseStringValue(Class<T> returnType, String value) {
		T objValue = null;

		if (returnType == null) {
			throw new IllegalArgumentException(
					"Parameter (Class<T> returnType) is null.");
		}

		if ((!returnType.isPrimitive()) && value == null) {
			return null;
		}
		if (char[].class.equals(returnType)) {
			return (T) value.toCharArray();
		}
		if (java.util.Date.class.equals(returnType)) {
			try {
				objValue = (T) new Date(Date.parse(value));
			} catch (IllegalArgumentException e) {
				// try to parse with java.sql.Date parser
				try {
					objValue = (T) new Date(java.sql.Date.valueOf(value)
							.getTime());
				} catch (RuntimeException e1) {
					// finally, try to parse with set format
					SimpleDateFormat sdf = new SimpleDateFormat(
							"yyyy-MM-dd H:mm:ss");
					try {
						objValue = (T) sdf.parse(value);
					} catch (ParseException e2) {
						System.out
								.println("Unable to parse date with format = "
										+ value);
					}
				}
			}
		} else {
			// objValue = (T) ConvertUtils.convert(value, returnType);
		}

		return objValue;
	}

	/**
	 * Sets the value of a property on an object.
	 * 
	 * @param obj
	 *            the object to set the value on
	 * @param propertyName
	 *            the name of the property
	 * @param value
	 *            the of a property to set in the object
	 * @throws ReflectionException
	 *             On error
	 */
	public static void setPropertyValue(Object obj, String propertyName,
			Object value) throws ReflectionException {
		propertyName = propertyName.trim();

		if (obj == null) {
			throw new IllegalArgumentException(
					"Parameter (Object obj) is null.");
		}

		if ((propertyName == null) || (EMPTY_STRING.equals(propertyName))) {
			throw new IllegalArgumentException(PROPERTY_NAME_IS_NULL_OR_EMPTY);
		}
		try {
			String setterName = getSetterName(propertyName);
			Class<?> clazz = obj.getClass();
			Class<?> type = getType(clazz, propertyName);
			Method method = getMethod(obj.getClass(), setterName, type);
			invoke(method, obj, value);
		} catch (SecurityException e) {
			throw new ReflectionException(e);
		}
	}

	/**
	 * Gets the property value of an object from a property name.
	 * 
	 * @param obj
	 *            the object to get the value from
	 * @param propertyName
	 *            the name of the property
	 * @return the property value
	 * @throws ReflectionException
	 *             On error
	 */
	public static Object getPropertyValue(Object obj, String propertyName)
			throws ReflectionException {
		propertyName = propertyName.trim();

		if (obj == null) {
			throw new IllegalArgumentException(
					"Parameter (Object obj) is null.");
		}

		if ((propertyName == null) || (EMPTY_STRING.equals(propertyName))) {
			throw new IllegalArgumentException(PROPERTY_NAME_IS_NULL_OR_EMPTY);
		}

		Object propertyValue;
		try {
			Method method = null;
			try {
				// First try a standard getter
				String getterName = getGetterName(propertyName);
				method = getMethod(obj.getClass(), getterName);
			} catch (ReflectionException re) {
				// Now try a boolean
				method = getMethod(obj.getClass(),
						getBooleanGetterName(propertyName));
			}
			propertyValue = invoke(method, obj);
		}

		catch (SecurityException e) {
			throw new ReflectionException(e);
		}

		return propertyValue;
	}

	/**
	 * Creates a new instance of an object from a class with no parameters,
	 * using the default constructor.
	 * 
	 * @param <T>
	 *            The object
	 * @param clazz
	 *            The class
	 * @return a new object instance
	 * @throws ReflectionException
	 *             On error
	 */
	public static <T> T newInstance(Class<T> clazz) throws ReflectionException {

		try {
			return clazz.getConstructor().newInstance();
		} catch (Exception e) {
			throw new ReflectionException(e);
		}
	}

	/**
	 * Creates a new instance of an object from a constructor and parameters.
	 * 
	 * @param <T>
	 *            The type defined by the constructor.
	 * @param constructor
	 *            the constructor.
	 * @param initargs
	 *            arguments to pass to the constructor
	 * @return a new instance of the object
	 * @throws ReflectionException
	 *             On Error
	 */
	public static <T> T newInstance(Constructor<T> constructor,
			Object... initargs) throws ReflectionException {

		try {
			return constructor.newInstance(initargs);
		} catch (Exception e) {
			throw new ReflectionException(e);
		}
	}

	/**
	 * Finds the constructor based on types for the specified class.
	 * 
	 * @param <T>
	 *            The type defined by the class.
	 * @param clazz
	 *            the class
	 * @param parameterTypes
	 *            the types of arguments that define the constructor.
	 * @return the constructor
	 * @throws ReflectionException
	 *             On Error
	 */
	public static <T> Constructor<T> getConstructor(Class<T> clazz,
			Class<?>... parameterTypes) throws ReflectionException {

		try {

			return clazz.getConstructor(parameterTypes);
		} catch (Exception e) {
			throw new ReflectionException(e);
		}
	}

	/**
	 * Calls Method.invoke(Object obj, Object... args).
	 * 
	 * @param method
	 *            the method to invoke
	 * @param obj
	 *            the object the underlying method is invoked from
	 * @param args
	 *            the arguments used for the method call
	 * @return the result of dispatching the method represented by this object
	 *         on <code>obj</code> with parameters <code>args</code>
	 * @throws ReflectionException
	 *             on Error.
	 */
	public static Object invoke(Method method, Object obj, Object... args)
			throws ReflectionException {

		try {
			return method.invoke(obj, args);
		} catch (Exception e) {
			throw new ReflectionException(e);
		}
	}

	/**
	 * Calls Class.forName(String className).
	 * 
	 * @param className
	 *            the fully qualified name of the desired class.
	 * @return the <code>Class</code> object for the class with the specified
	 *         name.
	 * @throws ReflectionException
	 *             on Error.
	 */
	public static Class<?> forName(String className) throws ReflectionException {

		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			throw new ReflectionException(e);
		}
	}

	/**
	 * Calls Class.getMethod(String name, Class<?>... parameterTypes).
	 * 
	 * @param clazz
	 *            the Class to call getMethod on.
	 * @param name
	 *            the name of the method to get.
	 * @param parameterTypes
	 *            the parameter types of the method
	 * @return the method
	 * @throws ReflectionException
	 *             on Error
	 */
	public static Method getMethod(Class<?> clazz, String name,
			Class<?>... parameterTypes) throws ReflectionException {
		try {
			return clazz.getMethod(name, parameterTypes);
		} catch (Exception e) {
			throw new ReflectionException(e);
		}
	}

	/**
	 * Creates a copy of the JavaBean type object (those that have a getter and
	 * setter for every attribute).
	 * 
	 * @param <T>
	 *            the object.
	 * @param object
	 *            the object to copy
	 * @return the copied object
	 * @throws ReflectionException
	 */
	public static <T> T getClone(T object) throws ReflectionException {
		T clone = null;
		@SuppressWarnings("unchecked")
		final Class<T> cloneClass = (Class<T>) object.getClass();
		T tmpClone = newInstance(cloneClass);

		Method[] methods = cloneClass.getMethods();
		for (Method method : methods) {
			final String methodName = method.getName();
			if (isGetter(methodName)) {
				// call the getter
				Object got = invoke(method, object, (Object[]) null);

				// get the setter and call it
				Method setMethod = getMethod(cloneClass,
						methodName.replaceFirst("get", "set"),
						method.getReturnType());
				invoke(setMethod, tmpClone, got);
			}
		}
		clone = tmpClone;
		return clone;
	}

	public static <T> T newInstanceOfAssignableType(Class<T> assignableType,
			String className) throws ReflectionException {
		return newInstanceOfAssignableType(assignableType, className,
				new Object[] {});
	}

	/**
	 * Create a new instance of the class named by the string argument and
	 * return it as the type specified by the class argument. This method is
	 * intended to be used to construct an implementor of an interface and
	 * return it as the specified interface type. If you simply want to
	 * construct a class and handle it as an instance of it's natural class,
	 * please use the <code>newInstance(Class)</code> method.
	 * 
	 * @param <T>
	 *            The return type.
	 * @param assignableType
	 *            the type to cast the instance to
	 * @param className
	 *            the fully qualified name of the class from which the object
	 *            should be constructed, which should be a subclass or
	 *            implementor of the <code>assignableType</code> argument.
	 * @param constructorArguments
	 *            the arguments to the constructor, the objects in the array
	 *            CANNOT be null
	 * @return the newly constructed instance of type T.
	 * @throws ReflectionException
	 *             when the class cannot be loaded, does not have a default
	 *             constructor, cannot be instantiated due to a security
	 *             constraint, or is not a subclass or implementor of the Class
	 *             argument.
	 */
	public static <T> T newInstanceOfAssignableType(Class<T> assignableType,
			String className, Object... constructorArguments)
			throws ReflectionException {
		T result;

		Class<?> loadedClass = forName(className);
		if (!assignableType.isAssignableFrom(loadedClass)) {
			throw new ReflectionException(
					new IllegalArgumentException(
							"Class named "
									+ loadedClass.getName()
									+ " is not a subclass or implementor of the specified type: "
									+ assignableType.getName()));
		}

		// Look up a constructor accepting the arguments
		Class<?>[] constructorParameters = new Class<?>[constructorArguments.length];

		for (int i = 0; i < constructorArguments.length; i++) {
			Object obj = constructorArguments[i];

			if (obj == null) {
				throw new IllegalArgumentException(
						"Cannot pass null arguments using this utility method, "
								+ "all arguments must have a valid object assigned to them");
			}

			constructorParameters[i] = obj.getClass();
		}

		Constructor<?> constructor = ReflectionUtils.getConstructor(
				loadedClass, constructorParameters);

		Object instance = newInstance(constructor, constructorArguments);
		result = assignableType.cast(instance);

		return result;
	}

	public static <T> T getField(Class<?> clazzWithField, Object obj,
			String fieldName, Class<T> returnType) {
		try {
			Field field = clazzWithField.getDeclaredField(fieldName);
			field.setAccessible(true);
			return returnType.cast(field.get(obj));
		} catch (Exception e) {
			throw new ReflectionException(e);
		}
	}
}
