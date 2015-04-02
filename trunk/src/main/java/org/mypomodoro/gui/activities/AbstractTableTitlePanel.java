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
package org.mypomodoro.gui.activities;

import java.awt.Dimension;
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
public abstract class AbstractTableTitlePanel extends JPanel {

    protected final JLabel titleLabel = new JLabel();
    private final ImageIcon refreshIcon = new ImageIcon(Main.class.getResource("/images/refresh.png"));
    private final ImageIcon createIcon = new ImageIcon(Main.class.getResource("/images/create.png"));
    private final ImageIcon duplicateIcon = new ImageIcon(Main.class.getResource("/images/duplicate.png"));
    private final ImageIcon selectedIcon = new ImageIcon(Main.class.getResource("/images/selected.png"));
    private final ImageIcon unplannedIcon = new ImageIcon(Main.class.getResource("/images/unplanned.png"));
    private final ImageIcon internalIcon = new ImageIcon(Main.class.getResource("/images/internal.png"));
    private final ImageIcon externalIcon = new ImageIcon(Main.class.getResource("/images/external.png"));
    private final ImageIcon overestimateIcon = new ImageIcon(Main.class.getResource("/images/plusone.png"));
    protected final ImageIcon runningIcon = new ImageIcon(Main.class.getResource("/images/running.png"));
    protected final DefaultButton unplannedButton = new DefaultButton(unplannedIcon);
    protected final DefaultButton internalButton = new DefaultButton(internalIcon);
    protected final DefaultButton externalButton = new DefaultButton(externalIcon);
    protected final DefaultButton overestimateButton = new DefaultButton(overestimateIcon);
    protected final DefaultButton refreshButton = new DefaultButton(refreshIcon);
    protected final DefaultButton createButton = new DefaultButton(createIcon);
    protected final DefaultButton duplicateButton = new DefaultButton(duplicateIcon);
    protected final DefaultButton selectedButton = new DefaultButton(selectedIcon);
    protected final Insets buttonInsets = new Insets(0, 10, 0, 10);

    public AbstractTableTitlePanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));
        // Add label to panel
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
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

    protected void showSelectedButton() {
        add(selectedButton);
    }

    protected void showCreateButton() {
        add(createButton);
    }

    protected void showDuplicateButton() {
        add(duplicateButton);
    }

    protected void showUnplannedButton() {
        add(unplannedButton);
    }

    protected void showInternalButton() {
        add(internalButton);
    }

    protected void showExternalButton() {
        add(externalButton);
    }

    protected void showRefreshButton() {
        add(refreshButton);
    }

    protected void hideSelectedButton() {
        remove(selectedButton);
    }

    protected void hideCreateButton() {
        remove(createButton);
    }

    protected void hideDuplicateButton() {
        remove(duplicateButton);
    }

    protected void hideUnplannedButton() {
        remove(unplannedButton);
    }

    protected void hideInternalButton() {
        remove(internalButton);
    }

    protected void hideExternalButton() {
        remove(externalButton);
    }

    protected void hideRefreshButton() {
        remove(refreshButton);
    }

    @Override
    public void setToolTipText(String text) {
        titleLabel.setToolTipText(text);
    }

    public void setText(String text) {
        titleLabel.setText(text);
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

    protected abstract void refreshTable(boolean fromDatabase);
}
