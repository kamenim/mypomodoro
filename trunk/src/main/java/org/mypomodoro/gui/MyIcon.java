package org.mypomodoro.gui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import org.mypomodoro.Main;

public class MyIcon extends JLabel {

    private static final long serialVersionUID = 20110814L;

    private static ImageIcon getIcon(String path) {
        return new ImageIcon(Main.class.getResource(path));
    }

    public static MyIcon getInstance(final MyPomodoroView view, String text,
            String fileName, JPanel p) {
        String onPath = "/images/" + fileName + "2.png";
        String offPath = "/images/" + fileName + ".png";
        ImageIcon onIcon = getIcon(onPath);
        ImageIcon offIcon = getIcon(offPath);
        return new MyIcon(view, text, onIcon, offIcon, p);
    }
    private final Dimension d = new Dimension(1000, 80);
    private final JPanel panel;
    private final ImageIcon on;
    private final ImageIcon off;

    public void highlight() {
        setIcon(on);
    }

    public void unhighlight() {
        setIcon(off);
    }

    public MyIcon(final MyPomodoroView view, String Text, ImageIcon on,
            ImageIcon off, JPanel p) {
        super(Text, off, CENTER);
        this.off = off;
        this.on = on;
        panel = p;

        setPreferredSize(d);
        setMinimumSize(d);
        setHorizontalTextPosition(JLabel.CENTER);
        setVerticalTextPosition(JLabel.BOTTOM);
        addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                view.getIconBar().highlightIcon(MyIcon.this);
                view.setWindow(panel);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                setBorder(new EtchedBorder(EtchedBorder.LOWERED));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBorder(null);
            }
        });
    }

    public JPanel getPanel() {
        return panel;
    }
}