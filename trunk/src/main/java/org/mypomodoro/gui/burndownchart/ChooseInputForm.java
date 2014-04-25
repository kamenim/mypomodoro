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
package org.mypomodoro.gui.burndownchart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ComponentTitledBorder;
import org.mypomodoro.util.Labels;

/**
 * Export form
 *
 */
public class ChooseInputForm extends JPanel {

    protected static final Dimension LABEL_DIMENSION = new Dimension(170, 20);
    private static final Dimension COMBO_BOX_DIMENSION = new Dimension(300, 20);
    private static final Dimension COLOR_SIZE_DIMENSION = new Dimension(60, 20);
    private final GridBagConstraints c = new GridBagConstraints();
    // Burndown Chart form
    private final JPanel burndownChartInputFormPanel = new JPanel();
    private JTextField primaryYAxisName = new JTextField();
    private final String defaultPrimaryYAxisName = Labels.getString("BurndownChartPanel.Story Points");
    private JTextField primaryYAxisLegend = new JTextField();
    private final String defaultPrimaryYAxisLegend = Labels.getString("BurndownChartPanel.Story Points");
    private JTextField primaryYAxisColor = new JTextField();
    private final Color defaultPrimaryYAxisColor = ColorUtil.YELLOW_CHART;
    final JCheckBox burndownChartCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Burndown Chart"), true);
    private final ComponentTitledBorder borderBurndownChart = new ComponentTitledBorder(burndownChartCheckBox, burndownChartInputFormPanel, new EtchedBorder(), getFont().deriveFont(Font.BOLD));
    // Burndown Target Line form
    private final JPanel targetInputFormPanel = new JPanel();
    private JTextField targetLegend = new JTextField();
    private final String defaultTargetLegend = Labels.getString("BurndownChartPanel.Target");
    private JTextField targetColor = new JTextField();
    private final Color defaultTargetColor = ColorUtil.BLACK;
    private final JCheckBox targetCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Target"), true);
    // Burn-up Chart form
    private final JPanel burnupChartInputFormPanel = new JPanel();
    private JTextField secondaryYAxisName = new JTextField();
    private final String defaultSecondaryYAxisName = Labels.getString("BurndownChartPanel.Story Points");
    private JTextField secondaryYAxisLegend = new JTextField();
    private final String defaultSecondaryYAxisLegend = Labels.getString("BurndownChartPanel.Story Points");
    private JTextField secondaryYAxisColor = new JTextField();
    private final Color defaultSecondaryYAxisColor = ColorUtil.RED_CHART;
    final JCheckBox burnupChartCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Burn-up Chart"), true);
    // Burn-up Target Line form
    private final JPanel burnupTargetInputFormPanel = new JPanel();
    private JTextField burnupTargetLegend = new JTextField();
    private final String defaultBurnupTargetLegend = Labels.getString("BurndownChartPanel.Target");
    private JTextField burnupTargetColor = new JTextField();
    private final Color defaultBurnupTargetColor = ColorUtil.BLACK;
    private final JCheckBox burnupTargetCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Target"), true);
    // Burn-up Scope Line form
    private final JPanel scopeInputFormPanel = new JPanel();
    private JTextField scopeLegend = new JTextField();
    private final String defaultScopeLegend = Labels.getString("BurndownChartPanel.Scope");
    private JTextField scopeColor = new JTextField();
    private final Color defaultScopeColor = ColorUtil.BLACK;
    private final JCheckBox scopeCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Scope"), true);
    private final ComponentTitledBorder borderScope = new ComponentTitledBorder(scopeCheckBox, scopeInputFormPanel, new EtchedBorder(), getFont().deriveFont(Font.BOLD));

    public ChooseInputForm() {
        setLayout(new GridBagLayout());
        // The following three lines are necessary to make the additional jpanels to fill up the parent jpanel
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        addBurndownChartInputFormPanel();
        addBurnupChartInputFormPanel();
    }

    private void addBurndownChartInputFormPanel() {
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.5;
        // Burndown       
        burndownChartCheckBox.setFocusPainted(false);
        burndownChartCheckBox.setSelected(true);
        burndownChartCheckBox.addActionListener(new ActionListener() { // no burndown and scope line on the same chart as they share the same axis (X-Axis)

            @Override
            public void actionPerformed(ActionEvent event) {
                scopeCheckBox.setSelected(scopeCheckBox.isSelected() && !burndownChartCheckBox.isSelected());
                borderScope.repaint();
            }
        });
        burndownChartInputFormPanel.setBorder(borderBurndownChart);
        burndownChartInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cChart = new GridBagConstraints();
        addBurndownChartFields(cChart);
        // Target
        targetCheckBox.setFocusPainted(false);
        targetCheckBox.setSelected(true);
        ComponentTitledBorder border = new ComponentTitledBorder(targetCheckBox, targetInputFormPanel, new EtchedBorder(), getFont().deriveFont(Font.BOLD));
        targetInputFormPanel.setBorder(border);
        targetInputFormPanel.setLayout(new GridBagLayout());
        addTargetFields();
        cChart.gridx = 0;
        cChart.gridy = 3; // see addBurndownChartFields
        cChart.gridwidth = 2; // see addBurndownChartFields
        burndownChartInputFormPanel.add(targetInputFormPanel, cChart);
        add(burndownChartInputFormPanel, c);
    }

    private void addBurnupChartInputFormPanel() {
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.5;
        // Burnup        
        burnupChartCheckBox.setFocusPainted(false);
        burnupChartCheckBox.setSelected(false);
        ComponentTitledBorder border = new ComponentTitledBorder(burnupChartCheckBox, burnupChartInputFormPanel, new EtchedBorder(), getFont().deriveFont(Font.BOLD));
        burnupChartInputFormPanel.setBorder(border);
        burnupChartInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cChart = new GridBagConstraints();
        addBurndupChartFields(cChart);
        // Target
        burnupTargetCheckBox.setFocusPainted(false);
        burnupTargetCheckBox.setSelected(true);
        border = new ComponentTitledBorder(burnupTargetCheckBox, burnupTargetInputFormPanel, new EtchedBorder(), getFont().deriveFont(Font.BOLD));
        burnupTargetInputFormPanel.setBorder(border);
        burnupTargetInputFormPanel.setLayout(new GridBagLayout());
        addBurnupTargetFields();
        cChart.gridx = 0;
        cChart.gridy = 3; // see addBurndupChartFields
        cChart.gridwidth = 2; // see addBurndupChartFields
        burnupChartInputFormPanel.add(burnupTargetInputFormPanel, cChart);
        // Scope
        scopeCheckBox.setFocusPainted(false);
        scopeCheckBox.setSelected(false);
        scopeCheckBox.addActionListener(new ActionListener() { // no burndown and scope line on the same chart as they share the same axis (X-Axis)

            @Override
            public void actionPerformed(ActionEvent event) {
                burndownChartCheckBox.setSelected(burndownChartCheckBox.isSelected() && !scopeCheckBox.isSelected());
                borderBurndownChart.repaint();
            }
        });
        scopeInputFormPanel.setBorder(borderScope);
        scopeInputFormPanel.setLayout(new GridBagLayout());
        addScopeFields();
        cChart.gridx = 0;
        cChart.gridy = 4; // see addBurndupChartFields
        cChart.gridwidth = 2; // see addBurndupChartFields
        burnupChartInputFormPanel.add(scopeInputFormPanel, cChart);

        add(burnupChartInputFormPanel, c);
    }

    private void addBurndownChartFields(final GridBagConstraints cChart) {
        // Primary Y axis
        // Name
        cChart.gridx = 0;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel primaryYAxisLabel = new FormLabel(
                "Y1-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        primaryYAxisLabel.setMinimumSize(LABEL_DIMENSION);
        primaryYAxisLabel.setPreferredSize(LABEL_DIMENSION);
        burndownChartInputFormPanel.add(primaryYAxisLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        primaryYAxisName = new JTextField();
        primaryYAxisName.setText(defaultPrimaryYAxisName);
        primaryYAxisName.setMinimumSize(COMBO_BOX_DIMENSION);
        primaryYAxisName.setPreferredSize(COMBO_BOX_DIMENSION);
        burndownChartInputFormPanel.add(primaryYAxisName, cChart);
        // Legend
        cChart.gridx = 0;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        FormLabel legendLabel = new FormLabel(
                "X-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        legendLabel.setMinimumSize(LABEL_DIMENSION);
        legendLabel.setPreferredSize(LABEL_DIMENSION);
        burndownChartInputFormPanel.add(legendLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        primaryYAxisLegend = new JTextField();
        primaryYAxisLegend.setText(defaultPrimaryYAxisLegend);
        primaryYAxisLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        primaryYAxisLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        burndownChartInputFormPanel.add(primaryYAxisLegend, cChart);
        // Color
        cChart.gridx = 0;
        cChart.gridy = 2;
        //cChart.weighty = 0.5;
        FormLabel colorLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Color") + ": ");
        colorLabel.setMinimumSize(LABEL_DIMENSION);
        colorLabel.setPreferredSize(LABEL_DIMENSION);
        burndownChartInputFormPanel.add(colorLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 2;
        //cChart.weighty = 0.5;
        cChart.anchor = GridBagConstraints.WEST;
        primaryYAxisColor = new JTextField();
        primaryYAxisColor.setEditable(false);
        primaryYAxisColor.setBackground(defaultPrimaryYAxisColor);
        primaryYAxisColor.setMinimumSize(COLOR_SIZE_DIMENSION);
        primaryYAxisColor.setPreferredSize(COLOR_SIZE_DIMENSION);
        primaryYAxisColor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                        primaryYAxisColor,
                        Labels.getString("BurndownChartPanel.Choose a color"),
                        primaryYAxisColor.getBackground());
                if (newColor != null) {
                    primaryYAxisColor.setBackground(newColor);
                }
            }
        });
        burndownChartInputFormPanel.add(primaryYAxisColor, cChart);
    }

    private void addBurndupChartFields(final GridBagConstraints cChart) {
        // Secondary Y axis
        // Name
        cChart.gridx = 0;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel secondaryYAxisLabel = new FormLabel(
                "Y2-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        secondaryYAxisLabel.setMinimumSize(LABEL_DIMENSION);
        secondaryYAxisLabel.setPreferredSize(LABEL_DIMENSION);
        burnupChartInputFormPanel.add(secondaryYAxisLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        secondaryYAxisName = new JTextField();
        secondaryYAxisName.setText(defaultSecondaryYAxisName);
        secondaryYAxisName.setMinimumSize(COMBO_BOX_DIMENSION);
        secondaryYAxisName.setPreferredSize(COMBO_BOX_DIMENSION);
        burnupChartInputFormPanel.add(secondaryYAxisName, cChart);
        // Legend
        cChart.gridx = 0;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        FormLabel legendLabel = new FormLabel(
                "X-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        legendLabel.setMinimumSize(LABEL_DIMENSION);
        legendLabel.setPreferredSize(LABEL_DIMENSION);
        burnupChartInputFormPanel.add(legendLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        secondaryYAxisLegend = new JTextField();
        secondaryYAxisLegend.setText(defaultSecondaryYAxisLegend);
        secondaryYAxisLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        secondaryYAxisLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        burnupChartInputFormPanel.add(secondaryYAxisLegend, cChart);
        // Color
        cChart.gridx = 0;
        cChart.gridy = 2;
        //cChart.weighty = 0.5;
        FormLabel colorLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Color") + ": ");
        colorLabel.setMinimumSize(LABEL_DIMENSION);
        colorLabel.setPreferredSize(LABEL_DIMENSION);
        burnupChartInputFormPanel.add(colorLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 2;
        //cChart.weighty = 0.5;
        cChart.anchor = GridBagConstraints.WEST;
        secondaryYAxisColor = new JTextField();
        secondaryYAxisColor.setEditable(false);
        secondaryYAxisColor.setBackground(defaultSecondaryYAxisColor);
        secondaryYAxisColor.setMinimumSize(COLOR_SIZE_DIMENSION);
        secondaryYAxisColor.setPreferredSize(COLOR_SIZE_DIMENSION);
        secondaryYAxisColor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                        secondaryYAxisColor,
                        Labels.getString("BurndownChartPanel.Choose a color"),
                        secondaryYAxisColor.getBackground());
                if (newColor != null) {
                    secondaryYAxisColor.setBackground(newColor);
                }
            }
        });
        burnupChartInputFormPanel.add(secondaryYAxisColor, cChart);
    }

    private void addTargetFields() {
        GridBagConstraints cChart = new GridBagConstraints();
        // Target
        // Legend
        cChart.gridx = 0;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel legendLabel = new FormLabel(
                "X-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        legendLabel.setMinimumSize(LABEL_DIMENSION);
        legendLabel.setPreferredSize(LABEL_DIMENSION);
        targetInputFormPanel.add(legendLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        targetLegend = new JTextField();
        targetLegend.setText(defaultTargetLegend);
        targetLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        targetLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        targetInputFormPanel.add(targetLegend, cChart);
        // Color
        cChart.gridx = 0;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        FormLabel colorLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Color") + ": ");
        colorLabel.setMinimumSize(LABEL_DIMENSION);
        colorLabel.setPreferredSize(LABEL_DIMENSION);
        targetInputFormPanel.add(colorLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        cChart.anchor = GridBagConstraints.WEST;
        targetColor = new JTextField();
        targetColor.setEditable(false);
        targetColor.setBackground(defaultTargetColor);
        targetColor.setMinimumSize(COLOR_SIZE_DIMENSION);
        targetColor.setPreferredSize(COLOR_SIZE_DIMENSION);
        targetColor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                        targetColor,
                        Labels.getString("BurndownChartPanel.Choose a color"),
                        targetColor.getBackground());
                if (newColor != null) {
                    targetColor.setBackground(newColor);
                }
            }
        });
        targetInputFormPanel.add(targetColor, cChart);
    }

    private void addBurnupTargetFields() {
        GridBagConstraints cChart = new GridBagConstraints();
        // Target
        // Legend
        cChart.gridx = 0;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel legendLabel = new FormLabel(
                "X-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        legendLabel.setMinimumSize(LABEL_DIMENSION);
        legendLabel.setPreferredSize(LABEL_DIMENSION);
        burnupTargetInputFormPanel.add(legendLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        burnupTargetLegend = new JTextField();
        burnupTargetLegend.setText(defaultBurnupTargetLegend);
        burnupTargetLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        burnupTargetLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        burnupTargetInputFormPanel.add(burnupTargetLegend, cChart);
        // Color
        cChart.gridx = 0;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        FormLabel colorLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Color") + ": ");
        colorLabel.setMinimumSize(LABEL_DIMENSION);
        colorLabel.setPreferredSize(LABEL_DIMENSION);
        burnupTargetInputFormPanel.add(colorLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        cChart.anchor = GridBagConstraints.WEST;
        burnupTargetColor = new JTextField();
        burnupTargetColor.setEditable(false);
        burnupTargetColor.setBackground(defaultBurnupTargetColor);
        burnupTargetColor.setMinimumSize(COLOR_SIZE_DIMENSION);
        burnupTargetColor.setPreferredSize(COLOR_SIZE_DIMENSION);
        burnupTargetColor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                        burnupTargetColor,
                        Labels.getString("BurndownChartPanel.Choose a color"),
                        burnupTargetColor.getBackground());
                if (newColor != null) {
                    burnupTargetColor.setBackground(newColor);
                }
            }
        });
        burnupTargetInputFormPanel.add(burnupTargetColor, cChart);
    }

    private void addScopeFields() {
        GridBagConstraints cChart = new GridBagConstraints();
        // Legend
        cChart.gridx = 0;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel legendLabel = new FormLabel(
                "X-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        legendLabel.setMinimumSize(LABEL_DIMENSION);
        legendLabel.setPreferredSize(LABEL_DIMENSION);
        scopeInputFormPanel.add(legendLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        scopeLegend = new JTextField();
        scopeLegend.setText(defaultScopeLegend);
        scopeLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        scopeLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        scopeInputFormPanel.add(scopeLegend, cChart);
        // Color
        cChart.gridx = 0;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        FormLabel colorLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Color") + ": ");
        colorLabel.setMinimumSize(LABEL_DIMENSION);
        colorLabel.setPreferredSize(LABEL_DIMENSION);
        scopeInputFormPanel.add(colorLabel, cChart);
        cChart.gridx = 1;
        cChart.gridy = 1;
        //cChart.weighty = 0.5;
        cChart.anchor = GridBagConstraints.WEST;
        scopeColor = new JTextField();
        scopeColor.setEditable(false);
        scopeColor.setBackground(defaultScopeColor);
        scopeColor.setMinimumSize(COLOR_SIZE_DIMENSION);
        scopeColor.setPreferredSize(COLOR_SIZE_DIMENSION);
        scopeColor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                        scopeColor,
                        Labels.getString("BurndownChartPanel.Choose a color"),
                        scopeColor.getBackground());
                if (newColor != null) {
                    scopeColor.setBackground(newColor);
                }
            }
        });
        scopeInputFormPanel.add(scopeColor, cChart);
    }

    public JCheckBox getBurndownChartCheckBox() {
        return burndownChartCheckBox;
    }

    public JCheckBox getBurnupChartCheckBox() {
        return burnupChartCheckBox;
    }

    public JCheckBox getTargetCheckBox() {
        return targetCheckBox;
    }

    public JCheckBox getBurnupTargetCheckBox() {
        return burnupTargetCheckBox;
    }

    public JCheckBox getScopeCheckBox() {
        return scopeCheckBox;
    }

    public Color getPrimaryYAxisColor() {
        return primaryYAxisColor.getBackground();
    }

    public Color getTargetColor() {
        return targetColor.getBackground();
    }

    public Color getSecondaryYAxisColor() {
        return secondaryYAxisColor.getBackground();
    }

    public Color getScopeColor() {
        return scopeColor.getBackground();
    }

    public Color getBurnupTargetColor() {
        return burnupTargetColor.getBackground();
    }

    public String getPrimaryYAxisName() {
        return primaryYAxisName.getText();
    }

    public String getSecondaryYAxisName() {
        return secondaryYAxisName.getText();
    }

    public String getTargetLegend() {
        return targetLegend.getText();
    }

    public String getBurnupTargetLegend() {
        return burnupTargetLegend.getText();
    }

    public String getScopeLegend() {
        return scopeLegend.getText();
    }

    public String getPrimaryYAxisLegend() {
        return primaryYAxisLegend.getText();
    }

    public String getSecondaryYAxisLegend() {
        return secondaryYAxisLegend.getText();
    }

}
