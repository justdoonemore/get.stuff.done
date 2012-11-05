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
package com.jdom.util.patterns.mvc;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.jdom.util.reflection.ReflectionException;
import com.jdom.util.reflection.ReflectionUtils;

public final class MvcLocator {

    private static final Map<String, Object> STORE = new ConcurrentHashMap<String, Object>();

    private static final ImplLocator[] locators = new ImplLocator[] { new ImplInSamePackageLocator(),
            new ImplPackageLocator(), new StaticInnerClassLocator() };

    public static <I> I getImpl(Class<I> interfaceClass) {
        return getImplementation(interfaceClass, false, new Object[0]);
    }

    public static <I> I getImpl(Class<I> interfaceClass, Object... constructorArguments) {
        return getImplementation(interfaceClass, false, constructorArguments);
    }

    public static <I> I getNewImpl(Class<I> interfaceClass) {

        return getImplementation(interfaceClass, true, new Object[0]);
    }

    public static <I> I getNewImpl(Class<I> interfaceClass, Object... constructorArguments) {

        return getImplementation(interfaceClass, true, constructorArguments);
    }

    private static <I> I getImplementation(Class<I> interfaceClass, boolean forceNew, Object[] constructorArguments) {

        I retVal = null;

        String interfaceName = interfaceClass.getName();

        Object obj = STORE.get(interfaceName);

        if (obj == null || forceNew) {

            synchronized (STORE) {

                // Check for null again now that we've synchronized
                if (obj == null || forceNew) {

                    Class<?> clazz = null;

                    try {

                        for (ImplLocator locator : locators) {
                            clazz = locator.locateImpl(interfaceClass);

                            if (clazz != null) {
                                break;
                            }
                        }

                        // If we couldn't find an implementation
                        if (clazz == null) {
                            throw new MvcLocatorException("Unable to find implementation for interface ["
                                    + interfaceName + "]");
                        }

                        // Or it doesn't implement the interface
                        if (!interfaceClass.isAssignableFrom(clazz)) {
                            throw new MvcLocatorException("[" + clazz.getName() + "] is not an implementer for ["
                                    + interfaceName + "]");
                        }

                        obj = constructImpl(clazz, constructorArguments);

                        STORE.put(interfaceName, obj);
                    } catch (ReflectionException e) {
                        throw new MvcLocatorException("Unable to instantiate class [" + clazz.getName() + "]", e);
                    }
                }
            }
        }

        try {
            retVal = interfaceClass.cast(obj);
        } catch (ClassCastException cce) {
            throw new MvcLocatorException("SERIOUS ERROR! Class [" + obj.getClass().getName()
                    + "] is not an instance of [" + interfaceName + "].  This should NEVER happen!", cce);
        }

        return retVal;
    }

    static void clearLookups() {
        synchronized (STORE) {
            STORE.clear();
        }
    }

    private static Class<?>[] getConstructorArgumentTypes(Object[] constructorArguments) {
        Class<?>[] constructorArgumentTypes = new Class[constructorArguments.length];

        for (int i = 0; i < constructorArguments.length; i++) {
            constructorArgumentTypes[i] = constructorArguments[i].getClass();

            // Check all interfaces first, for one to end without
            Class<?> mvcInterface = checkForMvcInterface(constructorArgumentTypes[i], constructorArgumentTypes[i]
                    .getInterfaces());

            if (mvcInterface != null) {
                constructorArgumentTypes[i] = mvcInterface;
                continue;
            }

            Class<?> mvcSuperclass = checkForMvcInterface(constructorArgumentTypes[i], constructorArgumentTypes[i]
                    .getSuperclass());

            if (mvcSuperclass != null) {
                constructorArgumentTypes[i] = mvcSuperclass;
                continue;
            }
        }

        return constructorArgumentTypes;
    }

    private static Class<?> checkForMvcInterface(Class<?> possibleImplClass,
            Class<?>... possibleInterfacesOrAbstractClasses) {
        if (possibleInterfacesOrAbstractClasses != null && possibleInterfacesOrAbstractClasses.length > 0) {
            for (Class<?> interfaze : possibleInterfacesOrAbstractClasses) {
                boolean amImpl = checkToBeImpl(possibleImplClass, possibleInterfacesOrAbstractClasses[0]);

                if (amImpl) {
                    return interfaze;
                }
            }
        }

        return null;
    }

    private static Object constructImpl(Class<?> clazz, Object[] constructorArguments) {

        Class<?>[] constructorArgumentTypes = getConstructorArgumentTypes(constructorArguments);

        Constructor<?> constructor = ReflectionUtils.getConstructor(clazz, constructorArgumentTypes);

        return ReflectionUtils.newInstance(constructor, constructorArguments);
    }

    private static boolean checkToBeImpl(Class<?> possibleImplClass, Class<?> possibleInterface) {

        boolean impl = false;

        String interfacePackage = possibleInterface.getPackage().getName();
        String possibleInterfaceImplPackage = interfacePackage + ".impl";
        String classPackage = possibleImplClass.getPackage().getName();

        boolean inSameOrImplPackage = interfacePackage.equals(classPackage)
                || possibleInterfaceImplPackage.equals(classPackage);

        if (inSameOrImplPackage) {
            impl = (possibleInterface.getSimpleName() + "Impl").equals(possibleImplClass.getSimpleName());
        }

        return impl;
    }

}
