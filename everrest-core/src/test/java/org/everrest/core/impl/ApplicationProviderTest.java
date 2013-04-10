/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.everrest.core.impl;

import org.everrest.core.impl.provider.IOHelper;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ApplicationProviderTest extends BaseTest {

    // Register this provider in ApplicationProvider , it should 'override' default String provider.
    @Provider
    public static class StringInvertor implements MessageBodyWriter<String>, MessageBodyReader<String> {

        public long getSize(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return -1;
        }

        public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == String.class;
        }

        public void writeTo(String t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                            MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException,
                                                                                                          WebApplicationException {
            IOHelper.writeString(new StringBuilder(t).reverse().toString(), entityStream, null);
        }

        public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
            return type == String.class;
        }

        public String readFrom(Class<String> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                               MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException,
                                                                                                            WebApplicationException {
            return new StringBuilder(IOHelper.readString(entityStream, null)).reverse().toString();
        }

    }

    @Path("/")
    public static class Resource1 {
        @POST
        public String m(String s) {
            assertEquals(invertedMessage, s);
            return s;
        }
    }

    private static final String message = "to be or not to be";

    private static final String invertedMessage = new StringBuilder(message).reverse().toString();

    public void setUp() throws Exception {
        super.setUp();
        providers.addMessageBodyWriter(StringInvertor.class);
    }

    public void testApplicationProvider() throws Exception {
        ApplicationPublisher deployer = new ApplicationPublisher(resources, providers);
        deployer.publish(new Application() {
            Set<Class<?>> classes = new HashSet<Class<?>>();

            Set<Object> instances = new HashSet<Object>();

            {
                classes.add(Resource1.class);
                instances.add(new StringInvertor());
            }

            public Set<Class<?>> getClasses() {
                return classes;
            }

            public Set<Object> getSingletons() {
                return instances;
            }
        });

        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        ContainerResponse response = launcher.service("POST", "/", "", null, message.getBytes(), writer, null);
        assertEquals(200, response.getStatus());
        // After twice reversing response string must be primordial
        assertEquals(message, new String(writer.getBody()));
    }

}
