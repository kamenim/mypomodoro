package org.mypomodoro.gui;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class SplashScreen extends JPanel {

    private static final long serialVersionUID = 20110814L;

    public SplashScreen() {
        setBackground(Color.white);
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        JLabel title = new JLabel("");
        title.setFont(title.getFont().deriveFont(48f));
        title.setForeground(Color.red);

        c.gridx = 0;
        c.gridy = 0;
        c.fill = GridBagConstraints.BOTH;
        // add(title, c);
        c.gridy = 1;
        add(new JLabel(ImageIcons.SPLASH_ICON, JLabel.CENTER), c);
    }
}