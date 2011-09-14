package org.mypomodoro.gui.todo;


import javax.swing.JComboBox;

import org.mypomodoro.gui.create.ActivityInputForm;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

public class UnplannedActivityInputForm extends ActivityInputForm {

    private static final long serialVersionUID = 20110814L;
    protected JComboBox interruptions = new JComboBox();
    protected final String internal = Labels.getString("ToDoListPanel.Internal");
    protected final String external = Labels.getString("ToDoListPanel.External");

    public UnplannedActivityInputForm() {
        super(1);
        addInterruptions();
    }

    protected final void addInterruptions() {
        // Internal and External
        c.gridx = 0;
        c.gridy = 0;
        c.weighty = 0.5;
        add(new FormLabel(Labels.getString("ToDoListPanel.Interruption") + ": "),
                c);
        c.gridx = 1;
        c.gridy = 0;
        c.weighty = 0.5;
        String items[] = new String[3];
        items[0] = "";
        items[1] = internal;
        items[2] = external;
        interruptions = new JComboBox(items);
        interruptions.setBackground(ColorUtil.WHITE);
        add(interruptions, c);
    }

    public boolean isSelectedInternalInterruption() {
        return ( (String) interruptions.getSelectedItem() ).equals(internal);
    }

    public boolean isSelectedExternalInterruption() {
        return ( (String) interruptions.getSelectedItem() ).equals(external);
    }

    public void setInterruption(int index) {
        interruptions.setSelectedIndex(index);
    }
}