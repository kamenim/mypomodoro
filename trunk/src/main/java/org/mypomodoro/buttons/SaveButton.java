package org.mypomodoro.buttons;


import org.mypomodoro.gui.SaveListener;
import org.mypomodoro.gui.create.CreatePanel;
import org.mypomodoro.util.Labels;

public class SaveButton extends MyButton {

    public SaveButton(CreatePanel panel) {
        super(Labels.getString("Common.Save"));
        addActionListener(new SaveListener(panel));
    }
}