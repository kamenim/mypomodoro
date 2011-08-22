package org.mypomodoro.gui.activities.export;

/**
 * Panel to import activities
 * 
 * @author Phil Karoo
 */
public class ImportPanel extends org.mypomodoro.gui.reports.export.ImportPanel {
    
    @Override
    protected void insertData(String[] line) throws Exception {
        insertData(line, false);
    }    
}