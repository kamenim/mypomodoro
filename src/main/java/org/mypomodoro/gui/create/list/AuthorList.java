package org.mypomodoro.gui.create.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mypomodoro.db.ActivitiesDAO;

/**
 * List of authors of activities and reports
 *
 * @author Phil Karoo
 */
public class AuthorList {

    private static List<String> authors = new ArrayList<String>();

    public static void initAuthors() {
        authors = ActivitiesDAO.getInstance().getAuthors();
    }

    public static List<String> getAuthors() {
        if (authors.size() > 1) {
            Collections.sort(authors);
        }
        return authors;
    }

    public static void addAuthor(String author) {
        if (!authors.contains(author)) {
            authors.add(author);
        }
    }
}