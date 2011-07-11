package org.mypomodoro.gui;

import javax.swing.ImageIcon;

import org.mypomodoro.Main;

public class ImageIcons {

    public static final ImageIcon MAIN_ICON = getIcon("/images/pomodoro16.png");
    public static final ImageIcon SPLASH_ICON = getIcon("/images/pomodoroTechnique128.png");
    public static final ImageIcon CREATE_ICON_ON = getIcon("/images/createButton2.png");

    private static ImageIcon getIcon(String resourcePath) {
        return new ImageIcon(Main.class.getResource(resourcePath));
    }
}
