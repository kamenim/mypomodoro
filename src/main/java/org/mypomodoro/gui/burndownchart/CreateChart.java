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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import static org.mypomodoro.gui.burndownchart.ChartTabbedPanel.CHOOSEINPUTFORM;
import static org.mypomodoro.gui.burndownchart.ChartTabbedPanel.CONFIGUREINPUTFORM;
import org.mypomodoro.gui.burndownchart.types.IChartType;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DateUtil;

/**
 * Creates Burndown, burnup, target and scope line charts
 *
 */
// TODO error scope lines with iterations : java.lang.IndexOutOfBoundsException: Index: 0, Size: 0
// No SP and IT columns for subtasks
// rendering of SP and IT are not good
public class CreateChart extends JPanel {

    private JFreeChart charts;
    private ArrayList<Date> XAxisDateValues = new ArrayList<Date>();
    private float totalForBurndown = 0; // this may include ToDo tasks (providing ToDos are included - see CONFIGUREINPUTFORM)
    private float totalForBurndownInPercentage = 0;
    private float totalForBurnup = 0;
    //private float initialTotalForBurnup = 0;
    private float totalForBurnupInPercentage = 0;
    private ChartPanel chartPanel;
    private float maxSumForScopeLine = 0;
    private IChartType burndownchartType;
    private IChartType burnupchartType;
    private boolean burndownChartPercentage = false;
    private boolean burnupChartPercentage = false;
    private ArrayList<Float> sumForScope;

    public CreateChart() {
    }

    /**
     * Creates charts
     *
     */
    public void create() {
        removeAll();
        // Get dates
        if (CONFIGUREINPUTFORM.getDatesCheckBox().isSelected()) {
            XAxisDateValues = getXAxisDateValues(CONFIGUREINPUTFORM.getStartDate(), CONFIGUREINPUTFORM.getEndDate());
        }
        // Get type
        burndownchartType = CHOOSEINPUTFORM.getBurndownChartType();
        burnupchartType = CHOOSEINPUTFORM.getBurnupChartType();
        totalForBurndown = burndownchartType.getTotalForBurndown();
        totalForBurnup = burnupchartType.getTotalForBurnup();
        // Burndown chart in percentage
        burndownChartPercentage = CHOOSEINPUTFORM.getBurndownChartCheckBox().isSelected() && CHOOSEINPUTFORM.getBurndownChartPercentageCheckBox().isSelected();
        if (burndownChartPercentage && totalForBurndown > 0) {
            totalForBurndownInPercentage = totalForBurndown;
            totalForBurndown = 100;
        }
        // Burn-up chart in percentage
        burnupChartPercentage = CHOOSEINPUTFORM.getBurnupChartCheckBox().isSelected() && CHOOSEINPUTFORM.getBurnupChartPercentageCheckBox().isSelected();
        if (burnupChartPercentage && totalForBurnup > 0) {
            totalForBurnupInPercentage = totalForBurnup;
            totalForBurnup = 100;
        }
        // Sum for percentages and scope
        if ((CHOOSEINPUTFORM.getBurnupChartCheckBox().isSelected() && CHOOSEINPUTFORM.getScopeCheckBox().isSelected())
                || burnupChartPercentage) {
            if (CONFIGUREINPUTFORM.getDatesCheckBox().isSelected()) {
                sumForScope = burnupchartType.getSumDateRangeForScope(XAxisDateValues, CHOOSEINPUTFORM.getDataSubtasksCheckBox().isSelected());
            } else if (CONFIGUREINPUTFORM.getIterationsCheckBox().isSelected()) {
                sumForScope = burnupchartType.getSumIterationRangeForScope(CONFIGUREINPUTFORM.getStartIteration(), CONFIGUREINPUTFORM.getEndIteration());
            }
        }
        /*if (burnupChartPercentage && totalForBurnup > 0) {
         totalForBurnupInPercentage = sumForScope.get(sumForScope.size() - 1);
         if (totalForBurnupInPercentage > 0) {
         //initialTotalForBurnup = totalForBurnup;
         totalForBurnup = 100;
         } else {
         burnupChartPercentage = false; // we can't show the chart in percentage
         }
         }*/
        maxSumForScopeLine = 0;
        charts = createChart();
        chartPanel = new ChartPanel(charts);
        if (CONFIGUREINPUTFORM.getChartWidth() != 0 && CONFIGUREINPUTFORM.getChartHeight() != 0) {
            chartPanel.setPreferredSize(new Dimension(CONFIGUREINPUTFORM.getChartWidth(), CONFIGUREINPUTFORM.getChartHeight()));
        }
        chartPanel.addChartMouseListener(new CustomLayeredBarChartMouseListener(charts));
        add(chartPanel);
    }

    /**
     * Retrieves list of X-Axis date values
     *
     * @return list of dates
     */
    private ArrayList<Date> getXAxisDateValues(Date dateStart, Date dateEnd) {
        ArrayList<Date> dates = DateUtil.getDatesWithExclusions(dateStart, dateEnd,
                CONFIGUREINPUTFORM.getExcludeSaturdays().isSelected(),
                CONFIGUREINPUTFORM.getExcludeSundays().isSelected(),
                CONFIGUREINPUTFORM.getExcludedDates());
        return dates;
    }

    // When the period exceeds 2 working weeks (10 days): first date + Mondays are displayed
    // When the period exceeds 5 working months (100 days): first date + first day of the month
    private Comparable getXAxisDateValue(int XAxisIndex) {
        Date date = XAxisDateValues.get(XAxisIndex);
        boolean displayDate = true;
        if (XAxisIndex != 0
                && XAxisIndex + 1 != XAxisDateValues.size()) { // first date always displayed            
            if (XAxisDateValues.size() > 100
                    && !DateUtil.isFirstDayOfMonth(date)) { // first condition
                displayDate = false;
            } else if (XAxisDateValues.size() <= 100
                    && XAxisDateValues.size() > 10
                    && !DateUtil.isMonday(date)) { // second condition (cannot be mixed up with the first condition)
                displayDate = false;
            }
        }
        return new ComparableCustomDateForXAxis(date, displayDate);
        //return new ComparableCustomDateForXAxis(date, XAxisDateValues.displayDate(XAxisIndex));
    }

    /**
     * Creates burndown/ burn-up target line chart
     *
     * @return dataset
     */
    private CategoryDataset createBurndownTargetDataset() {
        String label = CHOOSEINPUTFORM.getTargetLegend();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (CONFIGUREINPUTFORM.getDatesCheckBox().isSelected()) {
            for (int i = 0; i < XAxisDateValues.size(); i++) {
                // use double to make the values more accurate
                // OK
                dataset.addValue((Number) new Double(totalForBurndown - i * (totalForBurndown / (XAxisDateValues.size() - 1))), label, getXAxisDateValue(i));
            }
        } else if (CONFIGUREINPUTFORM.getIterationsCheckBox().isSelected()) {
            int size = CONFIGUREINPUTFORM.getEndIteration() - CONFIGUREINPUTFORM.getStartIteration() + 1;
            for (int i = 0; i < size; i++) {
                // use double to make the values more accurate  
                // TODO
                dataset.addValue((Number) new Double(totalForBurndown - i * (totalForBurndown / (size - 1))), label, i + CONFIGUREINPUTFORM.getStartIteration());
            }
        }
        return dataset;
    }

    private CategoryDataset createBurnupGuideDataset() {
        String label = CHOOSEINPUTFORM.getBurnupGuideLegend();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (CONFIGUREINPUTFORM.getDatesCheckBox().isSelected()) {
            for (int i = 0; i < XAxisDateValues.size(); i++) {
                // use double to make the values more accurate                
                // OK
                dataset.addValue((Number) new Double(i * (totalForBurnup / (XAxisDateValues.size() - 1))), label, getXAxisDateValue(i));
            }
        } else if (CONFIGUREINPUTFORM.getIterationsCheckBox().isSelected()) {
            int size = CONFIGUREINPUTFORM.getEndIteration() + 1;
            if (size > 0) {
                for (int i = CONFIGUREINPUTFORM.getStartIteration(); i <= CONFIGUREINPUTFORM.getEndIteration(); i++) {
                    // use double to make the values more accurate                        
                    // TODO
                    dataset.addValue((Number) new Double(i * (totalForBurnup / (size - 1))), label, i);
                }
            }
        }
        return dataset;
    }

    /**
     * Creates burn-up Scope line chart
     *
     * @return dataset
     */
    private CategoryDataset createBurnupScopeDataset() {
        String label = CHOOSEINPUTFORM.getScopeLegend();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (CONFIGUREINPUTFORM.getDatesCheckBox().isSelected()) {
            ArrayList<Float> sum = getSumDateRangeForScope();
            for (int i = 0; i < XAxisDateValues.size(); i++) {
                dataset.addValue((Number) sum.get(i), label, getXAxisDateValue(i));
            }
            if (sum.size() > 0) {
                maxSumForScopeLine = sum.get(sum.size() - 1);
            }
        } else if (CONFIGUREINPUTFORM.getIterationsCheckBox().isSelected()) {
            ArrayList<Float> sum = getSumIterationRangeForScope();
            for (int i = 0; i < sum.size(); i++) {
                dataset.addValue((Number) sum.get(i), label, i + CONFIGUREINPUTFORM.getStartIteration());
            }
            if (sum.size() > 0) {
                maxSumForScopeLine = sum.get(sum.size() - 1);
            }
        }
        return dataset;
    }

    /**
     * Creates burndown chart
     *
     * @return dataset
     */
    private CategoryDataset createBurndownChartDataset() {
        String label = CHOOSEINPUTFORM.getPrimaryYAxisLegend();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        float total = totalForBurndown;
        if (CONFIGUREINPUTFORM.getDatesCheckBox().isSelected()) {
            for (int i = 0; i < XAxisDateValues.size(); i++) {
                Date date = XAxisDateValues.get(i);
                if (!DateUtil.inFuture(date)) {
                    for (Activity activity : ChartList.getList()) {
                        total -= getBurndownValue(activity, date);
                    }
                    total = burndownChartPercentage ? Math.round(total) : total;  // ok to use Math.round here (eg: 1.0 --> 1 but 1.6 --> 2)
                    dataset.addValue((Number) total, label, getXAxisDateValue(i));
                } else {
                    dataset.addValue((Number) 0, label, getXAxisDateValue(i));
                }
            }
        } else if (CONFIGUREINPUTFORM.getIterationsCheckBox().isSelected()) {
            for (int i = CONFIGUREINPUTFORM.getStartIteration(); i <= CONFIGUREINPUTFORM.getEndIteration(); i++) {
                for (Activity activity : ChartList.getList()) {
                    if (activity.getIteration() == i) {
                        total -= getBurndownValue(activity);
                    }
                }
                total = burndownChartPercentage ? Math.round(total) : total;  // ok to use Math.round here (eg: 1.0 --> 1 but 1.6 --> 2)
                dataset.addValue((Number) total, label, i);
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
        String label = CHOOSEINPUTFORM.getSecondaryYAxisLegend();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        float total = 0;
        if (CONFIGUREINPUTFORM.getDatesCheckBox().isSelected()) {
            for (int i = 0; i < XAxisDateValues.size(); i++) {
                Date date = XAxisDateValues.get(i);
                if (!DateUtil.inFuture(date)) {
                    for (Activity activity : ChartList.getList()) {
                        total += getBurnupValue(activity, date);
                    }
                    total = burnupChartPercentage ? Math.round(total) : total; // ok to use Math.round here (eg: 1.0 --> 1 but 1.6 --> 2)
                    dataset.addValue((Number) total, label, getXAxisDateValue(i));
                } else {
                    dataset.addValue((Number) 0, label, getXAxisDateValue(i));
                }
            }
        } else if (CONFIGUREINPUTFORM.getIterationsCheckBox().isSelected()) {
            for (int i = CONFIGUREINPUTFORM.getStartIteration(); i <= CONFIGUREINPUTFORM.getEndIteration(); i++) {
                for (Activity activity : ChartList.getList()) {
                    if (activity.getIteration() == i) {
                        total += getBurnupValue(activity);
                    }
                }
                total = burnupChartPercentage ? Math.round(total) : total; // ok to use Math.round here (eg: 1.0 --> 1 but 1.6 --> 2)
                dataset.addValue((Number) total, label, i);
            }
        }
        return dataset;
    }

    /**
     * Creates charts
     *
     * Rendering index order (value to be set on methods : setDataset,
     * mapDatasetToRangeAxis and setRenderer for each renderer) 0 : target
     * burndown (see ChartFactory.createLineChart) 1 : scope burnup (on top of
     * target burnup) 2 : target burnup (on top of burnup) 3 : burnup (on top of
     * burndown) 4 : burndown
     *
     * @param dataset the primary target dataset
     */
    private JFreeChart createChart() {
        CategoryDataset burndownTargetDataset = null;
        if (CHOOSEINPUTFORM.getBurndownChartCheckBox().isSelected()
                && CHOOSEINPUTFORM.getTargetCheckBox().isSelected()
                && totalForBurndown > 0) {
            burndownTargetDataset = createBurndownTargetDataset();
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "",
                "",
                "",
                burndownTargetDataset, // burndown target dataset (Rendering index order = 0)
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );

        // Color
        //chart.setBackgroundPaint(ColorUtil.WHITE);
        // Legend
        LegendTitle legend = chart.getLegend();
        legend.setItemFont(getFont().deriveFont(Font.BOLD));

        // Customise the plot
        CategoryPlot plot = (CategoryPlot) chart.getCategoryPlot();
        plot.setBackgroundPaint(ColorUtil.WHITE); // This stays White despite the background or the current theme

        //////////////////// X-AXIS //////////////////////////
        CategoryAxis categoryAxis = (CategoryAxis) plot.getDomainAxis();
        categoryAxis.setAxisLineVisible(false);
        categoryAxis.setTickLabelFont(getFont().deriveFont(Font.BOLD, getFont().getSize() - 1)); // x-axis font
        if (CONFIGUREINPUTFORM.getDatesCheckBox().isSelected()) {
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // display diagonally
        }

        //////////////////// Y-AXIS //////////////////////////
        // BURNDOWN
        NumberAxis burndownRangeAxis = (NumberAxis) plot.getRangeAxis();
        if (CHOOSEINPUTFORM.getBurndownChartCheckBox().isSelected() && totalForBurndown > 0) { // BURNDOWN            
            // Horizontal/Grid lines for burndown
            plot.setRangeGridlinesVisible(true);
            plot.setRangeGridlineStroke(new BasicStroke((float) 1.5)); // plain stroke
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            // Customise the primary Y/Range axis
            burndownRangeAxis.setLabel(CHOOSEINPUTFORM.getPrimaryYAxisName());
            burndownRangeAxis.setLabelFont(getFont().deriveFont(Font.BOLD));
            burndownRangeAxis.setAutoRangeIncludesZero(true);
            burndownRangeAxis.setAxisLineVisible(false);
            // Margin for target not total points
            float burndownRange = totalForBurndown;
            burndownRangeAxis.setRange(0, burndownRange + burndownRange / 100); // add 1% margin on top
            TickUnits burndownCustomUnits = new TickUnits();
            // Tick units : from 1 to 10 = 1; from 10 to 50 --> 5; from 50 to 100 --> 10; from 100 to 500 --> 50; from 500 to 1000 --> 100 
            int tick = 10;
            int unit = 1;
            while (burndownRange > tick) {
                if (burndownRange > tick * 5) {
                    unit = unit * 10;
                } else {
                    unit = unit * 5;
                }
                tick = tick * 10;
            }
            burndownCustomUnits.add(new NumberTickUnit(unit));
            burndownRangeAxis.setStandardTickUnits(burndownCustomUnits);
            burndownRangeAxis.setTickLabelFont(getFont().deriveFont(Font.BOLD, getFont().getSize() - 3)); // left-y-axis font
            // Add the custom bar layered renderer to plot
            CategoryDataset burndownDataset = createBurndownChartDataset();
            plot.setDataset(4, burndownDataset);
            plot.mapDatasetToRangeAxis(4, 0);
            CustomLayeredBarRenderer burndownRenderer = new CustomLayeredBarRenderer();
            burndownRenderer.setSeriesPaint(0, CHOOSEINPUTFORM.getPrimaryYAxisColor());
            burndownRenderer.setSeriesBarWidth(0, 1.0);
            burndownRenderer.setDrawBarOutline(true); // this is needed to highlight the bar (see CustomLayerBarRenderer)
            burndownRenderer.setSeriesOutlinePaint(0, Color.LIGHT_GRAY); // as the out line cannot be made thicker, it is gray by default and black when highlighted (see CustomLayerBarRenderer)
            plot.setRenderer(4, burndownRenderer); // 4 = renderer index in the plot            
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
            burndownRenderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2}" + (burndownChartPercentage ? "%" : ""), NumberFormat.getInstance()));
            burndownRenderer.setBaseSeriesVisibleInLegend(!CHOOSEINPUTFORM.getPrimaryYAxisLegend().isEmpty());
            // Target
            if (CHOOSEINPUTFORM.getTargetCheckBox().isSelected()) {
                LineAndShapeRenderer burndownTargetrenderer = new LineAndShapeRenderer(); // do not use (LineAndShapeRenderer) plot.getRenderer(); it may mess up with the burnupGuideRenderer
                burndownTargetrenderer.setShapesVisible(true);
                burndownTargetrenderer.setDrawOutlines(false);
                burndownTargetrenderer.setSeriesStroke(
                        0, new BasicStroke(
                                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                1.0f, new float[]{10.0f, 6.0f}, 0.0f));
                burndownTargetrenderer.setSeriesPaint(0, CHOOSEINPUTFORM.getTargetColor());
                burndownTargetrenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("<html><b>{2}" + (burndownChartPercentage ? "%" : "") + "</b></html>", NumberFormat.getInstance()));
                burndownTargetrenderer.setBaseSeriesVisibleInLegend(CHOOSEINPUTFORM.getTargetCheckBox().isSelected() && !CHOOSEINPUTFORM.getTargetLegend().isEmpty());
                // Disable tooltip
                //burndownTargetrenderer.setBaseToolTipGenerator(null); // disable tooltip
                plot.setRenderer(0, burndownTargetrenderer);
            }
        } else {
            burndownRangeAxis.setVisible(false);
        }

        // BURN-UP
        if (CHOOSEINPUTFORM.getBurnupChartCheckBox().isSelected() && totalForBurnup > 0) {
            NumberAxis burnupRangeAxis = new NumberAxis();
            burnupRangeAxis.setLabel(CHOOSEINPUTFORM.getSecondaryYAxisName());
            burnupRangeAxis.setLabelFont(getFont().deriveFont(Font.BOLD));
            burnupRangeAxis.setAutoRangeIncludesZero(true);
            burnupRangeAxis.setAxisLineVisible(false);
            float burnupRange = totalForBurnup;
            // If scope is displayed the highest range between burnup and scope wins
            CategoryDataset scopeDataset = null;
            float scopeRange = 0;
            if (CHOOSEINPUTFORM.getScopeCheckBox().isSelected()) {
                scopeDataset = createBurnupScopeDataset();
                burnupRange = CHOOSEINPUTFORM.getScopeCheckBox().isSelected() && maxSumForScopeLine > totalForBurnup ? maxSumForScopeLine : totalForBurnup;
                if (CHOOSEINPUTFORM.getScopeCheckBox().isSelected()) {
                    burnupRange += burnupRange / 10; // add 10% to see the values on the scope line
                }
                scopeRange = burnupRange;
            } else {
                burnupRange += burnupRange / 100; // add 1% margin on top
            }
            burnupRangeAxis.setRange(0, burnupRange);
            TickUnits burnupCustomUnits = new TickUnits();
            // Tick units : from 1 to 10 = 1; from 10 to 50 --> 5; from 50 to 100 --> 10; from 100 to 500 --> 50; from 500 to 1000 --> 100 
            int tick = 10;
            int unit = 1;
            while (burnupRange > tick) {
                if (burnupRange > tick * 5) {
                    unit = unit * 10;
                } else {
                    unit = unit * 5;
                }
                tick = tick * 10;
            }
            burnupCustomUnits.add(new NumberTickUnit(unit));
            burnupRangeAxis.setStandardTickUnits(burnupCustomUnits);
            burnupRangeAxis.setTickLabelFont(getFont().deriveFont(Font.BOLD, getFont().getSize() - 3)); // right-y-axis font
            // when burndown, add the secondary Y axis to plot for burnup
            CategoryDataset burnupDataset = createBurnupChartDataset();
            if (CHOOSEINPUTFORM.getBurndownChartCheckBox().isSelected()) {
                plot.setRangeAxis(1, burnupRangeAxis);
                plot.setDataset(3, burnupDataset);
                plot.mapDatasetToRangeAxis(3, 1);
            } else { // no burndown, primary Y axis is used for burnup
                plot.setRangeAxis(0, burnupRangeAxis);
                plot.setDataset(3, burnupDataset);
                plot.mapDatasetToRangeAxis(3, 0);
                // Horizontal/Grid lines for burndup            
                plot.setRangeGridlinesVisible(true);
                plot.setRangeGridlineStroke(new BasicStroke((float) 1.5)); // plain stroke
                plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            }
            // Add the custom bar layered renderer to plot
            CustomLayeredBarRenderer burnupRenderer = new CustomLayeredBarRenderer();
            burnupRenderer.setSeriesPaint(0, CHOOSEINPUTFORM.getSecondaryYAxisColor());
            burnupRenderer.setSeriesBarWidth(0, 0.7);
            burnupRenderer.setDrawBarOutline(true); // this is needed to highlight the bar (see CustomLayerBarRenderer)
            burnupRenderer.setSeriesOutlinePaint(0, Color.LIGHT_GRAY); // as the out line cannot be made thicker, it is gray by default and black when highlighted (see CustomLayerBarRenderer)
            plot.setRenderer(3, burnupRenderer);
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
            burnupRenderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2}" + (burnupChartPercentage ? "%" : ""), NumberFormat.getInstance()));
            burnupRenderer.setBaseSeriesVisibleInLegend(!CHOOSEINPUTFORM.getSecondaryYAxisLegend().isEmpty());
            // GUIDE
            if (CHOOSEINPUTFORM.getBurnupGuideCheckBox().isSelected()) {
                CategoryDataset burnupGuideDataset = createBurnupGuideDataset();
                plot.setDataset(2, burnupGuideDataset);
                if (CHOOSEINPUTFORM.getBurndownChartCheckBox().isSelected()) { // when burndown, range axis for the target on the right
                    plot.mapDatasetToRangeAxis(2, 1);
                } else { // no burndown, range axis for the target on the left
                    plot.mapDatasetToRangeAxis(2, 0);
                }
                LineAndShapeRenderer burnupGuideRenderer = new LineAndShapeRenderer(); // do not use (LineAndShapeRenderer) plot.getRenderer(); it may mess up with the burndownGuideRenderer
                burnupGuideRenderer.setShapesVisible(true);
                burnupGuideRenderer.setDrawOutlines(false);
                burnupGuideRenderer.setSeriesStroke(
                        0, new BasicStroke(
                                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                1.0f, new float[]{10.0f, 6.0f}, 0.0f));
                burnupGuideRenderer.setSeriesPaint(0, CHOOSEINPUTFORM.getBurnupGuideColor());
                burnupGuideRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("<html><b>{2}" + (burnupChartPercentage ? "%" : "") + "</b></html>", NumberFormat.getInstance()));
                burnupGuideRenderer.setBaseSeriesVisibleInLegend(CHOOSEINPUTFORM.getBurnupGuideCheckBox().isSelected() && !CHOOSEINPUTFORM.getBurnupGuideLegend().isEmpty());
                // Disable tooltip (also deprecated setToolTipGenerator(null) removes the tool tip here; setBaseToolTipGenerator(null) doesn't)
                //burnupGuideRenderer.setToolTipGenerator(null);
                plot.setRenderer(2, burnupGuideRenderer);
            }
            // SCOPE
            if (CHOOSEINPUTFORM.getScopeCheckBox().isSelected()) {
                NumberAxis scopeRangeAxis = new NumberAxis();
                scopeRangeAxis.setAutoRangeIncludesZero(true);
                scopeRangeAxis.setAxisLineVisible(false);
                scopeRangeAxis.setRange(0, scopeRange); // add 1% margin on top
                scopeRangeAxis.setVisible(false); // hide tick values on axis                
                // Add the custom line renderer to plot
                plot.setRangeAxis(1, scopeRangeAxis);
                plot.setDataset(1, scopeDataset);
                plot.mapDatasetToRangeAxis(1, 1);
                LineAndShapeRenderer scopeRenderer = new LineAndShapeRenderer();
                scopeRenderer.setDrawOutlines(false);
                scopeRenderer.setSeriesStroke(
                        0, new BasicStroke(
                                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                1.0f, new float[]{10.0f, 6.0f}, 0.0f));
                scopeRenderer.setSeriesPaint(0, CHOOSEINPUTFORM.getScopeColor());
                // the following two lines make values being displayed onto the chart along the scope line
                scopeRenderer.setBaseItemLabelsVisible(true);
                scopeRenderer.setBaseItemLabelGenerator((CategoryItemLabelGenerator) new ScopeCategoryItemLabelGenerator(XAxisDateValues));
                // Disable tooltip (also deprecated setToolTipGenerator(null) removes the tool tip here; setBaseToolTipGenerator(null) doesn't)
                //scopeRenderer.setToolTipGenerator(null);
                scopeRenderer.setBaseToolTipGenerator(new StandardCategoryToolTipGenerator("<html><b>{2}" + (burnupChartPercentage ? "%" : "") + "</b></html>", NumberFormat.getInstance()));
                scopeRenderer.setBaseSeriesVisibleInLegend(CHOOSEINPUTFORM.getScopeCheckBox().isSelected() && !CHOOSEINPUTFORM.getScopeLegend().isEmpty());
                //scopeRenderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2}" + (burnupChartPercentage ? "%" : ""), NumberFormat.getInstance()));
                plot.setRenderer(1, scopeRenderer);
            }
        }
        return chart;
    }

    private float getBurndownValue(Activity activity) {
        return getBurndownValue(activity, null);
    }

    private float getBurndownValue(Activity activity, Date date) {
        float value = burndownchartType.getValue(activity, date);
        if (burndownChartPercentage) {
            value = (value / totalForBurndownInPercentage) * 100;
        }
        return value;
    }

    private float getBurnupValue(Activity activity) {
        return getBurnupValue(activity, null);
    }

    private float getBurnupValue(Activity activity, Date date) {
        float value = burnupchartType.getValue(activity, date);
        if (burnupChartPercentage) {
            value = (value / totalForBurnupInPercentage) * 100;
        }
        return value;
    }

    private ArrayList<Float> getSumDateRangeForScope() {
        ArrayList<Float> sum = sumForScope;
        if (burnupChartPercentage) {
            for (int i = 0; i < sum.size(); i++) {
                sum.set(i, new Float(Math.round((sum.get(i) / totalForBurnupInPercentage) * 100))); // ok to use Math.round here (eg: 1.0 --> 1 but 1.6 --> 2)
            }
        }
        return sum;
    }

    private ArrayList<Float> getSumIterationRangeForScope() {
        ArrayList<Float> sum = sumForScope;
        if (burnupChartPercentage) {
            for (int i = 0; i < sum.size(); i++) {
                sum.set(i, new Float(Math.round((sum.get(i) / totalForBurnupInPercentage) * 100))); // ok to use Math.round here (eg: 1.0 --> 1 but 1.6 --> 2)
            }
        }
        return sum;
    }
}
