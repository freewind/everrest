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
package org.everrest.core.impl.provider;

import org.everrest.core.impl.BaseTest;
import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.provider.EntityProvider;
import org.everrest.test.mock.MockHttpServletRequest;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * @author andrew00x
 */
public class ProviderContextParameterInjectionTest extends BaseTest {

    public static class MockEntity {
        String entity;
    }

    @Provider
    public static class EntityProviderChecker implements EntityProvider<MockEntity> {
        @Context
        private UriInfo            uriInfo;
        @Context
        private Request            request;
        @Context
        private HttpHeaders        httpHeaders;
        @Context
        private Providers          providers;
        @Context
        private HttpServletRequest httpRequest;

        // EntityProvider can be used for reading/writing ONLY if all fields above initialized

        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == MockEntity.class && uriInfo != null && request != null && httpHeaders != null
                   && providers != null && httpRequest != null;
        }

        public MockEntity readFrom(Class<MockEntity> type, Type genericType, Annotation[] annotations,
                                   MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
                throws IOException, WebApplicationException {
            MockEntity me = new MockEntity();
            me.entity = IOHelper.readString(entityStream, IOHelper.DEFAULT_CHARSET_NAME);
            return me;
        }

        public long getSize(MockEntity t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return 0;
        }

        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == MockEntity.class && uriInfo != null && request != null && httpHeaders != null
                   && providers != null && httpRequest != null;
        }

        public void writeTo(MockEntity t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
                throws IOException, WebApplicationException {
            IOHelper.writeString(t.entity, entityStream, IOHelper.DEFAULT_CHARSET_NAME);
        }
    }

    @Provider
    public static class ExceptionMapperChecker implements ExceptionMapper<RuntimeException> {
        @Context
        private UriInfo            uriInfo;
        @Context
        private Request            request;
        @Context
        private HttpHeaders        httpHeaders;
        @Context
        private Providers          providers;
        @Context
        private HttpServletRequest httpRequest;

        public Response toResponse(RuntimeException exception) {
            if (uriInfo != null && request != null && httpHeaders != null && providers != null && httpRequest != null) {
                return Response.status(200).build();
            }
            return Response.status(500).build();
        }
    }

    @Provider
    @Produces("text/plain")
    public static class ContextResolverChecker implements ContextResolver<String> {
        @Context
        private UriInfo            uriInfo;
        @Context
        private Request            request;
        @Context
        private HttpHeaders        httpHeaders;
        @Context
        private Providers          providers;
        @Context
        private HttpServletRequest httpRequest;

        public String getContext(Class<?> type) {
            if (uriInfo != null && request != null && httpHeaders != null && providers != null && httpRequest != null) {
                return "to be to not to be";
            }
            return null;
        }

    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        processor.addApplication(new Application() {
            @Override
            public Set<Class<?>> getClasses() {
                Set<Class<?>> classes = new LinkedHashSet<>();
                classes.add(EntityProviderChecker.class);
                classes.add(ExceptionMapperChecker.class);
                classes.add(ContextResolverChecker.class);
                classes.add(Resource1.class);
                return classes;
            }
        });
    }

    public void tearDown() throws Exception {
        super.tearDown();
    }

    @Path("a")
    public static class Resource1 {

        @Context
        private Providers providers;

        @GET
        @Path("1")
        public MockEntity m0(MockEntity me) {
            Assert.assertNotNull(me);
            Assert.assertEquals("to be or not to be", me.entity);
            me.entity = "to be";
            return me;
        }

        @GET
        @Path("2")
        public void m1() {
            throw new RuntimeException();
        }

        @GET
        @Path("3")
        public String m2() {
            ContextResolver<String> r = providers.getContextResolver(String.class, new MediaType("text", "plain"));
            return r.getContext(String.class);
        }
    }

    @Test
    public void testParameterInjection() throws Exception {
        EnvironmentContext env = new EnvironmentContext();
        env.put(HttpServletRequest.class, new MockHttpServletRequest("", new ByteArrayInputStream(new byte[0]), 0, "GET",
                                                                     new HashMap<String, List<String>>()));
        ContainerResponse resp = launcher.service("GET", "/a/1", "", null, "to be or not to be".getBytes(), env);
        Assert.assertEquals("to be", ((MockEntity)resp.getEntity()).entity);

        resp = launcher.service("GET", "/a/2", "", null, null, env);
        Assert.assertEquals(200, resp.getStatus());

        resp = launcher.service("GET", "/a/3", "", null, null, env);
        Assert.assertEquals(200, resp.getStatus());
        Assert.assertEquals("to be to not to be", resp.getEntity());
    }

}
