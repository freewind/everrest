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
package org.everrest.core.impl.provider.json;

import junit.framework.TestCase;

/**
 * @author <a href="andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public abstract class JsonTest extends TestCase {

    protected Book junitBook;

    protected Book csharpBook;

    protected Book javaScriptBook;

    protected void setUp() throws Exception {
        super.setUp();

        junitBook = new Book();
        junitBook.setAuthor("Vincent Masson");
        junitBook.setTitle("JUnit in Action");
        junitBook.setPages(386);
        junitBook.setPrice(19.37);
        junitBook.setIsdn(93011099534534L);
        junitBook.setAvailability(false);
        junitBook.setDelivery(false);

        csharpBook = new Book();
        csharpBook.setAuthor("Christian Gross");
        csharpBook.setTitle("Beginning C# 2008 from novice to professional");
        csharpBook.setPages(511);
        csharpBook.setPrice(23.56);
        csharpBook.setIsdn(9781590598696L);
        csharpBook.setAvailability(false);
        csharpBook.setDelivery(false);

        javaScriptBook = new Book();
        javaScriptBook.setAuthor("Chuck Easttom");
        javaScriptBook.setTitle("Advanced JavaScript. Third Edition");
        javaScriptBook.setPages(617);
        javaScriptBook.setPrice(25.99);
        javaScriptBook.setIsdn(9781598220339L);
        javaScriptBook.setAvailability(false);
        javaScriptBook.setDelivery(false);
    }

}
