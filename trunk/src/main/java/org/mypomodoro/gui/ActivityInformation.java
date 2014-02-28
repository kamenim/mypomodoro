package org.mypomodoro.gui;

import org.mypomodoro.model.Activity;

/**
 * An interface to represent an object that can describe an activity.
 *
 */
public interface ActivityInformation {

    void selectInfo(Activity activity);

    void showInfo();
    
    void showInfo(String info);

    void clearInfo();
    
    boolean isMultipleSelectionAllowed();
}
