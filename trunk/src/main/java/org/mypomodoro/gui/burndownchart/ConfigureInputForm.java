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
public class ConfigureInputForm extends JPanel {

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
    private final JCheckBox excludeToDos = new JCheckBox(Labels.getString((PreferencesPanel.preferences.getAgileMode() ? "Agile." : "") + "ToDoListPanel.ToDo List"), true);
    private final DatePicker excludeDatePicker = new DatePicker(Labels.getLocale());
    private final ArrayList<Date> excludedDates = new ArrayList<Date>();
    final JCheckBox datesCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Dates"), true);
    final ComponentTitledBorder borderDates = new ComponentTitledBorder(datesCheckBox, datesInputFormPanel, BorderFactory.createEtchedBorder());
    // Iterations form
    private final JPanel iterationsInputFormPanel = new JPanel();
    private final JComboBox startIteration = new JComboBox();
    private final JComboBox endIteration = new JComboBox();
    final JCheckBox iterationsCheckBox = new JCheckBox(Labels.getString("BurndownChartPanel.Iterations"), true);
    private final ComponentTitledBorder borderIterations = new ComponentTitledBorder(iterationsCheckBox, iterationsInputFormPanel, BorderFactory.createEtchedBorder());    
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
     imageWidth.setFont(new Font(Main.font.getName(), Font.BOLD,
     Main.font.getSize()));
     imageWidth.setMinimumSize(IMAGE_SIZE_DIMENSION);
     imageWidth.setPreferredSize(IMAGE_SIZE_DIMENSION);
     imageSizePanel.add(imageWidth, cImageSize);
     cImageSize.gridx = 1;
     cImageSize.gridy = 0;
     imageSizePanel.add(new JLabel(" X "), cImageSize);
     cImageSize.gridx = 2;
     cImageSize.gridy = 0;
     imageHeight.setText("" + defaultImageHeight);
     imageHeight.setFont(new Font(Main.font.getName(), Font.BOLD,
     Main.font.getSize()));
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
