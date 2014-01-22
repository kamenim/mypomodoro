package org.mypomodoro.gui.reports.burndownchart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import javax.swing.JFrame;
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
import org.mypomodoro.util.Labels;

/**
 * Agile-like burndown chart
 *
 * Code examples (JFreeChart) :
 * http://www.java2s.com/Code/Java/Chart/CatalogChart.htm
 *
 * @author Phil Karoo
 */
public class BurndownChart extends JPanel {

    private static final long serialVersionUID = 1L;
    private JFreeChart chart;

    public BurndownChart() {
        CategoryDataset dataset = createTargetDataset();
        chart = createChart(dataset);
        ChartPanel chartPanel = new ChartPanel(chart);
        add(chartPanel);
    }

    private CategoryDataset createTargetDataset() {

        String label = "Target";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] XAxisValues = getXAxisValues();
        dataset.addValue(320, label, XAxisValues[0]);
        for (int i = 1; i < 14; i++) {
            dataset.addValue(Math.round(320 - i * 320 / 14), label, XAxisValues[i]);
        }
        dataset.addValue(0, label, XAxisValues[14]);

        return dataset;
    }

    private CategoryDataset createRemainingHoursDataset() {

        String label = "Remaining";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] XAxisValues = getXAxisValues();
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
        dataset.addValue(60, label, XAxisValues[14]);

        return dataset;
    }

    private CategoryDataset createCompletedTasksDataset() {

        String label = "Completed";
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        String[] XAxisValues = getXAxisValues();
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
        dataset.addValue(78, label, XAxisValues[14]);

        return dataset;
    }

    private String[] getXAxisValues() {
        //return new String[]{"6 AUG.", "7 AUG.", "8 AUG.", "9 AUG.", "10 AUG.", "13 AUG.", "14 AUG.", "15 AUG.", "16 AUG.", "17 AUG.", "20 AUG.", "21 AUG.", "22 AUG.", "23 AUG.", "24 AUG."};
        return new String[]{"6", "7", "8", "9", "10", "13", "14", "15", "16", "17", "20", "21", "22", "23", "24"};
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
        rangeAxis.setLabel("REMAINING WORKING HOURS");
        rangeAxis.setLabelFont(new Font(new JLabel().getFont().getName(), Font.BOLD,
                new JLabel().getFont().getSize()));
        rangeAxis.setAutoRangeIncludesZero(true);
        rangeAxis.setAxisLineVisible(false);
        rangeAxis.setRange(0, 350);
        TickUnits customUnits = new TickUnits();
        customUnits.add(new NumberTickUnit(50));
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
        CategoryDataset dataset2 = createRemainingHoursDataset();
        plot.setDataset(2, dataset2);
        plot.mapDatasetToRangeAxis(2, 0);
        LayeredBarRenderer renderer2 = new LayeredBarRenderer();
        renderer2.setSeriesPaint(0, new Color(249, 192, 9));
        renderer2.setSeriesBarWidth(0, 1.0);
        plot.setRenderer(2, renderer2);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
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
        CategoryDataset dataset3 = createCompletedTasksDataset();
        plot.setDataset(1, dataset3);
        plot.mapDatasetToRangeAxis(1, 1);
        LayeredBarRenderer renderer3 = new LayeredBarRenderer();
        renderer3.setSeriesPaint(0, new Color(228, 92, 17));
        renderer3.setSeriesBarWidth(0, 0.7);
        plot.setRenderer(1, renderer3);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
        renderer3.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2} %", NumberFormat.getInstance()));

        return chart;
    }

    public void saveImageChart() {
        String fileName = "mypomodoro.png";
        int imageWidth = 800;
        int imageHeight = 600;
        try {
            ChartUtilities.saveChartAsPNG(new File(fileName), chart, imageWidth, imageHeight);
        } catch (IOException ex) {
            String title = Labels.getString("Common.Error");
            String message = Labels.getString("ReportListPanel.Chart.Image creation failed");
            JOptionPane.showConfirmDialog(Main.gui, message, title,
                    JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE);
        }
    }
}
