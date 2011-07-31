package org.mypomodoro.buttons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import org.mypomodoro.util.Labels;
import org.mypomodoro.util.Restart;

public class RestartButton extends MyButton {

    public RestartButton() {
        super(Labels.getString("Common.Restart"));
        addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    Restart.restartApplication(null);
                }
                catch (IOException ex) {
                }
            }
        });
    }
}