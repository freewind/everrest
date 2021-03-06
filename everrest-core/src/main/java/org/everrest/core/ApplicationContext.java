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
package org.everrest.core;

import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.ProviderBinder;
import org.everrest.core.method.MethodInvoker;
import org.everrest.core.resource.GenericMethodResource;
import org.everrest.core.uri.UriPattern;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;
import java.util.List;
import java.util.Map;

/**
 * Provides access to ContainerRequest, ContainerResponse and request URI information.
 *
 * @author andrew00x
 */
public interface ApplicationContext extends UriInfo, InitialProperties {
    /**
     * Add ancestor resource, according to JSR-311:
     * <p>
     * Entries are ordered according in reverse request URI matching order, with the root resource last.
     * </p>
     * So add each new resource at the begin of list.
     *
     * @param resource
     *         the resource e. g. resource class, sub-resource method or sub-resource locator.
     */
    void addMatchedResource(Object resource);

    /**
     * Add ancestor resource, according to JSR-311:
     * <p>
     * Entries are ordered in reverse request URI matching order, with the root resource URI last.
     * </p>
     * So add each new URI at the begin of list.
     *
     * @param uri
     *         the partial part of that matched to resource class, sub-resource method or sub-resource locator.
     */
    void addMatchedURI(String uri);

    /** @return get mutable runtime attributes */
    Map<String, Object> getAttributes();

    /** @return See {@link GenericContainerRequest} */
    GenericContainerRequest getContainerRequest();

    /** @return See {@link GenericContainerResponse} */
    GenericContainerResponse getContainerResponse();

    /** @return See {@link DependencySupplier} */
    DependencySupplier getDependencySupplier();

    /**
     * @param depInjector
     *         DependencySupplier
     */
    void setDependencySupplier(DependencySupplier depInjector);

    /** @return See {@link HttpHeaders} */
    HttpHeaders getHttpHeaders();

    /** @return {@link InitialProperties} */
    InitialProperties getInitialProperties();

    /**
     * @param methodDescriptor
     *         method descriptor
     * @return invoker that must be used for processing methods
     */
    MethodInvoker getMethodInvoker(GenericMethodResource methodDescriptor);

    /**
     * Should be used to pass template values in context by using returned list in matching to @see
     * {@link org.everrest.core.uri.UriPattern#match(String, List)}. List will be cleared during matching.
     *
     * @return the list for template values
     */
    List<String> getParameterValues();

    /**
     * Pass in context list of path template parameters @see {@link UriPattern}.
     *
     * @param parameterNames
     *         list of templates parameters
     */
    void setParameterNames(List<String> parameterNames);

    /**
     * @return {@link ProviderBinder}
     * @see Providers
     */
    ProviderBinder getProviders();

    /**
     * @param providers
     *         ProviderBinder
     */
    void setProviders(ProviderBinder providers);

    /** @return See {@link Request} */
    Request getRequest();

    /** @return See {@link SecurityContext} */
    SecurityContext getSecurityContext();

    /** @return See {@link UriInfo} */
    UriInfo getUriInfo();

    /** @return <code>true</code> if request is asynchronous and <code>false</code> otherwise, */
    boolean isAsynchronous();

    Application getApplication();

    EverrestConfiguration getEverrestConfiguration();
}
