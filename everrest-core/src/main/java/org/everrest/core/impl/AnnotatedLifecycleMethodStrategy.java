/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.everrest.core.impl;

import org.everrest.core.LifecycleMethodStrategy;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Implementation of LifecycleComponent.LifecycleMethodStrategy that uses {@link PostConstruct} and {@link PreDestroy}
 * annotation to find "initialize" and "destroy" methods.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class AnnotatedLifecycleMethodStrategy implements LifecycleMethodStrategy {
    private static class MethodFilter {
        private final Class<? extends Annotation> annotation;

        MethodFilter(Class<? extends Annotation> annotation) {
            this.annotation = annotation;
        }

        /**
         * Check is method may be used as PostConstruct/PreDestroy method. There are some limitations according to
         * requirements usage of {@link PostConstruct} and {@link PreDestroy} annotation :
         * <ul>
         * <li>Method is annotated with {@link #annotation}.</li>
         * <li>Method must not be static.</li>
         * <li>Method has not have any parameters.</li>
         * <li>The return type of the method must be void.</li>
         * <li>Method must not throw checked exception.</li>
         * </ul>
         *
         * @param m
         *         the method
         * @return <code>true</code> if method is matched to requirements above and false otherwise
         * @see PostConstruct
         * @see PreDestroy
         */
        boolean accept(Method m) {
            return (!Modifier.isStatic(m.getModifiers())) //
                   && (m.getReturnType() == void.class || m.getReturnType() == Void.class) //
                   && m.getParameterTypes().length == 0 //
                   && noCheckedException(m)
                   && m.getAnnotation(annotation) != null;
        }

        private boolean noCheckedException(Method m) {
            Class<?>[] exceptions = m.getExceptionTypes();
            if (exceptions.length > 0) {
                for (int i = 0; i < exceptions.length; i++) {
                    if (!RuntimeException.class.isAssignableFrom(exceptions[i])) {
                        return false;
                    }
                }
            }
            return true;
        }
    }

    private static final MethodFilter POST_CONSTRUCT_METHOD_FILTER = new MethodFilter(PostConstruct.class);
    private static final MethodFilter PRE_DESTROY_METHOD_FILTER    = new MethodFilter(PreDestroy.class);

    private final HelperCache<Class<?>, Method[]> initializeMethodsCache =
            new HelperCache<Class<?>, Method[]>(60 * 1000, 250);
    private final HelperCache<Class<?>, Method[]> destroyMethodsCache    =
            new HelperCache<Class<?>, Method[]>(60 * 1000, 250);

    /** @see LifecycleMethodStrategy#invokeInitializeMethods(java.lang.Object) */
    @Override
    public void invokeInitializeMethods(Object o) {
        final Class<?> clazz = o.getClass();
        Method[] initMethods;
        synchronized (initializeMethodsCache) {
            initMethods = initializeMethodsCache.get(clazz);
            if (initMethods == null) {
                initMethods = getLifecycleMethods(clazz, POST_CONSTRUCT_METHOD_FILTER);
                initializeMethodsCache.put(clazz, initMethods);
            }
        }
        if (initMethods.length > 0) {
            doInvokeLifecycleMethods(o, initMethods);
        }
    }

    /** @see LifecycleMethodStrategy#invokeDestroyMethods(java.lang.Object) */
    @Override
    public void invokeDestroyMethods(Object o) {
        final Class<?> clazz = o.getClass();
        Method[] destroyMethods;
        synchronized (destroyMethodsCache) {
            destroyMethods = destroyMethodsCache.get(clazz);
            if (destroyMethods == null) {
                destroyMethods = getLifecycleMethods(clazz, PRE_DESTROY_METHOD_FILTER);
                destroyMethodsCache.put(clazz, destroyMethods);
            }
        }
        if (destroyMethods.length > 0) {
            doInvokeLifecycleMethods(o, destroyMethods);
        }
    }

    private Method[] getLifecycleMethods(Class<?> cl, MethodFilter filter) {
        try {
            LinkedList<Method> result = new LinkedList<Method>();
            Set<String> names = new HashSet<String>();
            for (; cl != Object.class; cl = cl.getSuperclass()) {
                Method[] methods = cl.getDeclaredMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (filter.accept(method) && names.add(method.getName())) {
                        if (!Modifier.isPublic(method.getModifiers())) {
                            method.setAccessible(true);
                        }
                        result.add(method);
                    }
                }
            }
            return result.toArray(new Method[result.size()]);
        } catch (SecurityException e) {
            throw new InternalException(e);
        }
    }

    private void doInvokeLifecycleMethods(Object o, Method[] lifecycleMethods) {
        for (Method method : lifecycleMethods) {
            try {
                method.invoke(o);
            } catch (InvocationTargetException e) {
                Throwable t = e.getTargetException();
                throw new InternalException(t);
            } catch (Exception e) {
                throw new InternalException(e);
            }
        }
    }
}
