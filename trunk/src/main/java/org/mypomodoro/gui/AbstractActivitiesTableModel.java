package org.mypomodoro.gui;

import javax.swing.table.DefaultTableModel;

public abstract class AbstractActivitiesTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 20110814L;

    public AbstractActivitiesTableModel(Object[][] tableData, String[] fields) {
        super(tableData, fields);
    }
}
