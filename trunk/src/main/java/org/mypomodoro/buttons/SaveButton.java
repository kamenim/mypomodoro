package org.mypomodoro.buttons;

import javax.swing.JButton;
import org.mypomodoro.gui.ControlPanel;

import org.mypomodoro.gui.SaveListener;
import org.mypomodoro.gui.create.CreatePanel;

public class SaveButton extends JButton {

    public SaveButton(CreatePanel panel) {
        super(ControlPanel.labels.getString("Common.Save"));
        addActionListener(new SaveListener(panel));
    }
}