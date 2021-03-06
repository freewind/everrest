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
package org.everrest.core.impl.method;

import org.everrest.core.ApplicationContext;
import org.everrest.core.method.TypeProducer;

import javax.ws.rs.QueryParam;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: QueryParameterResolver.java 285 2009-10-15 16:21:30Z aparfonov
 *          $
 */
public class QueryParameterResolver extends ParameterResolver<QueryParam> {
    /** See {@link QueryParam}. */
    private final QueryParam queryParam;

    /**
     * @param queryParam
     *         QueryParam
     */
    QueryParameterResolver(QueryParam queryParam) {
        this.queryParam = queryParam;
    }


    @Override
    public Object resolve(org.everrest.core.Parameter parameter, ApplicationContext context) throws Exception {
        String param = this.queryParam.value();
        TypeProducer typeProducer =
                ParameterHelper.createTypeProducer(parameter.getParameterClass(), parameter.getGenericType());
        return typeProducer.createValue(param, context.getQueryParameters(!parameter.isEncoded()), parameter
                .getDefaultValue());
    }
}
