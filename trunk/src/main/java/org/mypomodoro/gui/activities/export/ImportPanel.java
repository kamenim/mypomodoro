package org.mypomodoro.gui.activities.export;

import org.mypomodoro.model.Activity;

/**
 *
 * @author Phil Karoo
 */
public class ImportPanel extends org.mypomodoro.gui.reports.export.ImportPanel {
    
    @Override
    protected void insertData(String[] line) throws Exception {
        Activity newActivity = new Activity(line[13], line[12], line[3], line[14], line[11], Integer.parseInt(line[4]), 
                org.mypomodoro.util.DateUtil.getDate(line[1] + " " + line[2], importInputForm.getDatePattern()), Integer.parseInt(line[5]), Integer.parseInt(line[6]),
                Integer.parseInt(line[9]), Integer.parseInt(line[10]), line[15], 
                line[0].equals("0")?false:true, false);
        newActivity.databaseInsert();
    }    
}