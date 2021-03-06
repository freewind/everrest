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
package org.everrest.core.resource;

import org.everrest.core.method.MethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Abstraction for method in resource, this essence is common for
 * {@link ResourceMethodDescriptor}, {@link SubResourceMethodDescriptor},
 * {@link SubResourceLocatorDescriptor} .
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public interface GenericMethodResource {

    /** @return See {@link Method} */
    Method getMethod();

    /** @return List of method parameters */
    List<MethodParameter> getMethodParameters();

    /** @return parent resource descriptor */
    AbstractResourceDescriptor getParentResource();

    /** @return Java type returned by method, see {@link #getMethod()} */
    Class<?> getResponseType();

    /**
     * Get set or additional (not JAX-RS specific) annotation. Set of annotations
     * in implementation specific and it is not guaranteed this method will
     * return all annotations applied to the method.
     *
     * @return addition annotation
     */
    Annotation[] getAnnotations();
}
