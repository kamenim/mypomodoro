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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.DocumentFilter.FilterBypass;
import org.mypomodoro.gui.preferences.PreferencesPanel;
import org.mypomodoro.gui.activities.AbstractComboBoxRenderer;
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
public class ConfigureInputForm extends JPanel {

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
    private final JCheckBox excludeToDos = new JCheckBox(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List"), true);
    private final DatePicker excludeDatePicker = new DatePicker(Labels.getLocale());
    private final ArrayList<Date> excludedDates = new ArrayList<Date>();
    final JCheckBox datesCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Dates"), true);
    final ComponentTitledBorder borderDates = new ComponentTitledBorder(datesCheckBox, datesInputFormPanel, new EtchedBorder(), getFont().deriveFont(Font.BOLD));
    // Iterations form
    private final JPanel iterationsInputFormPanel = new JPanel();
    private final JComboBox startIteration = new JComboBox();
    private final JComboBox endIteration = new JComboBox();
    final JCheckBox iterationsCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Iterations"), true);
    private final ComponentTitledBorder borderIterations = new ComponentTitledBorder(iterationsCheckBox, iterationsInputFormPanel, new EtchedBorder(), getFont().deriveFont(Font.BOLD));
    // Dimension
    private final JPanel dimensionInputFormPanel = new JPanel();
    private final JTextField chartWidth = new JTextField("680");
    private final JTextField chartHeight = new JTextField("420");
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

    public ConfigureInputForm() {
        setLayout(new GridBagLayout());
        // The following three lines are necessary to make the additional jpanels to fill up the parent jpanel
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        addDatesInputFormPanel();
        addIterationsInputFormPanel();
        addDimensionInputFormPanel();
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
        addDatesFields();
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
        addIterationsFields();
        add(iterationsInputFormPanel, c);
    }

    private void addDimensionInputFormPanel() {
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.0;
        c.weighty = 0.5;
        //c.gridwidth = 2;
        TitledBorder borderDimension = new TitledBorder(new EtchedBorder());
        borderDimension.setTitleFont(getFont().deriveFont(Font.BOLD));
        borderDimension.setTitle(Labels.getString("BurndownChartPanel.Dimension"));
        dimensionInputFormPanel.setBorder(borderDimension);
        dimensionInputFormPanel.setLayout(new GridBagLayout());
        addDimensionFields();
        add(dimensionInputFormPanel, c);
    }

    private void addDatesFields() {
        GridBagConstraints cChart = new GridBagConstraints();
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
        exclusion.add(excludeToDos, exclusiongbc); // excludes ToDos Tasks/Iteration Backlog
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
        GridBagConstraints cChart = new GridBagConstraints();
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
        startIteration.setRenderer(new AbstractComboBoxRenderer());
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
        endIteration.setRenderer(new AbstractComboBoxRenderer());
        iterations.add(endIteration, iterationsgbc);
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
        iterationsInputFormPanel.add(iterations, cChart);
    }

    private void addDimensionFields() {
        GridBagConstraints cChart = new GridBagConstraints();
        cChart.gridx = 0;
        cChart.gridy = 0;
        cChart.weightx = 1.0;
        cChart.weighty = 0.5;
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
        chartWidth.setBackground(ColorUtil.WHITE);
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
        chartHeight.setBackground(ColorUtil.WHITE);
        chartHeight.setPreferredSize(new Dimension(40, 25));
        chartHeight.setHorizontalAlignment(SwingConstants.RIGHT);
        ((AbstractDocument) chartHeight.getDocument()).setDocumentFilter(new IntegerDocumentFilter());
        dimension.add(chartHeight, dimensionsgbc);
        dimensionInputFormPanel.add(dimension, cChart);
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
