package org.mypomodoro.menubar.help;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.mypomodoro.Main;
import org.mypomodoro.gui.ImageIcons;
import org.mypomodoro.gui.MyPomodoroView;
import org.mypomodoro.util.BareBonesBrowserLaunch;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.Labels;

/**
 * GUI for myPomodoro about menu. Using JDialog to remove minimize and maximize
 * icons
 * 
 * @author Phil Karoo
 */
public class AboutPanel extends JDialog {

    private static final long serialVersionUID = 20110814L;
    public static final int FRAME_WIDTH = 630;
    public static final int FRAME_HEIGHT = 360;
    private final GridBagConstraints gbc = new GridBagConstraints();

    public AboutPanel(JDialog frame, String str) {
        super(frame, str);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setIconImage(ImageIcons.MAIN_ICON.getImage());
        setResizable(false);

        JPanel about = new JPanel();
        about.setLayout(new GridBagLayout());
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
        gbc.weightx = 0.2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JLabel backgroundImage = new JLabel(new ImageIcon(
                Main.class.getResource("/images/pomodoroTechniqueAbout.png")));
        JPanel panel = new JPanel();
        panel.add(backgroundImage);
        panel.setBackground(ColorUtil.WHITE);
        add(panel, gbc);
    }

    private void addAbout() {
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.8;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel panel = new JPanel();
        BoxLayout layout = new BoxLayout(panel, BoxLayout.Y_AXIS); // left
        // alignment
        panel.setLayout(layout);
        panel.setBackground(ColorUtil.WHITE);

        GridBagConstraints gbcpanel = new GridBagConstraints();
        gbcpanel.gridx = 0;
        gbcpanel.gridy = 0;
        gbcpanel.fill = GridBagConstraints.BOTH;
        JLabel title = new JLabel("myPomodoro");
        title.setFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize() + 24));
        panel.add(title, gbcpanel);
        gbcpanel.gridx = 0;
        gbcpanel.gridy = 1;
        gbcpanel.fill = GridBagConstraints.BOTH;
        JLabel version = new JLabel(MyPomodoroView.MYPOMODORO_VERSION);
        version.setFont(new Font(new JLabel().getFont().getName(), Font.PLAIN,
                new JLabel().getFont().getSize() + 2));
        panel.add(version, gbcpanel);
        gbcpanel.gridx = 0;
        gbcpanel.gridy = 2;
        gbcpanel.fill = GridBagConstraints.BOTH;
        JButton checkButton = new JButton(
                Labels.getString("AboutPanel.Check for Updates"));
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
        String about = Labels.getString("AboutPanel.myPomodoro is a time management tool");
        JTextArea aboutTextArea = new JTextArea();
        aboutTextArea.setEditable(false);
        aboutTextArea.setLineWrap(true);
        aboutTextArea.setWrapStyleWord(true);
        aboutTextArea.setText(about);
        aboutTextArea.setFont(new Font(new JLabel().getFont().getName(),
                Font.PLAIN, new JLabel().getFont().getSize()));
        aboutTextArea.setOpaque(false);
        aboutTextArea.setAlignmentX(LEFT_ALIGNMENT); // left alignment
        panel.add(aboutTextArea, gbcpanel);
        gbcpanel.gridx = 0;
        gbcpanel.gridy = 4;
        gbcpanel.fill = GridBagConstraints.BOTH;
        String credits = Labels.getString("AboutPanel.Credits");
        JTextArea creditsTextArea = new JTextArea();
        creditsTextArea.setEditable(false);
        creditsTextArea.setLineWrap(true);
        creditsTextArea.setWrapStyleWord(true);
        creditsTextArea.setText(credits);
        creditsTextArea.setFont(new Font(new JLabel().getFont().getName(),
                Font.PLAIN, new JLabel().getFont().getSize()));
        creditsTextArea.setOpaque(false);
        creditsTextArea.setAlignmentX(LEFT_ALIGNMENT); // left alignment
        panel.add(creditsTextArea, gbcpanel);

        add(panel, gbc);
    }

    private void addLicence() {
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        String license = Labels.getString("AboutPanel.myPomodoro is open-source software");
        license += "<br>"
                + Labels.getString("AboutPanel.All documentation and images are licensed");
        license += "<br>"
                + Labels.getString("AboutPanel.Permissions beyond the scope of this license");
        JEditorPane editorPane = new JEditorPane("text/html", license);
        editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES,
                Boolean.TRUE);
        editorPane.setFont(new Font(new JLabel().getFont().getName(),
                Font.PLAIN, new JLabel().getFont().getSize() - 4));
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

        // Wrap content!
        panel.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1.0;

        panel.add(editorPane, constraints);
        add(panel, gbc);
    }
}