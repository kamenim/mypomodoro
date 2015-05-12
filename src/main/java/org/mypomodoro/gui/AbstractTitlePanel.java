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
    protected final JPanel buttonPanel = new JPanel();
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
    // left and rigth 'small' arrows
    private final String rightArrow = " " + (getFont().canDisplay('\u25b6') ? "\u25b6" : ">") + " ";
    private final String leftArrow = " " + (getFont().canDisplay('\u25c0') ? "\u25c0" : "<") + " ";    
    // Expand/Fold button
    protected final DefaultButton foldButton = new DefaultButton(leftArrow);

    public AbstractTitlePanel() {
        setLayout(new FlowLayout(FlowLayout.LEFT, 1, 1));
        setBorder(new EtchedBorder(EtchedBorder.LOWERED));        
        // Add label to panel
        titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD));
        //titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD, titleLabel.getFont().getSize() - 1));
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        showTitleLabel();
        // init button panel
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 1));
        buttonPanel.setBorder(null);
        showButtonPanel();
        // Init buttons
        // Fold button
        foldButton.setText(leftArrow);
        foldButton.setBorder(null); // this is important to remove the invisible border
        //foldButton.setMargin(buttonInsets); this doesn't work reason why we add spaces to rightArrow and leftArrow strings
        // foldButton.setSize(selectedButton.getSize()); this doesn't work either
        foldButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (titleLabel.isShowing()) {
                    foldButton.setText(rightArrow);
                    hideTitleLabel();
                    showButtonPanel();
                } else {
                    foldButton.setText(leftArrow);
                    hideButtonPanel();
                    showTitleLabel();
                }
                AbstractTitlePanel.this.repaint();
            }
        });
        // Scroll to selected task
        selectedButton.setMargin(buttonInsets);
        selectedButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                showCurrentSelectedRow();
            }
        });
        selectedButton.setToolTipText("CTRL + G");
        // Create new task
        createButton.setMargin(buttonInsets);
        createButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createNewTask();
            }
        });
        createButton.setToolTipText("CTRL + T");
        // Duplicate selected task
        duplicateButton.setMargin(buttonInsets);
        duplicateButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                duplicateTask();
            }
        });
        duplicateButton.setToolTipText("CTRL + D");
        // Create unplanned task
        unplannedButton.setMargin(buttonInsets);
        unplannedButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createUnplannedTask();
            }
        });
        unplannedButton.setToolTipText("CTRL + U");
        // Create internal interruption
        internalButton.setMargin(buttonInsets);
        internalButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createInternalInterruption();
            }
        });
        internalButton.setToolTipText("CTRL + I");
        // Create external interruption
        externalButton.setMargin(buttonInsets);
        externalButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createExternalInterruption();
            }
        });
        externalButton.setToolTipText("CTRL + E");
        // Overestimate by one pomodoro
        overestimationButton.setMargin(buttonInsets);
        overestimationButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                overestimateTask(1);
            }
        });
        // Refresh table from database
        refreshButton.setMargin(buttonInsets);
        refreshButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshButton.setEnabled(false);
                refreshTable(true);
                refreshButton.setEnabled(true);
            }
        });
    }
    
    public void showTitleLabel() {
        add(titleLabel);
    }
    
    public void hideTitleLabel() {
        remove(titleLabel);
    }
    
    public void showButtonPanel() {
        add(buttonPanel);
    }
    
    public void hideButtonPanel() {
        remove(buttonPanel);
    }
    
    public void showFoldButton() {
       add(foldButton, 0);
    }

    public void showSelectedButton() {
        buttonPanel.add(selectedButton);
    }

    public void switchSelectedButton() {
        selectedButton.setIcon(selectedIcon);
    }

    public void switchRunningButton() {
        selectedButton.setIcon(runningIcon);
    }

    public void showCreateButton() {
        buttonPanel.add(createButton);
    }

    public void showDuplicateButton() {
        buttonPanel.add(duplicateButton);
    }

    public void showOverestimationButton() {
        buttonPanel.add(overestimationButton);
    }

    public void showUnplannedButton() {
        buttonPanel.add(unplannedButton);
    }

    public void showInternalButton() {
        buttonPanel.add(internalButton);
    }

    public void showExternalButton() {
        buttonPanel.add(externalButton);
    }

    public void showRefreshButton() {
        buttonPanel.add(refreshButton);
    }

    public void hideSelectedButton() {
        buttonPanel.remove(selectedButton);
    }

    public void hideCreateButton() {
        buttonPanel.remove(createButton);
    }

    public void hideDuplicateButton() {
        buttonPanel.remove(duplicateButton);
    }

    public void hideOverestimationButton() {
        buttonPanel.remove(overestimationButton);
    }

    public void hideUnplannedButton() {
        buttonPanel.remove(unplannedButton);
    }

    public void hideInternalButton() {
        buttonPanel.remove(internalButton);
    }

    public void hideExternalButton() {
        buttonPanel.remove(externalButton);
    }
   
    public void hideRefreshButton() {
        buttonPanel.remove(refreshButton);
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

    protected abstract void overestimateTask(int poms);

    protected abstract void refreshTable(boolean fromDatabase);
}
