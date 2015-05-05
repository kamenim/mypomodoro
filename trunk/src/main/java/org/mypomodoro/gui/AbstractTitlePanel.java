/*
 * Copyright (C)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.mypomodoro.gui;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.Main;
import org.mypomodoro.buttons.DefaultButton;

/**
 *
 *
 */
public abstract class AbstractTitlePanel extends JPanel {

    protected final JLabel titleLabel = new JLabel();
    private final ImageIcon refreshIcon = new ImageIcon(Main.class.getResource("/images/refresh.png"));
    private final ImageIcon createIcon = new ImageIcon(Main.class.getResource("/images/create.png"));
    private final ImageIcon duplicateIcon = new ImageIcon(Main.class.getResource("/images/duplicate.png"));
    private final ImageIcon selectedIcon = new ImageIcon(Main.class.getResource("/images/selected.png"));
    private final ImageIcon unplannedIcon = new ImageIcon(Main.class.getResource("/images/unplanned.png"));
    private final ImageIcon internalIcon = new ImageIcon(Main.class.getResource("/images/internal.png"));
    private final ImageIcon externalIcon = new ImageIcon(Main.class.getResource("/images/external.png"));
    private final ImageIcon overestimationIcon = new ImageIcon(Main.class.getResource("/images/plusone.png"));
    protected final ImageIcon runningIcon = new ImageIcon(Main.class.getResource("/images/running.png"));
    protected final DefaultButton unplannedButton = new DefaultButton(unplannedIcon);
    protected final DefaultButton internalButton = new DefaultButton(internalIcon);
    protected final DefaultButton externalButton = new DefaultButton(externalIcon);
    protected final DefaultButton overestimationButton = new DefaultButton(overestimationIcon);
    protected final DefaultButton refreshButton = new DefaultButton(refreshIcon);
    protected final DefaultButton createButton = new DefaultButton(createIcon);
    protected final DefaultButton duplicateButton = new DefaultButton(duplicateIcon);
    protected final DefaultButton selectedButton = new DefaultButton(selectedIcon);
    protected final Insets buttonInsets = new Insets(0, 10, 0, 10);

    public AbstractTitlePanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        // Add label to panel
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        //titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, titleLabel.getFont().getSize() - 1));
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        add(titleLabel);
        // Init buttons
        // Scroll to selected task
        selectedButton.setMargin(buttonInsets);
        selectedButton.setFocusPainted(false); // removes borders around text
        selectedButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showCurrentSelectedRow();
            }
        });
        selectedButton.setToolTipText("CTRL + G");
        // Create new task
        createButton.setMargin(buttonInsets);
        createButton.setFocusPainted(false); // removes borders around text

        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createNewTask();
            }
        });
        createButton.setToolTipText("CTRL + T");
        // Duplicate selected task
        duplicateButton.setMargin(buttonInsets);
        duplicateButton.setFocusPainted(false); // removes borders around text
        duplicateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateTask();
            }
        });
        duplicateButton.setToolTipText("CTRL + D");
        // Create unplanned task
        unplannedButton.setMargin(buttonInsets);
        unplannedButton.setFocusPainted(false); // removes borders around text
        unplannedButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createUnplannedTask();
            }
        });
        unplannedButton.setToolTipText("CTRL + U");
        // Create internal interruption
        internalButton.setMargin(buttonInsets);
        internalButton.setFocusPainted(false); // removes borders around text
        internalButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createInternalInterruption();
            }
        });
        internalButton.setToolTipText("CTRL + I");
        // Create external interruption
        externalButton.setMargin(buttonInsets);
        externalButton.setFocusPainted(false); // removes borders around text
        externalButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createExternalInterruption();
            }
        });
        externalButton.setToolTipText("CTRL + E");
        // Overestimate by one pomodoro
        overestimationButton.setMargin(buttonInsets);
        overestimationButton.setFocusPainted(false); // removes borders around text
        overestimationButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                overestimateTask(1);
            }
        });
        // Refresh table from database
        refreshButton.setMargin(buttonInsets);
        refreshButton.setFocusPainted(false); // removes borders around text
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshButton.setEnabled(false);
                refreshTable(true);
                refreshButton.setEnabled(true);
            }
        });
    }

    public void showSelectedButton() {
        add(selectedButton);
    }

    public void showCreateButton() {
        add(createButton);
    }

    public void showDuplicateButton() {
        add(duplicateButton);
    }

    public void showOverestimationButton() {
        add(overestimationButton);
    }

    public void showUnplannedButton() {
        add(unplannedButton);
    }

    public void showInternalButton() {
        add(internalButton);
    }

    public void showExternalButton() {
        add(externalButton);
    }

    public void showRefreshButton() {
        add(refreshButton);
    }

    public void hideSelectedButton() {
        remove(selectedButton);
    }

    public void hideCreateButton() {
        remove(createButton);
    }

    public void hideDuplicateButton() {
        remove(duplicateButton);
    }

    public void hideOverestimationButton() {
        remove(overestimationButton);
    }

    public void hideUnplannedButton() {
        remove(unplannedButton);
    }

    public void hideInternalButton() {
        remove(internalButton);
    }

    public void hideExternalButton() {
        remove(externalButton);
    }

    public void hideRefreshButton() {
        remove(refreshButton);
    }

    @Override
    public void setToolTipText(String text) {
        titleLabel.setToolTipText(text);
    }

    public void setText(String text) {
        titleLabel.setText(text);
    }

    public void clear() {
        titleLabel.setText(null);
        Component[] comps = getComponents();
        for (Component comp : comps) {
            if (comp instanceof DefaultButton) {
                remove(comp);
            }
        }
    }

    /*public void repaintLabel() {
     titleLabel.repaint();        
     }*/
    protected abstract void showCurrentSelectedRow();

    protected abstract void createNewTask();

    protected abstract void duplicateTask();

    protected abstract void createUnplannedTask();

    protected abstract void createInternalInterruption();

    protected abstract void createExternalInterruption();

    protected abstract void overestimateTask(int poms);

    protected abstract void refreshTable(boolean fromDatabase);
}
