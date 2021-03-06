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

import org.everrest.core.ApplicationContext;
import org.everrest.core.impl.ApplicationContextImpl;
import org.everrest.core.provider.EntityProvider;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

/**
 * @author andrew00x
 */
@Provider
public class ReaderEntityProvider implements EntityProvider<Reader> {

    @Override
    public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return type == Reader.class;
    }


    @Override
    public Reader readFrom(Class<Reader> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                           MultivaluedMap<String, String> httpHeaders, InputStream entityStream) throws IOException {
        String cs = mediaType != null ? mediaType.getParameters().get("charset") : null;
        Charset charset = cs != null ? Charset.forName(cs) : IOHelper.DEFAULT_CHARSET;

        ApplicationContext context = ApplicationContextImpl.getCurrent();
        if (context.isAsynchronous()) {
            // If request is asynchronous spool content of stream to file or memory.
            int bufferSize = context.getEverrestConfiguration().getMaxBufferSize();
            return new InputStreamReader(IOHelper.bufferStream(entityStream, bufferSize), charset);
        }

        return new InputStreamReader(entityStream, charset);
    }


    @Override
    public long getSize(Reader t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }


    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Reader.class.isAssignableFrom(type);
    }


    @Override
    public void writeTo(Reader t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
        Writer out = new OutputStreamWriter(entityStream);
        try {
            IOHelper.write(t, out);
        } finally {
            out.flush();
            t.close();
        }
    }

}
