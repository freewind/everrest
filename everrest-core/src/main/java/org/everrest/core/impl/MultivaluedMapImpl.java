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
package org.everrest.core.impl;

import org.everrest.core.ExtMultivaluedMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 * @see javax.ws.rs.core.MultivaluedMap
 */
public class MultivaluedMapImpl extends HashMap<String, List<String>> implements ExtMultivaluedMap<String, String> {
    private static final long serialVersionUID = -6066678602537059655L;

    /** {@inheritDoc} */
    public void add(String key, String value) {
        if (value == null) {
            return;
        }
        List<String> list = getList(key);
        list.add(value);
    }

    /** {@inheritDoc} */
    public String getFirst(String key) {
        List<String> list = get(key);
        return list != null && list.size() > 0 ? list.get(0) : null;
    }

    /** {@inheritDoc} */
    public void putSingle(String key, String value) {
        if (value == null) {
            remove(key);
            return;
        }
        List<String> list = getList(key);
        list.clear();
        list.add(value);
    }

    /** {@inheritDoc} */
    public List<String> getList(String key) {
        List<String> list = get(key);
        if (list == null) {
            list = new ArrayList<String>();
            put(key, list);
        }
        return list;
    }
}
