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

package org.everrest.spring;

import org.everrest.core.DependencySupplier;
import org.everrest.core.ResourceBinder;
import org.everrest.core.impl.ApplicationProviderBinder;
import org.everrest.core.impl.EverrestConfiguration;
import org.everrest.core.impl.EverrestProcessor;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * HandlerMapping for EverrestProcessor.
 * 
 * @version $Id$
 * @deprecated Do not use it any more. SpringComponentsLoader defines a mapping for EverrestProcessor.
 */
public class EverrestHandlerMapping implements HandlerMapping
{
   protected EverrestProcessor processor;
   protected ResourceBinder resources;
   protected ApplicationProviderBinder providers;
   protected EverrestConfiguration configuration;

   protected EverrestHandlerMapping()
   {
   }

   public EverrestHandlerMapping(ResourceBinder resources, ApplicationProviderBinder providers,
      DependencySupplier dependencies)
   {
      this(resources, providers, new EverrestConfiguration(), dependencies);
   }

   public EverrestHandlerMapping(ResourceBinder resources, ApplicationProviderBinder providers,
      EverrestConfiguration configuration, DependencySupplier dependencies)
   {
      this.resources = resources;
      this.providers = providers;
      this.configuration = configuration;
      this.processor = new EverrestProcessor(resources, providers, dependencies, configuration, null);
   }

   /**
    * {@inheritDoc}
    */
   public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception
   {
      return new HandlerExecutionChain(getProcessor());
   }

   protected EverrestProcessor getProcessor()
   {
      return processor;
   }
}
