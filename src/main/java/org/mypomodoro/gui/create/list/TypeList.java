package org.mypomodoro.gui.create.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.mypomodoro.db.ActivitiesDAO;

/**
 * List of types of activities and reports
 *
 * @author Phil Karoo
 */
public class TypeList {

    private static List<String> types = new ArrayList<String>();

    public static void initTypes() {
        types = ActivitiesDAO.getInstance().getTypes();
    }

    public static List<String> getTypes() {
        if (types.size() > 1) {
            Collections.sort(types);
        }
        return types;
    }

    public static void addType(String type) {
        if (!types.contains(type)) {
            types.add(type);
        }
    }
}