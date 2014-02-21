package org.mypomodoro.gui.create.list;

import java.util.Comparator;

/**
 * List of types of activities and reports
 *
 */
public class AbstractList {

    // sorting case insensitive
    protected static class SortIgnoreCase implements Comparator<Object> {

        @Override
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }
}
