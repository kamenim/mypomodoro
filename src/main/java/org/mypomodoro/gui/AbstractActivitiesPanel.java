package org.mypomodoro.gui;

import javax.swing.JTable;
import org.mypomodoro.model.Activity;

public interface AbstractActivitiesPanel {

    void refresh();

    void move(Activity activity);

    void removeRow(int row);

    void setPanelBorder();

    JTable getTable();

    int getIdKey();

    Activity getActivityById(int id);

    void delete(Activity activity);

    void deleteAll();

    void complete(Activity activity);

    void completeAll();
    
    void addActivity(Activity activity);
}
