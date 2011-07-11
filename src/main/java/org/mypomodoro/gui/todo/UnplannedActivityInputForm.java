package org.mypomodoro.gui.todo;

import org.mypomodoro.gui.create.*;
import javax.swing.JComboBox;
import org.mypomodoro.gui.ControlPanel;

public class UnplannedActivityInputForm extends ActivityInputForm {

    protected JComboBox interruptions = new JComboBox();
    protected final String internal = ControlPanel.labels.getString("ToDoListPanel.Internal");
    protected final String external = ControlPanel.labels.getString("ToDoListPanel.External");

    public UnplannedActivityInputForm() {
        super(1);
        addInterruptions();
    }

    protected final void addInterruptions() {
        // Internal and External        
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.5;
        add(new FormLabel(ControlPanel.labels.getString("ToDoListPanel.Interruption") + ": "), c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.5;
        String items[] = new String[3];
        items[0] = "";
        items[1] = internal;
        items[2] = external;
        interruptions = new JComboBox(items);
        add(interruptions, c);
    }

    public boolean isSelectedInternalInterruption() {
        return ( (String) interruptions.getSelectedItem() ).equals(internal);
    }

    public boolean isSelectedExternalInterruption() {
        return ( (String) interruptions.getSelectedItem() ).equals(external);
    }
}