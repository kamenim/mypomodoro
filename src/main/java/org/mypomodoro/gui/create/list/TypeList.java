package org.mypomodoro.gui.create.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.mypomodoro.db.ActivitiesDAO;
import org.mypomodoro.gui.PreferencesPanel;

/**
 * List of types of activities and reports
 *
 */
public class TypeList extends AbstractList {

    private static ArrayList<String> types = new ArrayList<String>();

    public static void initTypes() {
        types = ActivitiesDAO.getInstance().getTypes();
        if (PreferencesPanel.preferences.getAgileMode()) {
            types.add("User Story");
            types.add("Epic");
            types.add("Defect");
            types.add("Technical work");
            types.add("Impediment");
            types.add("Bug");
            types.add("Duplicate");
            types.add("Spike");
            types.add("Change request");
            types.add("Knowledge acquisition");
            types = new ArrayList<String>(new HashSet<String>(types)); // remove duplicates
        }
    }

    public static List<String> getTypes() {
        if (types.size() > 1) {
            Collections.sort(types, new SortIgnoreCase());
        }
        return types;
    }

    public static void addType(String type) {
        if (type.trim().length() > 0 && !types.contains(type.trim())) {
            types.add(type.trim());
        }
    }
}
