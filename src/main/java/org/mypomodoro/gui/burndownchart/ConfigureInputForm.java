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

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import org.mypomodoro.Main;
import org.mypomodoro.gui.activities.AbstractComboBoxRenderer;
import org.mypomodoro.gui.create.FormLabel;
import org.mypomodoro.util.ComponentTitledBorder;
import org.mypomodoro.util.DatePicker;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * Configure chart form
 *
 */
public class ConfigureInputForm extends JPanel {

    // TODO improve layout of subpanels
    protected static final Dimension LABEL_DIMENSION = new Dimension(170, 20);
    // Tasks form
    private final JPanel tasksInputFormPanel = new JPanel();
    private final JComboBox iterationonlyComboBox = new JComboBox();
    // Dates form
    private final JPanel datesInputFormPanel = new JPanel();
    protected final DatePicker startDatePicker = new DatePicker(Labels.getLocale());
    protected final DatePicker endDatePicker = new DatePicker(Labels.getLocale());
    private final JCheckBox excludeSaturdays = new JCheckBox(Labels.getString("BurndownChartPanel.Saturdays"), true);
    private final JCheckBox excludeSundays = new JCheckBox(Labels.getString("BurndownChartPanel.Sundays"), true);
    private final JCheckBox typeReleaseAndIteration = new JCheckBox(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List") + " + " + Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "ReportListPanel.Report List"), true);
    private final JCheckBox typeReleaseOnly = new JCheckBox(Labels.getString((Main.preferences.getAgileMode() ? "Agile." : "") + "ReportListPanel.Report List"), false);
    private final JCheckBox typeIterationOnly = new JCheckBox(Labels.getString("Agile.Common.Iteration"), false);
    private final DatePicker excludeDatePicker = new DatePicker(Labels.getLocale());
    private final ArrayList<Date> excludedDates = new ArrayList<Date>();
    final JCheckBox datesCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Dates"), true);
    final ComponentTitledBorder borderDates = new ComponentTitledBorder(datesCheckBox, datesInputFormPanel, new EtchedBorder(), datesCheckBox.getFont().deriveFont(Font.BOLD));
    // Iterations form
    private final JPanel iterationsInputFormPanel = new JPanel();
    private final JComboBox startIteration = new JComboBox();
    private final JComboBox endIteration = new JComboBox();
    final JCheckBox iterationsCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Iterations"), true);
    private final ComponentTitledBorder borderIterations = new ComponentTitledBorder(iterationsCheckBox, iterationsInputFormPanel, new EtchedBorder(), iterationsCheckBox.getFont().deriveFont(Font.BOLD));
    // Dimension
    private final JPanel dimensionInputFormPanel = new JPanel();
    private final JTextField chartWidth = new JTextField("770");
    private final JTextField chartHeight = new JTextField("410");

    public ConfigureInputForm() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        addTasksInputFormPanel();
        if (!Main.preferences.getAgileMode()) {
            iterationsInputFormPanel.setVisible(false);
        }
        addImageInputFormPanel();
    }

    private void addTasksInputFormPanel() {
        JLabel titleBorderTasks = new JLabel(" " + Labels.getString("BurndownChartPanel.Tasks") + " ");
        titleBorderTasks.setOpaque(true);
        ComponentTitledBorder borderTasks = new ComponentTitledBorder(titleBorderTasks, tasksInputFormPanel, new EtchedBorder(), titleBorderTasks.getFont().deriveFont(Font.BOLD));
        GridBagConstraints cChart = new GridBagConstraints();
        cChart.weightx = 1;
        cChart.weighty = 1;
        cChart.fill = GridBagConstraints.CENTER;
        cChart.insets = new Insets(0, 5, 2, 5);
        tasksInputFormPanel.setBorder(borderTasks);
        tasksInputFormPanel.setLayout(new GridBagLayout());
        addTasksFields(cChart);
        addDatesInputFormPanel(cChart);
        addIterationsInputFormPanel(cChart);
        add(tasksInputFormPanel, cChart);
    }

    private void addTasksFields(final GridBagConstraints cChart) {
        cChart.gridx = 0;
        cChart.gridy = 0;
        JPanel tasks = new JPanel();
        tasks.setLayout(new BoxLayout(tasks, BoxLayout.Y_AXIS));
        typeReleaseAndIteration.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                typeReleaseAndIteration.setSelected(true);
                typeReleaseOnly.setSelected(false);
                if (Main.preferences.getAgileMode()) {
                    typeIterationOnly.setSelected(false);
                    iterationsInputFormPanel.setVisible(true);
                }
            }
        });
        tasks.add(typeReleaseAndIteration); // include ToDos /Iteration Backlog tasks
        // ReportList / Release Backlog only
        typeReleaseOnly.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent event) {
                typeReleaseAndIteration.setSelected(false);
                typeReleaseOnly.setSelected(true);
                datesCheckBox.setSelected(true); // force use of dates
                if (Main.preferences.getAgileMode()) {
                    typeIterationOnly.setSelected(false);
                    iterationsInputFormPanel.setVisible(false);
                    iterationsCheckBox.setSelected(false);
                }
            }
        });
        tasks.add(typeReleaseOnly); // excludes ToDos/Iteration Backlog tasks
        // Specific iteration
        if (Main.preferences.getAgileMode()) {
            JPanel iteration = new JPanel();
            iteration.setLayout(new FlowLayout());
            typeIterationOnly.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    typeReleaseAndIteration.setSelected(false);
                    typeReleaseOnly.setSelected(false);
                    datesCheckBox.setSelected(true); // force use of dates
                    typeIterationOnly.setSelected(true);
                    iterationsInputFormPanel.setVisible(false);
                    iterationsCheckBox.setSelected(false);
                }
            });
            iteration.add(typeIterationOnly); // only iteration
            for (int i = 0; i <= 100; i++) {
                iterationonlyComboBox.addItem(new Integer(i));
            }
            iterationonlyComboBox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent event) {
                    typeReleaseAndIteration.setSelected(false);
                    typeReleaseOnly.setSelected(false);
                    datesCheckBox.setSelected(true); // force use of dates
                    typeIterationOnly.setSelected(true);
                    iterationsInputFormPanel.setVisible(false);
                    iterationsCheckBox.setSelected(false);
                }
            });
            iteration.add(iterationonlyComboBox);
            tasks.add(iteration);
        }
        tasksInputFormPanel.add(tasks);
    }

    private void addDatesInputFormPanel(final GridBagConstraints cChart) {
        cChart.gridx = 0;
        cChart.gridy = 1;
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
        addDatesFields();
        tasksInputFormPanel.add(datesInputFormPanel, cChart);
    }

    private void addIterationsInputFormPanel(final GridBagConstraints cChart) {
        cChart.gridx = 0;
        cChart.gridy = 2;
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
        addIterationsFields();
        tasksInputFormPanel.add(iterationsInputFormPanel, cChart);
    }

    private void addImageInputFormPanel() {        
        JLabel titleBorderDimension = new JLabel(" " + Labels.getString("BurndownChartPanel.Image") + " ");
        titleBorderDimension.setOpaque(true);
        ComponentTitledBorder borderDimension = new ComponentTitledBorder(titleBorderDimension, dimensionInputFormPanel, new EtchedBorder(), titleBorderDimension.getFont().deriveFont(Font.BOLD));        
        dimensionInputFormPanel.setBorder(borderDimension);
        dimensionInputFormPanel.setLayout(new GridBagLayout());
        addDimensionFields();
        add(dimensionInputFormPanel);
    }

    private void addDatesFields() {
        GridBagConstraints cChart = new GridBagConstraints();
        cChart.gridx = 0;
        cChart.gridy = 0;
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
                // select iterations check box whenever the combo box is used
                datesCheckBox.setSelected(true);
                iterationsCheckBox.setSelected(false);
                borderDates.repaint();
                borderIterations.repaint();
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
                // select iterations check box whenever the combo box is used
                datesCheckBox.setSelected(true);
                iterationsCheckBox.setSelected(false);
                borderDates.repaint();
                borderIterations.repaint();
            }
        });
        dates.add(endDatePicker, datesgbc);
        datesInputFormPanel.add(dates, cChart);
        // Exclusion
        cChart.gridx = 0;
        cChart.gridy = 1;
        // first line
        JPanel exclusion = new JPanel();
        exclusion.setLayout(new GridBagLayout());
        GridBagConstraints exclusiongbc = new GridBagConstraints();
        FormLabel exclusionlabel = new FormLabel(Labels.getString("BurndownChartPanel.Exclusion") + "*: ");
        exclusionlabel.setMinimumSize(LABEL_DIMENSION);
        exclusionlabel.setPreferredSize(LABEL_DIMENSION);
        exclusiongbc.gridx = 0;
        exclusiongbc.gridy = 0;
        exclusion.add(exclusionlabel, exclusiongbc);
        exclusiongbc.gridx = 1;
        exclusiongbc.gridy = 0;
        exclusion.add(excludeSaturdays, exclusiongbc);
        exclusiongbc.gridx = 2;
        exclusiongbc.gridy = 0;
        exclusion.add(excludeSundays, exclusiongbc);
        // second line
        exclusiongbc.gridx = 1;
        exclusiongbc.gridy = 1;
        final JTextArea excludedDatesLabel = new JTextArea();
        excludedDatesLabel.setEditable(false);
        excludedDatesLabel.setVisible(false);
        excludedDatesLabel.setPreferredSize(new Dimension(270, 100));
        excludedDatesLabel.setLineWrap(true); // enable wrapping
        excludeDatePicker.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (!excludedDates.contains(excludeDatePicker.getDate())) {
                    excludedDates.add(excludeDatePicker.getDate());
                }
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
                excludedDatesLabel.setVisible(true);
            }
        });
        exclusion.add(excludeDatePicker, exclusiongbc);
        exclusiongbc.gridx = 2;
        exclusiongbc.gridy = 1;
        JButton reset = new JButton(Labels.getString("Common.Reset"));
        reset.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                excludedDates.clear();
                excludedDatesLabel.setText("");
                excludedDatesLabel.setVisible(false);
            }
        });
        exclusion.add(reset, exclusiongbc);
        exclusiongbc.gridx = 1;
        exclusiongbc.gridy = 2;
        exclusiongbc.gridwidth = 2;
        exclusion.add(excludedDatesLabel, exclusiongbc);
        datesInputFormPanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // no use
            }

            @Override
            public void mousePressed(MouseEvent e) {
                datesCheckBox.setSelected(true);
                iterationsCheckBox.setSelected(false);
                borderDates.repaint();
                borderIterations.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // no use
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // no use
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // no use
            }
        });
        datesInputFormPanel.add(exclusion, cChart);
    }

    private void addIterationsFields() {
        // Iterations
        JPanel iterations = new JPanel();
        iterations.setLayout(new GridBagLayout());
        GridBagConstraints iterationsgbc = new GridBagConstraints();
        iterationsgbc.weightx = 1;
        iterationsgbc.weighty = 1;
        iterationsgbc.fill = GridBagConstraints.BOTH;
        iterationsgbc.insets = new Insets(0, 5, 2, 5);
        FormLabel iterationslabel = new FormLabel(Labels.getString("BurndownChartPanel.Iterations") + "*: ");
        iterationslabel.setMinimumSize(LABEL_DIMENSION);
        iterationslabel.setPreferredSize(LABEL_DIMENSION);
        iterationsgbc.gridx = 0;
        iterationsgbc.gridy = 0;
        iterations.add(iterationslabel, iterationsgbc);
        iterationsgbc.gridx = 1;
        iterationsgbc.gridy = 0;
        for (int i = 0; i <= 100; i++) {
            startIteration.addItem(new Integer(i));
        }
        startIteration.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if ((Integer) startIteration.getSelectedItem() > (Integer) endIteration.getSelectedItem()) {
                    endIteration.setSelectedItem(startIteration.getSelectedItem());
                }
                // select iterations check box whenever the combo box is used
                datesCheckBox.setSelected(false);
                iterationsCheckBox.setSelected(true);
                borderDates.repaint();
                borderIterations.repaint();
            }
        });
        startIteration.setRenderer(new AbstractComboBoxRenderer());
        iterations.add(startIteration, iterationsgbc);
        iterationsgbc.gridx = 2;
        iterationsgbc.gridy = 0;
        for (int i = 0; i <= 100; i++) {
            endIteration.addItem(new Integer(i));
        }
        endIteration.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if ((Integer) endIteration.getSelectedItem() < (Integer) startIteration.getSelectedItem()) {
                    startIteration.setSelectedItem(endIteration.getSelectedItem());
                }
                // select iterations check box whenever the combo box is used
                datesCheckBox.setSelected(false);
                iterationsCheckBox.setSelected(true);
                borderDates.repaint();
                borderIterations.repaint();
            }
        });
        endIteration.setRenderer(new AbstractComboBoxRenderer());
        iterations.add(endIteration, iterationsgbc);
        // select iterations check box whenever the panel is selected
        iterationsInputFormPanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                // no use
            }

            @Override
            public void mousePressed(MouseEvent e) {
                datesCheckBox.setSelected(false);
                iterationsCheckBox.setSelected(true);
                borderDates.repaint();
                borderIterations.repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // no use
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                // no use
            }

            @Override
            public void mouseExited(MouseEvent e) {
                // no use
            }
        });
        iterationsInputFormPanel.add(iterations);
    }

    private void addDimensionFields() {
        // Iterations
        JPanel dimension = new JPanel();
        dimension.setLayout(new GridBagLayout());
        GridBagConstraints dimensionsgbc = new GridBagConstraints();
        FormLabel dimensionlabel = new FormLabel(Labels.getString("BurndownChartPanel.Dimension") + "*: ");
        dimensionlabel.setMinimumSize(LABEL_DIMENSION);
        dimensionlabel.setPreferredSize(LABEL_DIMENSION);
        dimensionsgbc.gridx = 0;
        dimensionsgbc.gridy = 0;
        dimensionsgbc.weightx = 1.0;
        dimensionsgbc.weighty = 0.5;
        dimension.add(dimensionlabel, dimensionsgbc);
        dimensionsgbc.gridx = 1;
        dimensionsgbc.gridy = 0;
        dimensionsgbc.weightx = 1.0;
        dimensionsgbc.weighty = 0.5;
        chartWidth.setPreferredSize(new Dimension(40, 25));
        chartWidth.setHorizontalAlignment(SwingConstants.RIGHT);
        ((AbstractDocument) chartWidth.getDocument()).setDocumentFilter(new IntegerDocumentFilter());
        dimension.add(chartWidth, dimensionsgbc);
        dimensionsgbc.gridx = 2;
        dimensionsgbc.gridy = 0;
        dimensionsgbc.weightx = 1.0;
        dimensionsgbc.weighty = 0.5;
        dimension.add(new JLabel(" X "), dimensionsgbc);
        dimensionsgbc.gridx = 3;
        dimensionsgbc.gridy = 0;
        dimensionsgbc.weightx = 1.0;
        dimensionsgbc.weighty = 0.5;
        chartHeight.setPreferredSize(new Dimension(40, 25));
        chartHeight.setHorizontalAlignment(SwingConstants.RIGHT);
        ((AbstractDocument) chartHeight.getDocument()).setDocumentFilter(new IntegerDocumentFilter());
        dimension.add(chartHeight, dimensionsgbc);
        dimensionInputFormPanel.add(dimension);
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

    public JCheckBox getReleaseOnly() {
        return typeReleaseOnly;
    }

    public JCheckBox getReleaseAndIteration() {
        return typeReleaseAndIteration;
    }

    public JCheckBox getIterationOnly() {
        return typeIterationOnly;
    }

    public int getIteration() {
        return (Integer) iterationonlyComboBox.getSelectedItem();
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

    public int getChartWidth() {
        return chartWidth.getText().isEmpty() ? 0 : Integer.parseInt(chartWidth.getText());
    }

    public int getChartHeight() {
        return chartHeight.getText().isEmpty() ? 0 : Integer.parseInt(chartHeight.getText());
    }
}

/**
 * Filter that makes JtextField fields allow integers only
 *
 */
class IntegerDocumentFilter extends DocumentFilter {

    @Override
    public void insertString(FilterBypass fb, int off, String str, AttributeSet attr)
            throws BadLocationException {
        // remove non-digits
        fb.insertString(off, str.replaceAll("\\D++", ""), attr);
    }

    @Override
    public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr)
            throws BadLocationException {
        // remove non-digits
        fb.replace(off, len, str.replaceAll("\\D++", ""), attr);
    }
}
