/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.everrest.core.impl.provider;

import org.everrest.core.BaseObjectModel;
import org.everrest.core.ComponentLifecycleScope;
import org.everrest.core.impl.header.MediaTypeHelper;
import org.everrest.core.provider.ProviderDescriptor;
import org.everrest.core.resource.ResourceDescriptorVisitor;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: ProviderDescriptorImpl.java 292 2009-10-19 07:03:07Z aparfonov
 *          $
 */
public class ProviderDescriptorImpl extends BaseObjectModel implements ProviderDescriptor
{

   /**
    * List of media types which this method can consume. See
    * {@link javax.ws.rs.Consumes} .
    */
   private final List<MediaType> consumes;

   /**
    * List of media types which this method can produce. See
    * {@link javax.ws.rs.Produces} .
    */
   private final List<MediaType> produces;

   /**
    * @param providerClass provider class
    * @param scope provider scope
    */
   public ProviderDescriptorImpl(Class<?> providerClass, ComponentLifecycleScope scope)
   {
      super(providerClass, scope);
      this.consumes = MediaTypeHelper.createConsumesList(providerClass.getAnnotation(Consumes.class));
      this.produces = MediaTypeHelper.createProducesList(providerClass.getAnnotation(Produces.class));
   }

   /**
    * {@inheritDoc}
    */
   public void accept(ResourceDescriptorVisitor visitor)
   {
      visitor.visitProviderDescriptor(this);
   }

   /**
    * {@inheritDoc}
    */
   public List<MediaType> consumes()
   {
      return consumes;
   }

   /**
    * {@inheritDoc}
    */
   public List<MediaType> produces()
   {
      return produces;
   }

   /**
    * {@inheritDoc}
    */
   public String toString()
   {
      StringBuilder sb = new StringBuilder("[ ProviderDescriptorImpl: ");
      sb.append("provider class: " + getObjectClass() + "; ").append("produces media type: " + produces() + "; ")
         .append("consumes media type: " + consumes() + "; ").append(getConstructorDescriptors() + "; ").append(
            getFieldInjectors()).append(" ]");
      return sb.toString();

   }

}
