/* 
 * Copyright (C) 2014
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

import java.awt.BorderLayout;
import java.awt.CardLayout;

import javax.swing.JPanel;

public class WindowPanel extends JPanel {

    private static final long serialVersionUID = 20110814L;

    CardLayout cardLayout = new CardLayout();
    final JPanel mainPanel = new JPanel(cardLayout);

    public WindowPanel(JPanel iconBar, MainPanel view) {
        setLayout(new BorderLayout());
        setOpaque(true);
        add(iconBar, BorderLayout.NORTH);
        add(view.getProgressBar(), BorderLayout.AFTER_LAST_LINE);
        SplashScreen splashScreen = new SplashScreen();
        mainPanel.add(splashScreen, splashScreen.getClass().getName());
        mainPanel.add(view.getPreferencesPanel(), view.getPreferencesPanel().getClass().getName());
        mainPanel.add(view.getCreatePanel(), view.getCreatePanel().getClass().getName());
        mainPanel.add(view.getActivityListPanel(), view.getActivityListPanel().getClass().getName());
        mainPanel.add(view.getToDoPanel(), view.getToDoPanel().getClass().getName());
        mainPanel.add(view.getReportListPanel(), view.getReportListPanel().getClass().getName());
        mainPanel.add(view.getChartTabbedPanel(), view.getChartTabbedPanel().getClass().getName());
        add(mainPanel, BorderLayout.CENTER);
    }

    public void showPanel(String name) {
        cardLayout.show(mainPanel, name);
    }
}
