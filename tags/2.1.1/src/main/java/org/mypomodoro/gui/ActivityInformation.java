package org.mypomodoro.gui;

import org.mypomodoro.model.Activity;

/**
 * An interface to represent an object that can describe an activity.
 *
 * @author nikolavp
 *
 */
public interface ActivityInformation {

    void showInfo(Activity activity);

    void clearInfo();
}
