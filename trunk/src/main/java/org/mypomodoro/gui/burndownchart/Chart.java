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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LayeredBarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.mypomodoro.Main;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.DateUtil;
import org.mypomodoro.util.Labels;

/**
 * Agile-like burndown chart
 *
 * Code examples (JFreeChart) :
 * http://www.java2s.com/Code/Java/Chart/CatalogChart.htm
 *
 */
public class Chart extends JPanel {

    private static final long serialVersionUID = 1L;
    private JFreeChart burndownChart;
    private ArrayList<Integer> XAxisValues = new ArrayList<Integer>();
    private float totalStoryPoints = 0;
    private ChartPanel chartPanel;
    private final CreateInputForm createInputForm;

    public Chart(CreateInputForm createInputForm) {
        this.createInputForm = createInputForm;
    }

    public void create() {
        removeAll();
        XAxisValues = getXAxisValues(createInputForm.getStartDate(), createInputForm.getEndDate());
        totalStoryPoints = 0; //reset
        for (Activity activity : ChartList.getList()) {
            totalStoryPoints += activity.getStoryPoints();
        }
        System.err.println("totalStoryPoints = " + totalStoryPoints);
        CategoryDataset dataset = createTargetDataset();
        burndownChart = createChart(dataset);
        chartPanel = new ChartPanel(burndownChart);
        add(chartPanel);
    }

    // TODO exclude week end and days off
    private ArrayList<Integer> getXAxisValues(Date dateStart, Date dateEnd) {
        return DateUtil.getDaysOfMonth(dateStart, dateEnd);
        //return DateUtil.getDaysOfMonth(dateStart, dateEnd, true, true, new ArrayList<Date>());
        //return new String[]{"6 AUG.", "7 AUG.", "8 AUG.", "9 AUG.", "10 AUG.", "13 AUG.", "14 AUG.", "15 AUG.", "16 AUG.", "17 AUG.", "20 AUG.", "21 AUG.", "22 AUG.", "23 AUG.", "24 AUG."};
        //return new String[]{"6", "7", "8", "9", "10", "13", "14", "15", "16", "17", "20", "21", "22", "23", "24"};
    }

    private CategoryDataset createTargetDataset() {
        String label = "Target";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        /*dataset.addValue(totalStoryPoints, label, XAxisValues[0]);
         for (int i = 1; i < 14; i++) {
         dataset.addValue(Math.round(320 - i * 320 / 14), label, XAxisValues[i]);
         }
         dataset.addValue(0, label, XAxisValues[14]);*/
        dataset.addValue(totalStoryPoints, label, XAxisValues.get(0));
        for (int i = 1; i < XAxisValues.size() - 1; i++) {
            dataset.addValue(Math.round(totalStoryPoints - i * (totalStoryPoints / (XAxisValues.size() - 1))), label, XAxisValues.get(i));
        }
        dataset.addValue(0, label, XAxisValues.get(XAxisValues.size() - 1));
        return dataset;
    }

    private CategoryDataset createRemainingStoryPointsDataset() {
        String label = "StoryPoints";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        /*String[] XAxisValues = getXAxisValues();
         dataset.addValue(320, label, XAxisValues[0]);
         dataset.addValue(300, label, XAxisValues[1]);
         dataset.addValue(275, label, XAxisValues[2]);
         dataset.addValue(260, label, XAxisValues[3]);
         dataset.addValue(252, label, XAxisValues[4]);
         dataset.addValue(240, label, XAxisValues[5]);
         dataset.addValue(211, label, XAxisValues[6]);
         dataset.addValue(211, label, XAxisValues[7]);
         dataset.addValue(180, label, XAxisValues[8]);
         dataset.addValue(172, label, XAxisValues[9]);
         dataset.addValue(155, label, XAxisValues[10]);
         dataset.addValue(110, label, XAxisValues[11]);
         dataset.addValue(93, label, XAxisValues[12]);
         dataset.addValue(61, label, XAxisValues[13]);
         dataset.addValue(60, label, XAxisValues[14]);*/

        float storyPoints = totalStoryPoints;
        for (Integer day : XAxisValues) {
            dataset.addValue(storyPoints, label, day);
            for (Activity activity : ChartList.getList()) {
                if (DateUtil.convertToDay(activity.getDateCompleted()) == day) {
                    storyPoints -= activity.getStoryPoints();
                }
            }
        }
        return dataset;
    }

    private CategoryDataset createCompletedTasksDataset(Date dateStart, Date dateEnd) {

        String label = "Completed";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        /*String[] XAxisValues = getXAxisValues();
         dataset.addValue(0, label, XAxisValues[0]);
         dataset.addValue(4, label, XAxisValues[1]);
         dataset.addValue(4, label, XAxisValues[2]);
         dataset.addValue(5, label, XAxisValues[3]);
         dataset.addValue(8, label, XAxisValues[4]);
         dataset.addValue(12, label, XAxisValues[5]);
         dataset.addValue(29, label, XAxisValues[6]);
         dataset.addValue(31, label, XAxisValues[7]);
         dataset.addValue(31, label, XAxisValues[8]);
         dataset.addValue(31, label, XAxisValues[9]);
         dataset.addValue(38, label, XAxisValues[10]);
         dataset.addValue(45, label, XAxisValues[11]);
         dataset.addValue(51, label, XAxisValues[12]);
         dataset.addValue(72, label, XAxisValues[13]);
         dataset.addValue(78, label, XAxisValues[14]);*/

        return dataset;
    }

    private JFreeChart createChart(final CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createLineChart(
                "",
                "",
                "",
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );
        chart.setBackgroundPaint(Color.WHITE);
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize()));
        //chart.removeLegend();

        // Customise the plot
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlineStroke(new BasicStroke((float) 1.5)); // plain stroke
        plot.setRangeGridlinePaint(Color.LIGHT_GRAY);

        // Customise the primary Y/Range axis
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setLabel("STORY POINTS");
        rangeAxis.setLabelFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize()));
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setRange(0, totalStoryPoints + totalStoryPoints / 100); // add 1% margin on top
        TickUnits customUnits = new TickUnits();
        // Tick units : from 1 to 10 = 1; from 10 to 50 --> 5; from 50 to 100 --> 10; from 100 to 500 --> 50; from 500 to 1000 --> 100 
        int tick = 10;
        int unit = 1;
        while (totalStoryPoints > tick) {
            if (totalStoryPoints > tick * 5) {
                unit = unit * 10;
            } else {
                unit = unit * 5;
            }
            tick = tick * 10;
        }
        customUnits.add(new NumberTickUnit(unit));
        rangeAxis.setStandardTickUnits(customUnits);
        rangeAxis.setTickLabelFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize() + 1));

        // Customise the X/Category axis
        CategoryAxis categoryAxis = (CategoryAxis) plot.getDomainAxis();
        categoryAxis.setAxisLineVisible(false);
        categoryAxis.setTickLabelFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize()));

        // Customise the line renderer
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setDrawOutlines(false);
        renderer.setSeriesStroke(
                0, new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                        1.0f, new float[]{10.0f, 6.0f}, 0.0f));
        renderer.setSeriesPaint(0, Color.BLACK);

        // Add the custom bar layered renderer to plot
        CategoryDataset dataset2 = createRemainingStoryPointsDataset();
        plot.setDataset(2, dataset2);
        plot.mapDatasetToRangeAxis(2, 0);
        LayeredBarRenderer renderer2 = new LayeredBarRenderer();
        renderer2.setSeriesPaint(0, new Color(249, 192, 9));
        renderer2.setSeriesBarWidth(0, 1.0);
        plot.setRenderer(2, renderer2);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
        renderer2.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2}", NumberFormat.getInstance()));

        // Customise the secondary Y axis
        NumberAxis rangeAxis2 = new NumberAxis();
        rangeAxis2.setLabel("COMPLETED TASKS %");
        rangeAxis2.setLabelFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize()));
        rangeAxis2.setAutoRangeIncludesZero(true);
        rangeAxis2.setAxisLineVisible(false);
        rangeAxis2.setRange(0, 100);
        TickUnits customUnits2 = new TickUnits();
        customUnits2.add(new NumberTickUnit(20));
        rangeAxis2.setStandardTickUnits(customUnits2);
        rangeAxis2.setTickLabelFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize() + 1));

        // Add the secondary Y axis to plot
        plot.setRangeAxis(1, rangeAxis2);

        // Add the custom bar layered renderer to plot
        /*CategoryDataset dataset3 = createCompletedTasksDataset();
         plot.setDataset(1, dataset3);
         plot.mapDatasetToRangeAxis(1, 1);
         LayeredBarRenderer renderer3 = new LayeredBarRenderer();
         renderer3.setSeriesPaint(0, new Color(228, 92, 17));
         renderer3.setSeriesBarWidth(0, 0.7);
         plot.setRenderer(1, renderer3);
         plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
         renderer3.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2} %", NumberFormat.getInstance()));*/
        return chart;
    }

    public void saveImageChart(String fileName) {
        int imageWidth = 800;
        int imageHeight = 600;
        try {
            ChartUtilities.saveChartAsPNG(new File(fileName), burndownChart, imageWidth, imageHeight);
        } catch (IOException ex) {
            String title = Labels.getString("Common.Error");
            String message = Labels.getString("BurndownChartPanel.Image creation failed");
            JOptionPane.showConfirmDialog(Main.gui, message, title,
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
        }
    }
}
