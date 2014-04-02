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
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
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
import org.joda.time.DateTime;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.DateUtil;

/**
 * Agile-like burndown chart
 *
 * Code examples (JFreeChart) :
 * http://www.java2s.com/Code/Java/Chart/CatalogChart.htm
 *
 */
public class Chart extends JPanel {

    private static final long serialVersionUID = 1L;

    private JFreeChart charts;
    private ArrayList<Date> XAxisValues = new ArrayList<Date>();
    private float totalStoryPoints = 0;
    private ChartPanel chartPanel;
    private final CreateInputForm createInputForm;

    public Chart(CreateInputForm createInputForm) {
        this.createInputForm = createInputForm;
    }

    /**
     * Creates charts (burndown, burn-up, target line)
     *
     */
    public void create() {
        removeAll();
        totalStoryPoints = 0;
        XAxisValues = getXAxisValues(createInputForm.getStartDate(), createInputForm.getEndDate());
        for (Activity activity : ChartList.getList()) {
            totalStoryPoints += activity.getStoryPoints();
        }
        CategoryDataset dataset = new DefaultCategoryDataset();
        if (createInputForm.getTargetCheckBox().isSelected()) {
            dataset = createTargetDataset();
        }
        charts = createChart(dataset);
        chartPanel = new ChartPanel(charts);
        add(chartPanel);
    }

    /**
     * Retrieves list of dates minus exclusions
     *
     * @return list of dates
     */
    private ArrayList<Date> getXAxisValues(Date dateStart, Date dateEnd) {
        return DateUtil.getDatesWithExclusions(dateStart, dateEnd,
                createInputForm.getExcludeSaturdays().isSelected(),
                createInputForm.getExcludeSundays().isSelected(),
                createInputForm.getExcludedDates());
    }

    /**
     * Creates target line
     *
     * @return dataset
     */
    private CategoryDataset createTargetDataset() {
        String label = "Target";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        float storyPoints = totalStoryPoints;
        dataset.addValue((Number) storyPoints, label, new DateTime(XAxisValues.get(0)).getDayOfMonth());
        for (int i = 1; i < XAxisValues.size() - 1; i++) {
            dataset.addValue((Number) (Math.round(storyPoints - i * (storyPoints / (XAxisValues.size() - 1)))), label, new DateTime(XAxisValues.get(i)).getDayOfMonth());
        }
        dataset.addValue((Number) 0, label, new DateTime(XAxisValues.get(XAxisValues.size() - 1)).getDayOfMonth());
        return dataset;
    }

    /**
     * Creates burndown chart
     *
     * @return dataset
     */
    private CategoryDataset createBurndownChartDataset() {
        String label = "StoryPoints";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        float storyPoints = totalStoryPoints;
        for (Date date : XAxisValues) {
            int day = new DateTime(date).dayOfMonth().get();
            if (DateUtil.inFuture(date)) {
                dataset.addValue((Number) 0, label, day);
            } else {
                dataset.addValue((Number) storyPoints, label, day);
            }
            for (Activity activity : ChartList.getList()) {
                if (activity.isCompleted()
                        && DateUtil.isEquals(activity.getDateCompleted(), date)) {
                    storyPoints -= activity.getStoryPoints();
                }
            }
        }
        return dataset;
    }

    /**
     * Creates burn-up chart
     *
     * @return dataset
     */
    private CategoryDataset createBurnupChartDataset() {
        String label = "StoryPoints";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        float storyPoints = 0;
        for (Date date : XAxisValues) {
            int day = new DateTime(date).dayOfMonth().get();
            if (DateUtil.inFuture(date)) {
                dataset.addValue((Number) 0, label, day);
            } else {
                dataset.addValue((Number) storyPoints, label, day);
            }
            for (Activity activity : ChartList.getList()) {
                if (activity.isCompleted()
                        && DateUtil.isEquals(activity.getDateCompleted(), date)) {
                    storyPoints += activity.getStoryPoints();
                }
            }
        }
        return dataset;
    }

    private JFreeChart createChart(final CategoryDataset dataset) {

        JFreeChart chart = ChartFactory.createLineChart(
                "",
                "",
                "",
                dataset, // Target data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );
        
        // Color
        chart.setBackgroundPaint(Color.WHITE);

        // Legend
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize()));
        //chart.removeLegend();

        // Customise the plot
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.WHITE);

        // Customise the X/Category axis
        CategoryAxis categoryAxis = (CategoryAxis) plot.getDomainAxis();
        categoryAxis.setAxisLineVisible(false);
        categoryAxis.setTickLabelFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize()));

        // Horizontal/Grid lines
        if (createInputForm.getBurndownChartCheckBox().isSelected()) {
            plot.setRangeGridlinesVisible(true);
            plot.setRangeGridlineStroke(new BasicStroke((float) 1.5)); // plain stroke
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
        }

        // Burndown chart
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        if (createInputForm.getBurndownChartCheckBox().isSelected()) {
            // Customise the primary Y/Range axis
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
            // Add the custom bar layered renderer to plot
            CategoryDataset dataset2 = createBurndownChartDataset();
            plot.setDataset(2, dataset2);
            plot.mapDatasetToRangeAxis(2, 0);
            LayeredBarRenderer renderer2 = new LayeredBarRenderer();
            renderer2.setSeriesPaint(0, new Color(249, 192, 9));
            renderer2.setSeriesBarWidth(0, 1.0);
            plot.setRenderer(2, renderer2);
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
            renderer2.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2}", NumberFormat.getInstance()));
        } else {
            // hide primary axis            
            rangeAxis.setVisible(false);
        }

        // Target line
        if (createInputForm.getTargetCheckBox().isSelected()) {
            // Customise the target line renderer     
            LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
            renderer.setDrawOutlines(false);
            renderer.setSeriesStroke(
                    0, new BasicStroke(
                            2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                            1.0f, new float[]{10.0f, 6.0f}, 0.0f));
            renderer.setSeriesPaint(0, Color.BLACK);
        }

        // Burn-up chart
        if (createInputForm.getBurnupChartCheckBox().isSelected()) {
            // Customise the secondary Y axis
            NumberAxis rangeAxis2 = new NumberAxis();
            rangeAxis2.setLabel("STORY POINTS");
            rangeAxis2.setLabelFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                    new JLabel().getFont().getSize()));
            rangeAxis2.setAutoRangeIncludesZero(true);
            rangeAxis2.setAxisLineVisible(false);
            rangeAxis2.setRange(0, totalStoryPoints + totalStoryPoints / 100); // add 1% margin on top
            TickUnits customUnits2 = new TickUnits();
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
            customUnits2.add(new NumberTickUnit(unit));
            rangeAxis2.setStandardTickUnits(customUnits2);
            rangeAxis2.setTickLabelFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                    new JLabel().getFont().getSize() + 1));
            // Add the secondary Y axis to plot
            plot.setRangeAxis(1, rangeAxis2);
            // Add the custom bar layered renderer to plot
            CategoryDataset dataset3 = createBurnupChartDataset();
            plot.setDataset(1, dataset3);
            plot.mapDatasetToRangeAxis(1, 1);
            LayeredBarRenderer renderer3 = new LayeredBarRenderer();
            renderer3.setSeriesPaint(0, new Color(228, 92, 17));
            renderer3.setSeriesBarWidth(0, 0.7);
            plot.setRenderer(1, renderer3);
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
            renderer3.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2}", NumberFormat.getInstance()));
        }        
        return chart;
    }

    /*public void saveImageChart(String fileName) {
     int imageWidth = 800;
     int imageHeight = 600;
     try {
     ChartUtilities.saveChartAsPNG(new File(fileName), charts, imageWidth, imageHeight);
     } catch (IOException ex) {
     String title = Labels.getString("Common.Error");
     String message = Labels.getString("BurndownChartPanel.Image creation failed");
     JOptionPane.showConfirmDialog(Main.gui, message, title,
     JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
     }
     }*/
}
