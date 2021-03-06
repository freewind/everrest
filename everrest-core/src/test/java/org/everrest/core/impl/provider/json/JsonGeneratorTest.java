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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class JsonGeneratorTest extends JsonTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testBean() throws Exception {
        JsonValue jsonValue = JsonGenerator.createJsonObject(junitBook);
        assertTrue(jsonValue.isObject());
        assertEquals(junitBook.getAuthor(), jsonValue.getElement("author").getStringValue());
        assertEquals(junitBook.getTitle(), jsonValue.getElement("title").getStringValue());
        assertEquals(junitBook.getPages(), jsonValue.getElement("pages").getIntValue());
        assertEquals(junitBook.getPrice(), jsonValue.getElement("price").getDoubleValue());
        assertEquals(junitBook.getIsdn(), jsonValue.getElement("isdn").getLongValue());
        assertEquals(junitBook.getDelivery(), jsonValue.getElement("delivery").getBooleanValue());
        assertEquals(junitBook.isAvailability(), jsonValue.getElement("availability").getBooleanValue());
    }

    public void testArray() throws Exception {
        Book[] a = new Book[]{junitBook, csharpBook, javaScriptBook};
        JsonValue jsonValue = JsonGenerator.createJsonArray(a);
        assertTrue(jsonValue.isArray());
        Iterator<JsonValue> iterator = jsonValue.getElements();
        assertEquals(a[0].getTitle(), iterator.next().getElement("title").getStringValue());
        assertEquals(a[1].getTitle(), iterator.next().getElement("title").getStringValue());
        assertEquals(a[2].getTitle(), iterator.next().getElement("title").getStringValue());
        //System.out.println(jsonValue);
    }

    public void testArrayNull() throws Exception {
        Book[] a = null;
        JsonValue jsonValue = JsonGenerator.createJsonArray(a);
        assertTrue(jsonValue.isNull());
    }

    public void testCollection() throws Exception {
        List<Book> l = Arrays.asList(junitBook, csharpBook, javaScriptBook);
        JsonValue jsonValue = JsonGenerator.createJsonArray(l);
        assertTrue(jsonValue.isArray());
        Iterator<JsonValue> iterator = jsonValue.getElements();
        assertEquals(l.get(0).getTitle(), iterator.next().getElement("title").getStringValue());
        assertEquals(l.get(1).getTitle(), iterator.next().getElement("title").getStringValue());
        assertEquals(l.get(2).getTitle(), iterator.next().getElement("title").getStringValue());
        //System.out.println(jsonValue);
    }

    public void testCollectionNull() throws Exception {
        List<Book> l = null;
        JsonValue jsonValue = JsonGenerator.createJsonArray(l);
        assertTrue(jsonValue.isNull());
    }

    public void testMap() throws Exception {
        Map<String, Book> m = new HashMap<String, Book>();
        m.put("junit", junitBook);
        m.put("csharp", csharpBook);
        m.put("js", javaScriptBook);
        JsonValue jsonValue = JsonGenerator.createJsonObjectFromMap(m);
        assertTrue(jsonValue.isObject());
        assertEquals(junitBook.getTitle(), jsonValue.getElement("junit").getElement("title").getStringValue());
        assertEquals(csharpBook.getTitle(), jsonValue.getElement("csharp").getElement("title").getStringValue());
        assertEquals(javaScriptBook.getTitle(), jsonValue.getElement("js").getElement("title").getStringValue());
        //System.out.println(jsonValue);
    }

    public void testMapNull() throws Exception {
        Map<String, Book> m = null;
        JsonValue jsonValue = JsonGenerator.createJsonObjectFromMap(m);
        assertTrue(jsonValue.isNull());
    }

    public void testBeanWrapper() throws Exception {
        BookWrapper bookWrapper = new BookWrapper();
        bookWrapper.setBook(junitBook);
        JsonValue jsonValue = JsonGenerator.createJsonObject(bookWrapper);
        assertTrue(jsonValue.isObject());
        assertEquals(junitBook.getAuthor(), jsonValue.getElement("book").getElement("author").getStringValue());
        assertEquals(junitBook.getTitle(), jsonValue.getElement("book").getElement("title").getStringValue());
        assertEquals(junitBook.getPages(), jsonValue.getElement("book").getElement("pages").getIntValue());
        assertEquals(junitBook.getPrice(), jsonValue.getElement("book").getElement("price").getDoubleValue());
        assertEquals(junitBook.getIsdn(), jsonValue.getElement("book").getElement("isdn").getLongValue());
        assertEquals(junitBook.getDelivery(), jsonValue.getElement("book").getElement("delivery").getBooleanValue());
        assertEquals(junitBook.isAvailability(), jsonValue.getElement("book").getElement("availability")
                                                          .getBooleanValue());
    }

    public void testBeanCollection() throws Exception {
        List<Book> l = new ArrayList<Book>();
        l.add(junitBook);
        l.add(csharpBook);
        l.add(javaScriptBook);
        BookStorage bookStorage = new BookStorage();
        bookStorage.setBooks(l);

        JsonValue jsonValue = JsonGenerator.createJsonObject(bookStorage);
        assertTrue(jsonValue.isObject());
        Iterator<JsonValue> iterator = jsonValue.getElement("books").getElements();
        assertEquals(l.get(0).getTitle(), iterator.next().getElement("title").getStringValue());
        assertEquals(l.get(1).getTitle(), iterator.next().getElement("title").getStringValue());
        assertEquals(l.get(2).getTitle(), iterator.next().getElement("title").getStringValue());
    }

    public void testBeanMap() throws Exception {
        JavaMapBean mb = new JavaMapBean();

        Map<String, Book> m = new HashMap<String, Book>();
        m.put("test", junitBook);
        mb.setHashMap((HashMap<String, Book>)m);

        Book[] books = new Book[3];
        books[0] = junitBook;
        books[1] = csharpBook;
        books[2] = javaScriptBook;

        Map<String, List<Book>> hu = new HashMap<String, List<Book>>();
        hu.put("1", Arrays.asList(books));
        hu.put("2", Arrays.asList(books));
        hu.put("3", Arrays.asList(books));
        mb.setMapList(hu);

        Map<String, Book[]> arrayMap = new HashMap<String, Book[]>();
        arrayMap.put("1", books);
        arrayMap.put("2", books);
        arrayMap.put("3", books);
        mb.setMapArray(arrayMap);

        Map<String, String> str = new HashMap<String, String>();
        str.put("key1", "value1");
        str.put("key2", "value2");
        str.put("key3", "value3");
        mb.setStrings(str);

        JsonValue jsonValue = JsonGenerator.createJsonObject(mb);

        assertEquals(str.get("key2"), jsonValue.getElement("strings").getElement("key2").getStringValue());
        assertNotNull(jsonValue.getElement("hashMap"));
        assertNotNull(jsonValue.getElement("mapList"));
        assertEquals("JUnit in Action",
                     jsonValue.getElement("mapList").getElement("3").getElements().next().getElement("title").getStringValue());
        assertNotNull(jsonValue.getElement("mapArray"));
        assertEquals("JUnit in Action",
                     jsonValue.getElement("mapArray").getElement("3").getElements().next().getElement("title").getStringValue());
        // System.out.println(jsonValue);
    }

    public void testBeanTransientField() throws Exception {
        BeanWithTransientField trBean = new BeanWithTransientField();
        JsonValue jsonValue = JsonGenerator.createJsonObject(trBean);
        assertEquals("visible", jsonValue.getElement("field").getStringValue());
        assertNull(jsonValue.getElement("transientField"));
    }

    public void testBeanEnum() throws Exception {
        BeanWithSimpleEnum be = new BeanWithSimpleEnum();
        be.setName("name");
        be.setCount(StringEnum.TWO);
        be.setCounts(new StringEnum[]{StringEnum.ONE, StringEnum.TWO});
        be.setCountList(Arrays.asList(StringEnum.ONE, StringEnum.TWO, StringEnum.TREE));
        JsonValue jsonValue = JsonGenerator.createJsonObject(be);
        //System.out.println(jsonValue);

        assertEquals("name", jsonValue.getElement("name").getStringValue());

        assertEquals(StringEnum.TWO.name(), jsonValue.getElement("count").getStringValue());

        JsonValue countValues = jsonValue.getElement("counts");
        List<String> tmp = new ArrayList<String>();
        for (Iterator<JsonValue> counts = countValues.getElements(); counts.hasNext(); ) {
            tmp.add(counts.next().getStringValue());
        }
        assertEquals(2, tmp.size());
        assertTrue(tmp.contains(StringEnum.ONE.name()));
        assertTrue(tmp.contains(StringEnum.TWO.name()));

        JsonValue countListValues = jsonValue.getElement("countList");
        tmp = new ArrayList<String>();
        for (Iterator<JsonValue> counts = countListValues.getElements(); counts.hasNext(); ) {
            tmp.add(counts.next().getStringValue());
        }
        assertEquals(3, tmp.size());
        assertTrue(tmp.contains(StringEnum.ONE.name()));
        assertTrue(tmp.contains(StringEnum.TWO.name()));
        assertTrue(tmp.contains(StringEnum.TREE.name()));
    }

    public void testBeanEnumObject() throws Exception {
        BeanWithBookEnum be = new BeanWithBookEnum();
        be.setBook(BookEnum.JUNIT_IN_ACTION);
        JsonValue jsonValue = JsonGenerator.createJsonObject(be);
        //System.out.println(jsonValue);
        assertEquals(BookEnum.JUNIT_IN_ACTION.name(), jsonValue.getElement("book").getStringValue());
    }

    public void testBeanClassTransf() throws Exception {
        ClassTransfBean be = new ClassTransfBean();
        be.setKlass(ForTestClass000.class);
        JsonValue jsonValue = JsonGenerator.createJsonObject(be);
        //System.out.println(jsonValue);
        assertEquals(ForTestClass000.class.getName(), jsonValue.getElement("klass").getStringValue());
    }

    public static class ForTestClass000 {
    }
}
