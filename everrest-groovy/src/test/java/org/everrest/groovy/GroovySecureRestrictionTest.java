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
package org.everrest.groovy;

import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.tools.ByteArrayContainerResponseWriter;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

/**
 * @author andrew00x
 */
public class GroovySecureRestrictionTest extends BaseTest {

    private InputStream script;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        Assert.assertNotNull("SecurityManager not installed", System.getSecurityManager());
        script = Thread.currentThread().getContextClassLoader().getResourceAsStream("a/b/GroovyResource3.groovy");
        Assert.assertNotNull(script);
    }

    @Test
    public void testReadSystemPropertyFail() throws Exception {
        groovyPublisher.publishPerRequest(script, new BaseResourceId("g1"), null, null, null);
        ByteArrayContainerResponseWriter writer = new ByteArrayContainerResponseWriter();
        ContainerResponse resp = launcher.service("GET", "/a/b", "", null, null, writer, null);
        Assert.assertEquals(500, resp.getStatus());
        Assert.assertTrue(new String(writer.getBody()).startsWith("access denied"));
    }
}
