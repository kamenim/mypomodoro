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
package org.mypomodoro.gui.burndownchart;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import org.mypomodoro.Main;
import org.mypomodoro.gui.activities.AbstractComboBoxRenderer;
import org.mypomodoro.gui.burndownchart.types.EffectiveHourChart;
import org.mypomodoro.gui.burndownchart.types.IChartType;
import org.mypomodoro.gui.burndownchart.types.PlainHourChart;
import org.mypomodoro.gui.burndownchart.types.PomodoroChart;
import org.mypomodoro.gui.burndownchart.types.StoryPointChart;
import org.mypomodoro.gui.burndownchart.types.TaskChart;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ComponentTitledBorder;
import org.mypomodoro.util.Labels;

/**
 * Choose Chart type form
 *
 */
public class ChooseInputForm extends JPanel {

    private static final Dimension COMBO_BOX_DIMENSION = new Dimension(300, 25);
    private static final Dimension COLOR_SIZE_DIMENSION = new Dimension(60, 20);
    private final GridBagConstraints c = new GridBagConstraints();
    // Burndown Chart form
    private final JPanel burndownChartInputFormPanel = new JPanel();
    private final JPanel burndownChartTypeLegendInputFormPanel = new JPanel();
    private JTextField primaryYAxisName = new JTextField();
    private String defaultPrimaryYAxisName = "";
    private JTextField primaryYAxisLegend = new JTextField();
    private String defaultPrimaryYAxisLegend = "";
    private JPanel primaryYAxisColor = new JPanel(); // use of JPanel instead of JTextField because doesn't appear when setEditable is false and using Nimrod theme
    private final Color defaultPrimaryYAxisColor = ColorUtil.YELLOW_CHART;
    final JCheckBox burndownChartCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Burndown Chart"), true);
    private final ComponentTitledBorder borderBurndownChart = new ComponentTitledBorder(burndownChartCheckBox, burndownChartInputFormPanel, new EtchedBorder(), burndownChartCheckBox.getFont().deriveFont(Font.BOLD));
    private final JComboBox chartTypesBurndownComboBox = new JComboBox();
    private final JCheckBox burndownChartPercentageCheckBox = new JCheckBox("%");
    // Burndown Target Line form
    private final JPanel targetInputFormPanel = new JPanel();
    private JTextField targetLegend = new JTextField();
    private final String defaultTargetLegend = Labels.getString("BurndownChartPanel.Target");
    private JPanel targetColor = new JPanel();
    private final Color defaultTargetColor = ColorUtil.BLACK;
    private final JCheckBox targetCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Target"), true);
    ComponentTitledBorder borderTarget = new ComponentTitledBorder(targetCheckBox, targetInputFormPanel, new EtchedBorder(), targetCheckBox.getFont().deriveFont(Font.BOLD));
    // Burn-up Chart form
    private final JPanel burnupChartInputFormPanel = new JPanel();
    private final JPanel burnupChartTypeLegendInputFormPanel = new JPanel();
    private JTextField secondaryYAxisName = new JTextField();
    private String defaultSecondaryYAxisName = "";
    private JTextField secondaryYAxisLegend = new JTextField();
    private String defaultSecondaryYAxisLegend = "";
    private JPanel secondaryYAxisColor = new JPanel();
    private final Color defaultSecondaryYAxisColor = ColorUtil.RED_CHART;
    final JCheckBox burnupChartCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Burn-up Chart"), true);
    private final ComponentTitledBorder borderBurnupChart = new ComponentTitledBorder(burnupChartCheckBox, burnupChartInputFormPanel, new EtchedBorder(), burnupChartCheckBox.getFont().deriveFont(Font.BOLD));
    private final JComboBox chartTypesBurnupComboBox = new JComboBox();
    private final JCheckBox burnupChartPercentageCheckBox = new JCheckBox("%");
    // Burn-up Guide Line form
    private final JPanel burnupGuideInputFormPanel = new JPanel();
    private JTextField burnupGuideLegend = new JTextField();
    private final String defaultBurnupGuideLegend = Labels.getString("BurndownChartPanel.Guide");
    private JPanel burnupGuideColor = new JPanel();
    private final Color defaultBurnupGuideColor = ColorUtil.BLACK;
    private final JCheckBox burnupGuideCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Guide"), true);
    ComponentTitledBorder borderGuide = new ComponentTitledBorder(burnupGuideCheckBox, burnupGuideInputFormPanel, new EtchedBorder(), burnupGuideCheckBox.getFont().deriveFont(Font.BOLD));
    // Burn-up Scope Line form
    private final JPanel scopeInputFormPanel = new JPanel();
    private JTextField scopeLegend = new JTextField();
    private final String defaultScopeLegend = Labels.getString("BurndownChartPanel.Scope");
    private JPanel scopeColor = new JPanel();
    private final Color defaultScopeColor = ColorUtil.BLACK;
    private final JCheckBox scopeCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Scope"), true);
    private final ComponentTitledBorder borderScope = new ComponentTitledBorder(scopeCheckBox, scopeInputFormPanel, new EtchedBorder(), scopeCheckBox.getFont().deriveFont(Font.BOLD));

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
        cChart.weightx = 1;
        cChart.weighty = 1;
        cChart.fill = GridBagConstraints.CENTER;
        // Type and legends
        burndownChartTypeLegendInputFormPanel.setLayout(new GridBagLayout());
        addBurndownChartFields(cChart);
        // Target
        targetCheckBox.setFocusPainted(false);
        targetCheckBox.setSelected(true);
        targetInputFormPanel.setBorder(borderTarget);
        targetInputFormPanel.setLayout(new GridBagLayout());
        addTargetFields(cChart);
        add(burndownChartInputFormPanel, c);
    }

    private void addBurnupChartInputFormPanel() {
        c.gridx = 0;
        c.gridy = 1;
        // Burnup        
        burnupChartCheckBox.setFocusPainted(false);
        burnupChartCheckBox.setSelected(false);
        burnupChartInputFormPanel.setBorder(borderBurnupChart);
        burnupChartInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cChart = new GridBagConstraints();
        cChart.weightx = 1;
        cChart.weighty = 1;
        cChart.fill = GridBagConstraints.CENTER;
        burnupChartTypeLegendInputFormPanel.setLayout(new GridBagLayout());
        addBurnupChartFields(cChart);
        // Guide
        burnupGuideCheckBox.setFocusPainted(false);
        burnupGuideCheckBox.setSelected(true);
        burnupGuideInputFormPanel.setBorder(borderGuide);
        burnupGuideInputFormPanel.setLayout(new GridBagLayout());
        addBurnupGuideFields(cChart);
        // Scope
        scopeCheckBox.setFocusPainted(false);
        scopeCheckBox.setSelected(false);
        scopeCheckBox.addActionListener(new ActionListener() { // no burndown and scope line on the same chart as they share the same axis (X-Axis)

            @Override
            public void actionPerformed(ActionEvent event) {
                burndownChartCheckBox.setSelected(burndownChartCheckBox.isSelected() && !scopeCheckBox.isSelected());
                burnupChartCheckBox.setSelected(true);
                borderBurndownChart.repaint();
                borderBurnupChart.repaint();
            }
        });
        scopeInputFormPanel.setBorder(borderScope);
        scopeInputFormPanel.setLayout(new GridBagLayout());
        addScopeFields(cChart);
        add(burnupChartInputFormPanel, c);
    }

    private void addBurndownChartFields(final GridBagConstraints cChart) {
        cChart.gridx = 0;
        cChart.gridy = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.gridheight = 3;
        // Primary Y axis
        // Types
        chartTypesBurndownComboBox.setRenderer(new AbstractComboBoxRenderer());
        PomodoroChart pomodoroChart = new PomodoroChart();
        if (Main.preferences.getAgileMode()) {
            StoryPointChart storyPointChart = new StoryPointChart();
            chartTypesBurndownComboBox.addItem(storyPointChart);
            defaultPrimaryYAxisName = storyPointChart.getYLegend();
            defaultPrimaryYAxisLegend = storyPointChart.getXLegend();
        } else {
            defaultPrimaryYAxisName = pomodoroChart.getYLegend();
            defaultPrimaryYAxisLegend = pomodoroChart.getXLegend();
        }
        chartTypesBurndownComboBox.addItem(pomodoroChart);
        chartTypesBurndownComboBox.addItem(new TaskChart());
        chartTypesBurndownComboBox.addItem(new EffectiveHourChart());
        chartTypesBurndownComboBox.addItem(new PlainHourChart());
        chartTypesBurndownComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                primaryYAxisName.setText(((IChartType) chartTypesBurndownComboBox.getSelectedItem()).getYLegend());
                primaryYAxisLegend.setText(((IChartType) chartTypesBurndownComboBox.getSelectedItem()).getYLegend());
                if (burndownChartPercentageCheckBox.isSelected()) {
                    primaryYAxisName.setText(primaryYAxisName.getText().trim() + " %");
                    primaryYAxisLegend.setText(primaryYAxisLegend.getText().trim() + " %");
                }
                burndownChartCheckBox.setSelected(true);
                borderBurndownChart.repaint();
                scopeCheckBox.setSelected(false);
                borderScope.repaint();
            }
        });
        burndownChartTypeLegendInputFormPanel.add(chartTypesBurndownComboBox, gbc);
        // Percentage
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        burndownChartPercentageCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (burndownChartPercentageCheckBox.isSelected()) {
                    primaryYAxisName.setText(primaryYAxisName.getText().trim() + " %");
                    primaryYAxisLegend.setText(primaryYAxisLegend.getText().trim() + " %");
                    targetLegend.setText(targetLegend.getText().trim() + " %");
                } else {
                    primaryYAxisName.setText(primaryYAxisName.getText().trim().replace("%", ""));
                    primaryYAxisLegend.setText(primaryYAxisLegend.getText().trim().replace("%", ""));
                    targetLegend.setText(targetLegend.getText().trim().replace("%", ""));
                }
            }
        });
        burndownChartTypeLegendInputFormPanel.add(burndownChartPercentageCheckBox, gbc);
        // Name
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        FormLabel primaryYAxisLabel = new FormLabel(
                "Y-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        burndownChartTypeLegendInputFormPanel.add(primaryYAxisLabel, gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        primaryYAxisName = new JTextField();
        primaryYAxisName.setText(defaultPrimaryYAxisName);
        primaryYAxisName.setMinimumSize(COMBO_BOX_DIMENSION);
        primaryYAxisName.setPreferredSize(COMBO_BOX_DIMENSION);
        burndownChartTypeLegendInputFormPanel.add(primaryYAxisName, gbc);
        // Legend
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        FormLabel legendLabel = new FormLabel(
                "X-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        burndownChartTypeLegendInputFormPanel.add(legendLabel, gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        primaryYAxisLegend = new JTextField();
        primaryYAxisLegend.setText(defaultPrimaryYAxisLegend);
        primaryYAxisLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        primaryYAxisLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        burndownChartTypeLegendInputFormPanel.add(primaryYAxisLegend, gbc);
        // Color
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        FormLabel colorLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Color") + ": ");
        burndownChartTypeLegendInputFormPanel.add(colorLabel, gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        primaryYAxisColor = new JPanel();
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
        burndownChartTypeLegendInputFormPanel.add(primaryYAxisColor, gbc);
        burndownChartInputFormPanel.add(burndownChartTypeLegendInputFormPanel, cChart);
    }

    private void addBurnupChartFields(final GridBagConstraints cChart) {
        cChart.gridx = 0;
        cChart.gridy = 0;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 10, 0, 10);
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.weighty = 1;
        // Secondary Y axis        
        // Types
        chartTypesBurnupComboBox.setRenderer(new AbstractComboBoxRenderer());
        if (Main.preferences.getAgileMode()) {
            chartTypesBurnupComboBox.addItem(new StoryPointChart());
            defaultSecondaryYAxisName = new StoryPointChart().getYLegend();
            defaultSecondaryYAxisLegend = new StoryPointChart().getXLegend();
        } else {
            defaultSecondaryYAxisName = new PomodoroChart().getYLegend();
            defaultSecondaryYAxisLegend = new PomodoroChart().getXLegend();
        }
        chartTypesBurnupComboBox.addItem(new PomodoroChart());
        chartTypesBurnupComboBox.addItem(new TaskChart());
        chartTypesBurnupComboBox.addItem(new EffectiveHourChart());
        chartTypesBurnupComboBox.addItem(new PlainHourChart());
        chartTypesBurnupComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                secondaryYAxisName.setText(((IChartType) chartTypesBurnupComboBox.getSelectedItem()).getYLegend());
                secondaryYAxisLegend.setText(((IChartType) chartTypesBurnupComboBox.getSelectedItem()).getYLegend());
                if (burnupChartPercentageCheckBox.isSelected()) {
                    secondaryYAxisName.setText(secondaryYAxisName.getText().trim() + " %");
                    secondaryYAxisLegend.setText(secondaryYAxisLegend.getText().trim() + " %");
                }
                burnupChartCheckBox.setSelected(true);
                borderBurnupChart.repaint();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        burnupChartTypeLegendInputFormPanel.add(chartTypesBurnupComboBox, gbc);
        // Percentage        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        burnupChartPercentageCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (burnupChartPercentageCheckBox.isSelected()) {
                    secondaryYAxisName.setText(secondaryYAxisName.getText().trim() + " %");
                    secondaryYAxisLegend.setText(secondaryYAxisLegend.getText().trim() + " %");
                    burnupGuideLegend.setText(burnupGuideLegend.getText().trim() + " %");
                    scopeLegend.setText(scopeLegend.getText().trim() + " %");
                } else {
                    secondaryYAxisName.setText(secondaryYAxisName.getText().trim().replace("%", ""));
                    secondaryYAxisLegend.setText(secondaryYAxisLegend.getText().trim().replace("%", ""));
                    burnupGuideLegend.setText(burnupGuideLegend.getText().trim().replace("%", ""));
                    scopeLegend.setText(scopeLegend.getText().trim().replace("%", ""));
                }
            }
        });
        burnupChartTypeLegendInputFormPanel.add(burnupChartPercentageCheckBox, gbc);
        // Name
        gbc.insets = new Insets(0, 0, 0, 10);
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        //cChart.weighty = 0.5;
        FormLabel secondaryYAxisLabel = new FormLabel(
                "Y-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        burnupChartTypeLegendInputFormPanel.add(secondaryYAxisLabel, gbc);
        gbc.gridx = 3;
        gbc.gridy = 0;
        //cChart.weighty = 0.5;
        secondaryYAxisName = new JTextField();
        secondaryYAxisName.setText(defaultSecondaryYAxisName);
        secondaryYAxisName.setMinimumSize(COMBO_BOX_DIMENSION);
        secondaryYAxisName.setPreferredSize(COMBO_BOX_DIMENSION);
        burnupChartTypeLegendInputFormPanel.add(secondaryYAxisName, gbc);
        // Legend
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridheight = 1;
        //cChart.weighty = 0.5;
        FormLabel legendLabel = new FormLabel(
                "X-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        burnupChartTypeLegendInputFormPanel.add(legendLabel, gbc);
        gbc.gridx = 3;
        gbc.gridy = 1;
        //cChart.weighty = 0.5;
        secondaryYAxisLegend = new JTextField();
        secondaryYAxisLegend.setText(defaultSecondaryYAxisLegend);
        secondaryYAxisLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        secondaryYAxisLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        burnupChartTypeLegendInputFormPanel.add(secondaryYAxisLegend, gbc);
        // Color
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridheight = 1;
        //cChart.weighty = 0.5;
        FormLabel colorLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Color") + ": ");
        burnupChartTypeLegendInputFormPanel.add(colorLabel, gbc);
        gbc.gridx = 3;
        gbc.gridy = 2;
        //cChart.weighty = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        secondaryYAxisColor = new JPanel();
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
        burnupChartTypeLegendInputFormPanel.add(secondaryYAxisColor, gbc);
        burnupChartInputFormPanel.add(burnupChartTypeLegendInputFormPanel, cChart);
    }

    private void addTargetFields(final GridBagConstraints cChart) {
        cChart.gridx = 0;
        cChart.gridy = 1;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 5, 2, 5);
        //gbc.anchor = GridBagConstraints.REMAINDER;
        // Target
        // Legend
        gbc.gridx = 0;
        gbc.gridy = 0;
        FormLabel legendLabel = new FormLabel(
                "X-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        targetInputFormPanel.add(legendLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        targetLegend = new JTextField();
        targetLegend.setText(defaultTargetLegend);
        targetLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        targetLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        targetInputFormPanel.add(targetLegend, gbc);
        // Color
        gbc.gridx = 0;
        gbc.gridy = 1;
        FormLabel colorLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Color") + ": ");
        targetInputFormPanel.add(colorLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        targetColor = new JPanel();
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
        targetInputFormPanel.add(targetColor, gbc);
        burndownChartInputFormPanel.add(targetInputFormPanel, cChart);
    }

    private void addBurnupGuideFields(final GridBagConstraints cChart) {
        cChart.gridx = 0;
        cChart.gridy = 1;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 5, 2, 5);
        // Target
        // Legend
        gbc.gridx = 0;
        gbc.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel legendLabel = new FormLabel(
                "X-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        burnupGuideInputFormPanel.add(legendLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        //cChart.weighty = 0.5;
        burnupGuideLegend = new JTextField();
        burnupGuideLegend.setText(defaultBurnupGuideLegend);
        burnupGuideLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        burnupGuideLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        burnupGuideInputFormPanel.add(burnupGuideLegend, gbc);
        // Color
        gbc.gridx = 0;
        gbc.gridy = 1;
        //cChart.weighty = 0.5;
        FormLabel colorLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Color") + ": ");
        burnupGuideInputFormPanel.add(colorLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        //cChart.weighty = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        burnupGuideColor = new JPanel();
        burnupGuideColor.setBackground(defaultBurnupGuideColor);
        burnupGuideColor.setMinimumSize(COLOR_SIZE_DIMENSION);
        burnupGuideColor.setPreferredSize(COLOR_SIZE_DIMENSION);
        burnupGuideColor.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                Color newColor = JColorChooser.showDialog(
                        burnupGuideColor,
                        Labels.getString("BurndownChartPanel.Choose a color"),
                        burnupGuideColor.getBackground());
                if (newColor != null) {
                    burnupGuideColor.setBackground(newColor);
                }
            }
        });
        burnupGuideInputFormPanel.add(burnupGuideColor, gbc);
        burnupChartInputFormPanel.add(burnupGuideInputFormPanel, cChart);
    }

    private void addScopeFields(final GridBagConstraints cChart) {
        cChart.gridx = 0;
        cChart.gridy = 2;
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.anchor = GridBagConstraints.NORTH;
        gbc.insets = new Insets(0, 5, 2, 5);
        // Legend
        gbc.gridx = 0;
        gbc.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel legendLabel = new FormLabel(
                "X-" + Labels.getString("BurndownChartPanel.Legend") + ": ");
        scopeInputFormPanel.add(legendLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        //cChart.weighty = 0.5;
        scopeLegend = new JTextField();
        scopeLegend.setText(defaultScopeLegend);
        scopeLegend.setMinimumSize(COMBO_BOX_DIMENSION);
        scopeLegend.setPreferredSize(COMBO_BOX_DIMENSION);
        scopeInputFormPanel.add(scopeLegend, gbc);
        // Color
        gbc.gridx = 0;
        gbc.gridy = 1;
        //cChart.weighty = 0.5;
        FormLabel colorLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Color") + ": ");
        scopeInputFormPanel.add(colorLabel, gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        //cChart.weighty = 0.5;
        gbc.anchor = GridBagConstraints.WEST;
        scopeColor = new JPanel();
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
        scopeInputFormPanel.add(scopeColor, gbc);
        burnupChartInputFormPanel.add(scopeInputFormPanel, cChart);
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

    public JCheckBox getBurnupGuideCheckBox() {
        return burnupGuideCheckBox;
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

    public Color getBurnupGuideColor() {
        return burnupGuideColor.getBackground();
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

    public String getBurnupGuideLegend() {
        return burnupGuideLegend.getText();
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

    public IChartType getBurndownChartType() {
        return (IChartType) chartTypesBurndownComboBox.getSelectedItem();
    }

    public IChartType getBurnupChartType() {
        return (IChartType) chartTypesBurnupComboBox.getSelectedItem();
    }

    public JCheckBox getBurndownChartPercentageCheckBox() {
        return burndownChartPercentageCheckBox;
    }

    public JCheckBox getBurnupChartPercentageCheckBox() {
        return burnupChartPercentageCheckBox;
    }
}
