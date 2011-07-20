package org.mypomodoro.menubar.help;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import org.mypomodoro.gui.*;

import javax.swing.JFrame;
import javax.swing.JLabel;

import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import org.mypomodoro.Main;
import org.mypomodoro.util.BareBonesBrowserLaunch;

/**
 * GUI for myPomodoro about menu.
 * Using JDialog to remove minimize and maximize icons
 * 
 * @author Phil Karoo
 */
public class AboutPanel extends JDialog {

    public static final int FRAME_WIDTH = 630;
    public static final int FRAME_HEIGHT = 360;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public AboutPanel(JFrame frame, String str) {
        super(frame, str);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setIconImage(ImageIcons.MAIN_ICON.getImage());
        setResizable(false);

        JPanel about = new JPanel();
        about.setLayout(new BorderLayout());
        about.setOpaque(true);
        add(about);

        setContentPane(about);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);

        setLayout(new GridBagLayout());

        addmyPomodoroImage();
        addAbout();
        addLicence();
    }

    private void addmyPomodoroImage() {
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.1;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;
        JLabel backgroundImage = new JLabel(new ImageIcon(Main.class.getResource("/images/pomodoroTechniqueAbout.png")));
        JPanel panel = new JPanel();
        panel.add(backgroundImage);
        panel.setBackground(Color.WHITE);
        add(panel, gbc);
    }

    private void addAbout() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.BOTH;        
        //JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
                
        GridBagConstraints gbcpanel = new GridBagConstraints();        
        gbcpanel.gridx = 0;
        gbcpanel.gridy = 0;
        gbcpanel.fill = GridBagConstraints.BOTH;
        JLabel title = new JLabel("myPomodoro");
        title.setFont(new Font(title.getFont().getName(), Font.BOLD, title.getFont().getSize() + 20));
        panel.add(title, gbcpanel);
        gbcpanel.gridx = 0;
        gbcpanel.gridy = 1;
        gbcpanel.fill = GridBagConstraints.BOTH;
        panel.add(new JLabel(MyPomodoroView.MYPOMODORO_VERSION), gbcpanel);
        gbcpanel.gridx = 0;
        gbcpanel.gridy = 2;
        gbcpanel.fill = GridBagConstraints.BOTH;
        JButton checkButton = new JButton("Check for Updates");        
        checkButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                BareBonesBrowserLaunch.openURL("https://code.google.com/p/mypomodoro/downloads/list");
            }
        });
        panel.add(checkButton, gbcpanel);
        gbcpanel.gridx = 0;
        gbcpanel.gridy = 3;
        gbcpanel.fill = GridBagConstraints.BOTH;
        String about = "myPomodoro is a time management tool based upon the time management technique called the Pomodoro Technique® by Francesco Cirillo. The objectives of this software are to automate all the materials and methods used in the Pomodoro Technique®, which are otherwise preformed manually.";
        JTextArea jTextArea = new JTextArea();
        jTextArea.setEditable(false); 
        jTextArea.setLineWrap(true); 
        jTextArea.setWrapStyleWord(true);
        jTextArea.setText(about);
        jTextArea.setFont(new Font(new JLabel().getFont().getName(), Font.PLAIN, new JLabel().getFont().getSize()));
        panel.add(jTextArea, gbcpanel);
        
        add(panel, gbc);
    }

    private void addLicence() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        String license = "myPomodoro is open-source software, licensed under the <a href=\"http://www.gnu.org/licenses/lgpl.html\">GNU Lesser General Public License</a> (LGPL).";
        license += "<br>All documentation and images are licensed under a <a href=\"http://creativecommons.org/licenses/by-nc-sa/3.0/us/\">Creative Commons Attribution-Noncommercial-Share Alike 3.0 United States License</a>.";
        license += "<br>Permissions beyond the scope of this license may be available at <a href=\"https://code.google.com/p/mypomodoro/\">https://code.google.com/p/mypomodoro/</a> by contacting one of the project owners.";
        JEditorPane editorPane = new JEditorPane("text/html", license);
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
        editorPane.setFont(new Font(new JLabel().getFont().getName(), Font.PLAIN, new JLabel().getFont().getSize() - 4));
        editorPane.setEditable(false);
        editorPane.setOpaque(false);
        editorPane.addHyperlinkListener(new HyperlinkListener() {

            @Override
            public void hyperlinkUpdate(HyperlinkEvent hle) {
                if (HyperlinkEvent.EventType.ACTIVATED.equals(hle.getEventType())) {
                    BareBonesBrowserLaunch.openURL(hle.getURL().toString());
                }
            }
        });
        JPanel panel = new JPanel();
        panel.add(editorPane);
        add(panel, gbc);
    }
}