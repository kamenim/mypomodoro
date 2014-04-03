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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import org.mypomodoro.gui.PreferencesPanel;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.ComponentTitledBorder;
import org.mypomodoro.util.DatePicker;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * Export form
 *
 */
public class CreateInputForm extends JPanel {

    private static final long serialVersionUID = 20110814L;
    protected static final Dimension LABEL_DIMENSION = new Dimension(170, 20);
    private static final Dimension COMBO_BOX_DIMENSION = new Dimension(300, 20);
    private static final Dimension IMAGE_SIZE_DIMENSION = new Dimension(30, 20);
    private static final Dimension COLOR_SIZE_DIMENSION = new Dimension(60, 20);
    private final GridBagConstraints c = new GridBagConstraints();
    // Dates form
    private final JPanel datesInputFormPanel = new JPanel();
    protected final DatePicker startDatePicker = new DatePicker(Labels.getLocale());
    protected final DatePicker endDatePicker = new DatePicker(Labels.getLocale());
    private final JCheckBox excludeSaturdays = new JCheckBox(Labels.getString("BurndownChartPanel.Saturdays"), true);
    private final JCheckBox excludeSundays = new JCheckBox(Labels.getString("BurndownChartPanel.Sundays"), true);
    private final JCheckBox excludeToDos = new JCheckBox(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List"), false);
    private final DatePicker excludeDatePicker = new DatePicker(Labels.getLocale());
    private final ArrayList<Date> excludedDates = new ArrayList<Date>();
    final JCheckBox datesCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Dates"), true);
    // Iterations form
    private final JPanel iterationsInputFormPanel = new JPanel();
    private final JComboBox startIteration = new JComboBox();
    private final JComboBox endIteration = new JComboBox();
    final JCheckBox iterationsCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Iterations"), true);
    // Burndown Chart form
    private final JPanel burndownChartInputFormPanel = new JPanel();
    private JTextField primaryYAxisName = new JTextField();
    private final String defaultPrimaryYAxisName = Labels.getString("BurndownChartPanel.Story Points");
    private JTextField primaryYAxisLegend = new JTextField();
    private final String defaultPrimaryYAxisLegend = Labels.getString("BurndownChartPanel.Story Points");
    private JTextField primaryYAxisColor = new JTextField();
    private final Color defaultPrimaryYAxisColor = ColorUtil.YELLOW_CHART;
    final JCheckBox burndownChartCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Burndown Chart"), true);
    // Burn-up Chart form
    private final JPanel burnupChartInputFormPanel = new JPanel();
    private JTextField secondaryYAxisName = new JTextField();
    private final String defaultSecondaryYAxisName = Labels.getString("BurndownChartPanel.Completed tasks %");
    private JTextField secondaryYAxisLegend = new JTextField();
    private final String defaultSecondaryYAxisLegend = Labels.getString("BurndownChartPanel.Completed");
    private JTextField secondaryYAxisColor = new JTextField();
    private final Color defaultSecondaryYAxisColor = ColorUtil.RED_CHART;
    final JCheckBox burnupChartCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Burn-up Chart"), true);
    // Target Chart form
    private final JPanel targetInputFormPanel = new JPanel();
    private JTextField targetLegend = new JTextField();
    private final String defaultTargetLegend = Labels.getString("BurndownChartPanel.Target");
    private JTextField targetColor = new JTextField();
    private final Color defaultTargetColor = ColorUtil.BLACK;
    private final JCheckBox targetCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Target"), true);
    // Image form
    /*private final JPanel imageInputFormPanel = new JPanel();
     private final JPanel imageSizePanel = new JPanel();
     private JTextField imageName = new JTextField();
     private final String defaultImageName = "myAgilePomodoro";
     private JComboBox imageFormatComboBox = new JComboBox();
     private final ImageFormat PNGFormat = new ImageFormat("PNG",
     ImageFormat.PNGExtention);
     private final ImageFormat JPGFormat = new ImageFormat("JPG",
     ImageFormat.JPGExtention);
     private final JTextField imageWidth = new JTextField();
     private final JTextField imageHeight = new JTextField();
     private final int defaultImageWidth = 800;
     private final int defaultImageHeight = 600;
     private final JCheckBox imageCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Image"), true);*/
    final ComponentTitledBorder borderIterations = new ComponentTitledBorder(iterationsCheckBox, iterationsInputFormPanel, BorderFactory.createEtchedBorder());
    final ComponentTitledBorder borderDates = new ComponentTitledBorder(datesCheckBox, datesInputFormPanel, BorderFactory.createEtchedBorder());

    public CreateInputForm() {
        setLayout(new GridBagLayout());
        // The following three lines are necessary to make the additional jpanels to fill up the parent jpanel
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        addDatesInputFormPanel();
        addIterationsInputFormPanel();
        addBurndownChartInputFormPanel();
        addBurnupChartInputFormPanel();
        addTargetInputFormPanel();
        //addImageInputFormPanel();
    }

    private void addDatesInputFormPanel() {
        c.gridx = 0;
        c.gridy = 0;
        c.weightx = 1.0;
        c.weighty = 0.5;
        //c.gridwidth = 2;        
        datesCheckBox.setFocusPainted(false);
        datesCheckBox.setSelected(true);      
        datesCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                datesCheckBox.setSelected(true);
                iterationsCheckBox.setSelected(false);
                borderIterations.repaint();
            }
        });
        datesInputFormPanel.setBorder(borderDates);
        datesInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cChart = new GridBagConstraints();
        addDatesFields(cChart);
        add(datesInputFormPanel, c);
    }

    private void addIterationsInputFormPanel() {
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.0;
        c.weighty = 0.5;
        //c.gridwidth = 2;        
        iterationsCheckBox.setFocusPainted(false);
        iterationsCheckBox.setSelected(false);
        iterationsCheckBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                iterationsCheckBox.setSelected(true);
                datesCheckBox.setSelected(false);
                borderDates.repaint();
            }
        });
        iterationsInputFormPanel.setBorder(borderIterations);
        iterationsInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cChart = new GridBagConstraints();
        addIterationsFields(cChart);
        add(iterationsInputFormPanel, c);
    }

    private void addBurndownChartInputFormPanel() {
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0;
        c.weighty = 0.5;
        //c.gridwidth = 2;        
        burndownChartCheckBox.setFocusPainted(false);
        ComponentTitledBorder border = new ComponentTitledBorder(burndownChartCheckBox, burndownChartInputFormPanel, BorderFactory.createEtchedBorder());
        burndownChartInputFormPanel.setBorder(border);
        burndownChartInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cChart = new GridBagConstraints();
        addBurndownChartFields(cChart);
        add(burndownChartInputFormPanel, c);
    }

    private void addBurnupChartInputFormPanel() {
        c.gridx = 0;
        c.gridy = 3;
        c.weightx = 1.0;
        c.weighty = 0.5;
        //c.gridwidth = 2;        
        burnupChartCheckBox.setFocusPainted(false);
        ComponentTitledBorder border = new ComponentTitledBorder(burnupChartCheckBox, burnupChartInputFormPanel, BorderFactory.createEtchedBorder());
        burnupChartInputFormPanel.setBorder(border);
        burnupChartInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cChart = new GridBagConstraints();
        addBurndupChartFields(cChart);
        add(burnupChartInputFormPanel, c);
    }

    private void addTargetInputFormPanel() {
        c.gridx = 0;
        c.gridy = 4;
        c.weightx = 1.0;
        c.weighty = 0.5;
        //c.gridwidth = 2;
        targetCheckBox.setFocusPainted(false);
        ComponentTitledBorder border = new ComponentTitledBorder(targetCheckBox, targetInputFormPanel, BorderFactory.createEtchedBorder());
        targetInputFormPanel.setBorder(border);
        targetInputFormPanel.setLayout(new GridBagLayout());
        GridBagConstraints cChart = new GridBagConstraints();
        addTargetFields(cChart);
        add(targetInputFormPanel, c);
    }

    /*private void addImageInputFormPanel() {
     c.gridx = 0;
     c.gridy = 5;
     c.weightx = 1.0;
     c.weighty = 0.5;
     //c.gridwidth = 2;
     imageCheckBox.setFocusPainted(false);
     ComponentTitledBorder border = new ComponentTitledBorder(imageCheckBox, imageInputFormPanel, BorderFactory.createEtchedBorder());
     imageInputFormPanel.setBorder(border);
     imageInputFormPanel.setLayout(new GridBagLayout());
     GridBagConstraints cImage = new GridBagConstraints();
     addImageFields(cImage);
     add(imageInputFormPanel, c);
     }*/
    private void addDatesFields(GridBagConstraints cChart) {
        cChart.gridx = 0;
        cChart.gridy = 0;
        cChart.weightx = 1.0;
        cChart.weighty = 0.5;
        // Dates
        JPanel dates = new JPanel();
        dates.setLayout(new GridBagLayout());
        GridBagConstraints datesgbc = new GridBagConstraints();
        FormLabel dateslabel = new FormLabel(Labels.getString("BurndownChartPanel.Dates") + "*: ");
        dateslabel.setMinimumSize(LABEL_DIMENSION);
        dateslabel.setPreferredSize(LABEL_DIMENSION);
        datesgbc.gridx = 0;
        datesgbc.gridy = 0;
        datesgbc.weightx = 1.0;
        datesgbc.weighty = 0.5;
        dates.add(dateslabel, datesgbc);
        datesgbc.gridx = 1;
        datesgbc.gridy = 0;
        datesgbc.weightx = 1.0;
        datesgbc.weighty = 0.5;
        startDatePicker.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // if end date is sooner than start date set end date to start date
                if (DateUtil.isSooner(endDatePicker.getDate(), startDatePicker.getDate())) {
                    endDatePicker.setDate(startDatePicker.getDate());
                }
                endDatePicker.setDateWithLowerBounds(startDatePicker.getDate());
            }
        });
        dates.add(startDatePicker, datesgbc);
        datesgbc.gridx = 2;
        datesgbc.gridy = 0;
        datesgbc.weightx = 1.0;
        datesgbc.weighty = 0.5;
        endDatePicker.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // if end date is sooner than start date set start date to end date
                if (DateUtil.isSooner(endDatePicker.getDate(), startDatePicker.getDate())) {
                    startDatePicker.setDate(endDatePicker.getDate());
                }
                startDatePicker.setDateWithUpperBounds(endDatePicker.getDate());
            }
        });
        dates.add(endDatePicker, datesgbc);
        datesInputFormPanel.add(dates, cChart);
        // Exclusion
        cChart.gridx = 0;
        cChart.gridy = 1;
        cChart.weightx = 1.0;
        cChart.weighty = 0.5;
        // first line
        JPanel exclusion = new JPanel();
        exclusion.setLayout(new GridBagLayout());
        GridBagConstraints exclusiongbc = new GridBagConstraints();
        FormLabel exclusionlabel = new FormLabel(Labels.getString("BurndownChartPanel.Exclusion") + "*: ");
        exclusionlabel.setMinimumSize(LABEL_DIMENSION);
        exclusionlabel.setPreferredSize(LABEL_DIMENSION);
        exclusiongbc.gridx = 0;
        exclusiongbc.gridy = 0;
        exclusiongbc.weightx = 1.0;
        exclusiongbc.weighty = 0.5;
        exclusion.add(exclusionlabel, exclusiongbc);
        exclusiongbc.gridx = 1;
        exclusiongbc.gridy = 0;
        exclusiongbc.weightx = 1.0;
        exclusiongbc.weighty = 0.5;
        exclusion.add(excludeSaturdays, exclusiongbc);
        exclusiongbc.gridx = 2;
        exclusiongbc.gridy = 0;
        exclusiongbc.weightx = 1.0;
        exclusiongbc.weighty = 0.5;
        exclusion.add(excludeSundays, exclusiongbc);
        exclusiongbc.gridx = 3;
        exclusiongbc.gridy = 0;
        exclusiongbc.weightx = 1.0;
        exclusiongbc.weighty = 0.5;
        exclusion.add(excludeToDos, exclusiongbc); // excludes ToDos/Tasks of ToDo list/Iteration Backlog
        // second line
        exclusiongbc.gridx = 0;
        exclusiongbc.gridy = 1;
        exclusiongbc.weightx = 1.0;
        exclusiongbc.weighty = 0.5;
        final JLabel excludedDatesLabel = new JLabel();
        excludeDatePicker.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                excludedDates.add(excludeDatePicker.getDate());
                String text = "";
                int increment = 1;
                for (Date date : excludedDates) {
                    if (increment > 1) {
                        text += ", ";
                    }
                    text += DateUtil.getFormatedDate(date);
                    increment++;
                }
                excludedDatesLabel.setText(text);
            }
        });
        exclusion.add(excludeDatePicker, exclusiongbc);
        exclusiongbc.gridx = 1;
        exclusiongbc.gridy = 1;
        exclusiongbc.weightx = 1.0;
        exclusiongbc.weighty = 0.5;
        exclusiongbc.gridwidth = 2;
        exclusion.add(excludedDatesLabel, exclusiongbc);
        exclusiongbc.gridx = 2;
        exclusiongbc.gridy = 1;
        exclusiongbc.weightx = 1.0;
        exclusiongbc.weighty = 0.5;
        JButton reset = new JButton(Labels.getString("Common.Reset"));
        reset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                excludedDates.clear();
                excludedDatesLabel.setText("");
            }
        });
        exclusion.add(reset, exclusiongbc);
        datesInputFormPanel.add(exclusion, cChart);
    }

    private void addIterationsFields(GridBagConstraints cChart) {
        cChart.gridx = 0;
        cChart.gridy = 0;
        cChart.weightx = 1.0;
        cChart.weighty = 0.5;
        // Iterations
        JPanel iterations = new JPanel();
        iterations.setLayout(new GridBagLayout());
        GridBagConstraints iterationsgbc = new GridBagConstraints();
        FormLabel iterationslabel = new FormLabel(Labels.getString("BurndownChartPanel.Iterations") + "*: ");
        iterationslabel.setMinimumSize(LABEL_DIMENSION);
        iterationslabel.setPreferredSize(LABEL_DIMENSION);
        iterationsgbc.gridx = 0;
        iterationsgbc.gridy = 0;
        iterationsgbc.weightx = 1.0;
        iterationsgbc.weighty = 0.5;
        iterations.add(iterationslabel, iterationsgbc);
        iterationsgbc.gridx = 1;
        iterationsgbc.gridy = 0;
        iterationsgbc.weightx = 1.0;
        iterationsgbc.weighty = 0.5;
        startIteration.setBackground(ColorUtil.WHITE);
        for (int i = 0; i <= 100; i++) {
            startIteration.addItem(new Integer(i));
        }
        startIteration.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if ((Integer) startIteration.getSelectedItem() > (Integer) endIteration.getSelectedItem()) {
                    endIteration.setSelectedItem(startIteration.getSelectedItem());
                }
            }
        });
        iterations.add(startIteration, iterationsgbc);
        iterationsgbc.gridx = 2;
        iterationsgbc.gridy = 0;
        iterationsgbc.weightx = 1.0;
        iterationsgbc.weighty = 0.5;
        endIteration.setBackground(ColorUtil.WHITE);
        for (int i = 0; i <= 100; i++) {
            endIteration.addItem(new Integer(i));
        }
        endIteration.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if ((Integer) endIteration.getSelectedItem() < (Integer) startIteration.getSelectedItem()) {
                    startIteration.setSelectedItem(endIteration.getSelectedItem());
                }
            }
        });
        iterations.add(endIteration, iterationsgbc);
        iterationsInputFormPanel.add(iterations, cChart);
    }

    private void addBurndownChartFields(GridBagConstraints cChart) {
        // Primary Y axis
        // Name
        cChart.gridx = 0;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel primaryYAxisLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Hours") + ": ");
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
        burndownChartInputFormPanel.add(new FormLabel(""), cChart);
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
        burndownChartInputFormPanel.add(new FormLabel(""), cChart);
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

    private void addBurndupChartFields(GridBagConstraints cChart) {
        // Secondary Y axis
        // Name
        cChart.gridx = 0;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel secondaryYAxisLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Tasks") + ": ");
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
        burnupChartInputFormPanel.add(new FormLabel(""), cChart);
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
        burnupChartInputFormPanel.add(new FormLabel(""), cChart);
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

    private void addTargetFields(GridBagConstraints cChart) {
        // Target
        // Legend
        cChart.gridx = 0;
        cChart.gridy = 0;
        //cChart.weighty = 0.5;
        FormLabel targetLabel = new FormLabel(
                Labels.getString("BurndownChartPanel.Target") + ": ");
        targetLabel.setMinimumSize(LABEL_DIMENSION);
        targetLabel.setPreferredSize(LABEL_DIMENSION);
        targetInputFormPanel.add(targetLabel, cChart);
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
        targetInputFormPanel.add(new FormLabel(""), cChart);
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

    // Getters
    public Date getStartDate() {
        return startDatePicker.getDate();
    }

    public Date getEndDate() {
        return endDatePicker.getDate();
    }

    public JCheckBox getExcludeSaturdays() {
        return excludeSaturdays;
    }

    public JCheckBox getExcludeSundays() {
        return excludeSundays;
    }

    public JCheckBox getExcludeToDos() {
        return excludeToDos;
    }

    public ArrayList<Date> getExcludedDates() {
        return excludedDates;
    }

    public JCheckBox getDatesCheckBox() {
        return datesCheckBox;
    }

    public JCheckBox getIterationsCheckBox() {
        return iterationsCheckBox;
    }

    public int getStartIteration() {
        return (Integer) startIteration.getSelectedItem();
    }

    public int getEndIteration() {
        return (Integer) endIteration.getSelectedItem();
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

    /*private void addImageFields(GridBagConstraints cImage) {
     // Image name
     cImage.gridx = 0;
     cImage.gridy = 0;
     //cImage.weighty = 0.5;
     FormLabel imageNamelabel = new FormLabel(
     Labels.getString("BurndownChartPanel.Name") + ": ");
     imageNamelabel.setMinimumSize(LABEL_DIMENSION);
     imageNamelabel.setPreferredSize(LABEL_DIMENSION);
     imageInputFormPanel.add(imageNamelabel, cImage);
     cImage.gridx = 1;
     cImage.gridy = 0;
     //cImage.weighty = 0.5;
     imageName = new JTextField();
     imageName.setText(defaultImageName);
     imageName.setMinimumSize(COMBO_BOX_DIMENSION);
     imageName.setPreferredSize(COMBO_BOX_DIMENSION);
     imageInputFormPanel.add(imageName, cImage);
     // Image formats
     cImage.gridx = 0;
     cImage.gridy = 1;
     //cImage.weighty = 0.5;
     FormLabel imageFormatLabel = new FormLabel(
     Labels.getString("BurndownChartPanel.Format") + ": ");
     imageFormatLabel.setMinimumSize(LABEL_DIMENSION);
     imageFormatLabel.setPreferredSize(LABEL_DIMENSION);
     imageInputFormPanel.add(imageFormatLabel, cImage);
     cImage.gridx = 1;
     cImage.gridy = 1;
     //cImage.weighty = 0.5;
     Object imageFormats[] = new Object[]{PNGFormat, JPGFormat};
     imageFormatComboBox = new JComboBox(imageFormats);
     imageFormatComboBox.setMinimumSize(COMBO_BOX_DIMENSION);
     imageFormatComboBox.setPreferredSize(COMBO_BOX_DIMENSION);
     imageFormatComboBox.setBackground(ColorUtil.WHITE);
     imageInputFormPanel.add(imageFormatComboBox, cImage);
     // Image size
     cImage.gridx = 0;
     cImage.gridy = 2;
     //cImage.weighty = 0.5;
     FormLabel imageSizelabel = new FormLabel(
     Labels.getString("BurndownChartPanel.Size") + ": ");
     imageSizelabel.setMinimumSize(LABEL_DIMENSION);
     imageSizelabel.setPreferredSize(LABEL_DIMENSION);
     imageInputFormPanel.add(imageSizelabel, cImage);
     cImage.gridx = 1;
     cImage.gridy = 2;
     //cImage.weighty = 0.5;
     cImage.anchor = GridBagConstraints.WEST;
     imageSizePanel.setLayout(new GridBagLayout());
     GridBagConstraints cImageSize = new GridBagConstraints();
     cImageSize.gridx = 0;
     cImageSize.gridy = 0;
     imageWidth.setText("" + defaultImageWidth);
     imageWidth.setFont(new Font(imageHeight.getFont().getName(), Font.BOLD,
     imageHeight.getFont().getSize()));
     imageWidth.setMinimumSize(IMAGE_SIZE_DIMENSION);
     imageWidth.setPreferredSize(IMAGE_SIZE_DIMENSION);
     imageSizePanel.add(imageWidth, cImageSize);
     cImageSize.gridx = 1;
     cImageSize.gridy = 0;
     imageSizePanel.add(new JLabel(" X "), cImageSize);
     cImageSize.gridx = 2;
     cImageSize.gridy = 0;
     imageHeight.setText("" + defaultImageHeight);
     imageHeight.setFont(new Font(imageHeight.getFont().getName(), Font.BOLD,
     imageHeight.getFont().getSize()));
     imageHeight.setMinimumSize(IMAGE_SIZE_DIMENSION);
     imageHeight.setPreferredSize(IMAGE_SIZE_DIMENSION);
     imageSizePanel.add(imageHeight, cImageSize);
     imageInputFormPanel.add(imageSizePanel, cImage);
     }
    
     private class ImageFormat {

     public static final String PNGExtention = "png";
     public static final String JPGExtention = "jpg";
     private final String formatName;
     private final String extention;

     public ImageFormat(String formatName, String extention) {
     this.formatName = formatName;
     this.extention = extention;
     }

     public String getExtention() {
     return extention;
     }

     public boolean isPNGFormat() {
     return extention.equals(PNGExtention);
     }

     public boolean isJPGFormat() {
     return extention.equals(JPGExtention);
     }

     @Override
     public String toString() {
     return formatName;
     }
     }

     public String getImageExtention() {
     return ((ImageFormat) imageFormatComboBox.getSelectedItem()).getExtention();
     }

     public boolean isFilePNGFormat() {
     return ((ImageFormat) imageFormatComboBox.getSelectedItem()).isPNGFormat();
     }

     public boolean isFileJPGFormat() {
     return ((ImageFormat) imageFormatComboBox.getSelectedItem()).isJPGFormat();
     }

     public String getImageName() {
     return imageName.getText().trim();
     }

     public void initImageName() {
     imageName.setText(defaultImageName);
     }

     public JCheckBox getImageCheckBox() {
     return imageCheckBox;
     }*/
}
