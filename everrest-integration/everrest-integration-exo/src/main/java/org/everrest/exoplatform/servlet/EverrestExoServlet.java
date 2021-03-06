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
package org.everrest.exoplatform.servlet;

import org.everrest.core.RequestHandler;
import org.everrest.core.UnhandledException;
import org.everrest.core.impl.ContainerResponse;
import org.everrest.core.impl.EnvironmentContext;
import org.everrest.core.servlet.ServletContainerRequest;
import org.everrest.core.servlet.ServletContainerResponseWriter;
import org.everrest.core.tools.WebApplicationDeclaredRoles;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.component.RequestLifeCycle;
import org.exoplatform.container.web.AbstractHttpServlet;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Use this servlet for integration in eXo environment. If just need to use IoC container then
 * use org.everrest.core.servlet.EverrestServlet. Usage of this servlet assumes that components of EverRest framework
 * registered in ExoContainer. To do so do not use EverrestExoContextListener as bootstrap but use
 * org.everrest.exoplatform.EverrestInitializer instead and provide corresponded configuration for ExoContainer.
 *
 * @author andrew00x
 */
@SuppressWarnings("serial")
public class EverrestExoServlet extends AbstractHttpServlet {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(EverrestExoServlet.class);
    private WebApplicationDeclaredRoles webApplicationRoles;

    @Override
    protected void afterInit(ServletConfig config) throws ServletException {
        webApplicationRoles = new WebApplicationDeclaredRoles(getServletContext());
    }

    @Override
    protected void onService(ExoContainer container, HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        RequestLifeCycle.begin(container);

        RequestHandler requestHandler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);

        EnvironmentContext env = new EnvironmentContext();
        env.put(HttpServletRequest.class, req);
        env.put(HttpServletResponse.class, res);
        env.put(ServletConfig.class, config);
        env.put(ServletContext.class, getServletContext());
        env.put(WebApplicationDeclaredRoles.class, webApplicationRoles);

        try {
            EnvironmentContext.setCurrent(env);
            ServletContainerRequest request = ServletContainerRequest.create(req);
            ContainerResponse response = new ContainerResponse(new ServletContainerResponseWriter(res));
            requestHandler.handleRequest(request, response);
        } catch (IOException ioe) {
            // Met problem with Acrobat Reader HTTP client when use EverRest for WebDav.
            // Client close connection before all data transferred and it cause error on server side.
            if (ioe.getClass().getName().equals("org.apache.catalina.connector.ClientAbortException")) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(ioe.getMessage(), ioe);
                }
            } else {
                throw ioe;
            }
        } catch (UnhandledException e) {
            throw new ServletException(e);
        } finally {
            EnvironmentContext.setCurrent(null);
            RequestLifeCycle.end();
        }
    }
}
