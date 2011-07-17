package org.mypomodoro.menubar.help;

import java.awt.BorderLayout;
import javax.swing.JDialog;
import org.mypomodoro.gui.*;

import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JPanel;

/**
 * GUI for myPomodoro about menu.
 * Using JDialog to remove minimize and maximize icons
 * 
 * @author Phil Karoo
 */
public class AboutView extends JDialog {

    public static final int FRAME_WIDTH = 360;
    public static final int FRAME_HEIGHT = 630;

    public AboutView(JFrame frame, String str) {
        super(frame, str);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setIconImage(ImageIcons.MAIN_ICON.getImage());
        setResizable(false);
        
        JPanel test = new JPanel();
        test.setLayout(new BorderLayout());
        test.setOpaque(true);
        add(test);

        setContentPane(test);
        setSize(FRAME_HEIGHT, FRAME_WIDTH);

        JLabel lbl = new JLabel("TEST");
        JPanel panel = new JPanel();
        panel.add(lbl);
        add(panel);
        
        // TO BE FINISHED
    }
}