package org.mypomodoro.gui;

import javax.swing.JTable;
import org.mypomodoro.model.Activity;

public interface AbstractActivitiesPanel {

    void move(Activity activity);

    void removeRow(int row);

    void setPanelBorder();

    void selectActivity();

    JTable getTable();

    int getIdKey();
    
    Activity getActivityById(int id);
    
    void delete(Activity activity);
}
