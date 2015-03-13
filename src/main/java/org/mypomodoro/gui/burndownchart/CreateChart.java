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
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LayeredBarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.mypomodoro.gui.burndownchart.types.IChartType;
import org.mypomodoro.model.Activity;
import org.mypomodoro.model.ChartList;
import org.mypomodoro.util.ColorUtil;
import org.mypomodoro.util.DateUtil;

/**
 * Creates Burndown, burnup, target and scope line charts
 *
 */
public class CreateChart extends JPanel {

    private JFreeChart charts;
    private ArrayList<Date> XAxisDateValues = new ArrayList<Date>();
    private float totalForBurndown = 0; // this may include ToDo tasks (providing ToDos are included - see configureInputForm)
    private float totalForBurndownInPercentage = 0;
    private float totalForBurnup = 0;
    private float initialTotalForBurnup = 0;
    private float totalForBurnupInPercentage = 0;
    private ChartPanel chartPanel;
    private final ChooseInputForm chooseInputForm;
    private final ConfigureInputForm configureInputForm;
    private float maxSumForScopeLine = 0;
    private IChartType burndownchartType;
    private IChartType burnupchartType;
    private boolean burndownChartPercentage = false;
    private boolean burnupChartPercentage = false;
    private ArrayList<Float> sumForScope;

    public CreateChart(ChooseInputForm chooseInputForm, ConfigureInputForm configureInputForm) {
        this.chooseInputForm = chooseInputForm;
        this.configureInputForm = configureInputForm;
    }

    /**
     * Creates charts
     *
     */
    public void create() {
        removeAll();
        // Get dates
        if (configureInputForm.getDatesCheckBox().isSelected()) {
            XAxisDateValues = getXAxisDateValues(configureInputForm.getStartDate(), configureInputForm.getEndDate());
        }
        // Get type
        burndownchartType = chooseInputForm.getBurndownChartType();
        burnupchartType = chooseInputForm.getBurnupChartType();
        totalForBurndown = burndownchartType.getTotalForBurndown();
        totalForBurnup = burnupchartType.getTotalForBurnup();
        // Burndown chart in percentage
        burndownChartPercentage = chooseInputForm.getBurndownChartCheckBox().isSelected() && chooseInputForm.getBurndownChartPercentageCheckBox().isSelected();
        if (burndownChartPercentage && totalForBurndown > 0) {
            totalForBurndownInPercentage = totalForBurndown;
            totalForBurndown = 100;
        }
        // Burn-up chart in percentage
        burnupChartPercentage = chooseInputForm.getBurnupChartCheckBox().isSelected() && chooseInputForm.getBurnupChartPercentageCheckBox().isSelected();
        // Sum for percentages and scope
        if ((chooseInputForm.getBurnupChartCheckBox().isSelected() && chooseInputForm.getScopeCheckBox().isSelected())
                || burnupChartPercentage) {
            if (configureInputForm.getDatesCheckBox().isSelected()) {
                sumForScope = burnupchartType.getSumDateRangeForScope(XAxisDateValues);
            } else if (configureInputForm.getIterationsCheckBox().isSelected()) {
                sumForScope = burnupchartType.getSumIterationRangeForScope(configureInputForm.getStartIteration(), configureInputForm.getEndIteration());
            }
        }
        if (burnupChartPercentage && totalForBurnup > 0) {
            totalForBurnupInPercentage = sumForScope.get(sumForScope.size() - 1);
            if (totalForBurnupInPercentage > 0) {
                initialTotalForBurnup = totalForBurnup;
                totalForBurnup = 100;
            } else {
                burnupChartPercentage = false; // we can't show the chart in percentage
            }
        }
        maxSumForScopeLine = 0;
        charts = createChart();
        chartPanel = new ChartPanel(charts);
        if (configureInputForm.getChartWidth() != 0 && configureInputForm.getChartHeight() != 0) {
            chartPanel.setPreferredSize(new Dimension(configureInputForm.getChartWidth(), configureInputForm.getChartHeight()));
        }
        add(chartPanel);
    }

    /**
     * Retrieves list of X-Axis date values
     *
     * @return list of dates
     */
    private ArrayList<Date> getXAxisDateValues(Date dateStart, Date dateEnd) {
        ArrayList<Date> dates = DateUtil.getDatesWithExclusions(dateStart, dateEnd,
                configureInputForm.getExcludeSaturdays().isSelected(),
                configureInputForm.getExcludeSundays().isSelected(),
                configureInputForm.getExcludedDates());
        return dates;
    }

    private Comparable getXAxisDateValue(int XAxisIndex) {
        return new ComparableCustomDateForXAxis(XAxisDateValues.get(XAxisIndex));
    }

    /**
     * Creates burndown/ burn-up target line chart
     *
     * @return dataset
     */
    private CategoryDataset createBurndownTargetDataset() {
        String label = chooseInputForm.getTargetLegend();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (configureInputForm.getDatesCheckBox().isSelected()) {
            for (int i = 0; i < XAxisDateValues.size(); i++) {
                // use double to make the values more accurate (tooltip disable - see renderer)
                dataset.addValue((Number) new Double(totalForBurndown - i * (totalForBurndown / (XAxisDateValues.size() - 1))), label, getXAxisDateValue(i));
            }
        } else if (configureInputForm.getIterationsCheckBox().isSelected()) {
            int size = configureInputForm.getEndIteration() - configureInputForm.getStartIteration() + 1;
            for (int i = 0; i < size; i++) {
                // use double to make the values more accurate (tooltip disable - see renderer)
                dataset.addValue((Number) new Double(totalForBurndown - i * (totalForBurndown / (size - 1))), label, i + configureInputForm.getStartIteration());
            }
        }
        return dataset;
    }

    private CategoryDataset createBurnupGuideDataset() {
        String label = chooseInputForm.getBurnupGuideLegend();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (configureInputForm.getDatesCheckBox().isSelected()) {
            int size = XAxisDateValues.size() - 1;
            for (int i = 0; i < XAxisDateValues.size(); i++) {
                Date date = XAxisDateValues.get(i);
                if (DateUtil.inFuture(date)) {
                    break;
                }
                size = i + 1;
            }
            if (size > 0) {
                for (int i = 0; i < XAxisDateValues.size(); i++) {
                    // use double to make the values more accurate (tooltip disable - see renderer)
                    if (burnupChartPercentage) {
                        dataset.addValue((Number) new Double((i + 1) * (((initialTotalForBurnup / size) / totalForBurnupInPercentage) * 100)), label, getXAxisDateValue(i));
                    } else {
                        dataset.addValue((Number) new Double((i + 1) * (totalForBurnup / size)), label, getXAxisDateValue(i));
                    }
                }
            }
        } else if (configureInputForm.getIterationsCheckBox().isSelected()) {
            int size = configureInputForm.getEndIteration() + 1;
            if (size > 0) {
                for (int i = configureInputForm.getStartIteration(); i <= configureInputForm.getEndIteration(); i++) {
                    // use double to make the values more accurate (tooltip disable - see renderer)
                    if (burnupChartPercentage) {
                        dataset.addValue((Number) new Double((i + 1) * (((initialTotalForBurnup / size) / totalForBurnupInPercentage) * 100)), label, i);
                    } else {
                        dataset.addValue((Number) new Double((i + 1) * (totalForBurnup / size)), label, i);
                    }
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
        String label = chooseInputForm.getScopeLegend();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        if (configureInputForm.getDatesCheckBox().isSelected()) {
            ArrayList<Float> sum = getSumDateRangeForScope();
            for (int i = 0; i < XAxisDateValues.size(); i++) {
                dataset.addValue((Number) sum.get(i), label, getXAxisDateValue(i));
            }
            if (sum.size() > 0) {
                maxSumForScopeLine = sum.get(sum.size() - 1);
            }
        } else if (configureInputForm.getIterationsCheckBox().isSelected()) {
            ArrayList<Float> sum = getSumIterationRangeForScope();
            for (int i = 0; i < sum.size(); i++) {
                dataset.addValue((Number) sum.get(i), label, i + configureInputForm.getStartIteration());
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
        String label = chooseInputForm.getPrimaryYAxisLegend();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        float total = totalForBurndown;
        if (configureInputForm.getDatesCheckBox().isSelected()) {
            for (int i = 0; i < XAxisDateValues.size(); i++) {
                Date date = XAxisDateValues.get(i);
                if (!DateUtil.inFuture(date)) {
                    for (Activity activity : ChartList.getList()) {
                        if (DateUtil.isEquals(activity.getDateCompleted(), date)) {
                            total -= getBurndownValue(activity);
                        }
                    }
                    total = burndownChartPercentage ? Math.round(total) : total;
                    dataset.addValue((Number) total, label, getXAxisDateValue(i));
                } else {
                    dataset.addValue((Number) 0, label, getXAxisDateValue(i));
                }
            }
        } else if (configureInputForm.getIterationsCheckBox().isSelected()) {
            for (int i = configureInputForm.getStartIteration(); i <= configureInputForm.getEndIteration(); i++) {
                for (Activity activity : ChartList.getList()) {
                    if (activity.getIteration() == i && activity.isCompleted()) {
                        total -= getBurndownValue(activity);
                    }
                }
                total = burndownChartPercentage ? Math.round(total) : total;
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
        String label = chooseInputForm.getSecondaryYAxisLegend();
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        float total = 0;
        if (configureInputForm.getDatesCheckBox().isSelected()) {
            for (int i = 0; i < XAxisDateValues.size(); i++) {
                Date date = XAxisDateValues.get(i);
                if (!DateUtil.inFuture(date)) {
                    for (Activity activity : ChartList.getList()) {
                        if (DateUtil.isEquals(activity.getDateCompleted(), date)) {
                            total += getBurnupValue(activity);
                        }
                    }
                    total = burnupChartPercentage ? Math.round(total) : total;
                    dataset.addValue((Number) total, label, getXAxisDateValue(i));
                } else {
                    dataset.addValue((Number) 0, label, getXAxisDateValue(i));
                }
            }
        } else if (configureInputForm.getIterationsCheckBox().isSelected()) {
            for (int i = configureInputForm.getStartIteration(); i <= configureInputForm.getEndIteration(); i++) {
                for (Activity activity : ChartList.getList()) {
                    if (activity.getIteration() == i && activity.isCompleted()) {
                        total += getBurnupValue(activity);
                    }
                }
                total = burnupChartPercentage ? Math.round(total) : total;
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
        if (chooseInputForm.getBurndownChartCheckBox().isSelected()
                && chooseInputForm.getTargetCheckBox().isSelected()
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
        categoryAxis.setTickLabelFont(getFont().deriveFont(Font.BOLD, getFont().getSize() - 3)); // x-axis font
        if (configureInputForm.getDatesCheckBox().isSelected()) {
            categoryAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45); // display diagonally
        }

        //////////////////// Y-AXIS //////////////////////////
        // BURNDOWN
        NumberAxis burndownRangeAxis = (NumberAxis) plot.getRangeAxis();
        if (chooseInputForm.getBurndownChartCheckBox().isSelected() && totalForBurndown > 0) { // BURNDOWN            
            // Horizontal/Grid lines for burndown
            plot.setRangeGridlinesVisible(true);
            plot.setRangeGridlineStroke(new BasicStroke((float) 1.5)); // plain stroke
            plot.setRangeGridlinePaint(Color.LIGHT_GRAY);
            // Customise the primary Y/Range axis
            burndownRangeAxis.setLabel(chooseInputForm.getPrimaryYAxisName());
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
            LayeredBarRenderer burndownRenderer = new LayeredBarRenderer();
            burndownRenderer.setSeriesPaint(0, chooseInputForm.getPrimaryYAxisColor());
            burndownRenderer.setSeriesBarWidth(0, 1.0);
            plot.setRenderer(4, burndownRenderer);
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
            burndownRenderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2}" + (burndownChartPercentage ? "%" : ""), NumberFormat.getInstance()));
            burndownRenderer.setBaseSeriesVisibleInLegend(!chooseInputForm.getPrimaryYAxisLegend().isEmpty());
            // Target
            if (chooseInputForm.getTargetCheckBox().isSelected()) {
                LineAndShapeRenderer burndownTargetrenderer = (LineAndShapeRenderer) plot.getRenderer();
                burndownTargetrenderer.setDrawOutlines(false);
                burndownTargetrenderer.setSeriesStroke(
                        0, new BasicStroke(
                                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                1.0f, new float[]{10.0f, 6.0f}, 0.0f));
                burndownTargetrenderer.setSeriesPaint(0, chooseInputForm.getTargetColor());
                // Disable tooltip
                burndownTargetrenderer.setBaseToolTipGenerator(null);
                burndownTargetrenderer.setBaseSeriesVisibleInLegend(chooseInputForm.getTargetCheckBox().isSelected() && !chooseInputForm.getTargetLegend().isEmpty());
                plot.setRenderer(0, burndownTargetrenderer);
            }
        } else {
            burndownRangeAxis.setVisible(false);
        }

        // BURN-UP
        if (chooseInputForm.getBurnupChartCheckBox().isSelected() && totalForBurnup > 0) {
            NumberAxis burnupRangeAxis = new NumberAxis();
            burnupRangeAxis.setLabel(chooseInputForm.getSecondaryYAxisName());
            burnupRangeAxis.setLabelFont(getFont().deriveFont(Font.BOLD));
            burnupRangeAxis.setAutoRangeIncludesZero(true);
            burnupRangeAxis.setAxisLineVisible(false);
            float burnupRange = totalForBurnup;
            // If scope is displayed the highest range between burnup and scope wins
            CategoryDataset scopeDataset = null;
            float scopeRange = 0;
            if (chooseInputForm.getScopeCheckBox().isSelected()) {
                scopeDataset = createBurnupScopeDataset();
                burnupRange = chooseInputForm.getScopeCheckBox().isSelected() && maxSumForScopeLine > totalForBurnup ? maxSumForScopeLine : totalForBurnup;
                if (chooseInputForm.getScopeCheckBox().isSelected()) {
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
            if (chooseInputForm.getBurndownChartCheckBox().isSelected()) {
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
            LayeredBarRenderer burnupRenderer = new LayeredBarRenderer();
            burnupRenderer.setSeriesPaint(0, chooseInputForm.getSecondaryYAxisColor());
            burnupRenderer.setSeriesBarWidth(0, 0.7);
            plot.setRenderer(3, burnupRenderer);
            plot.setDatasetRenderingOrder(DatasetRenderingOrder.REVERSE);
            burnupRenderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2}" + (burnupChartPercentage ? "%" : ""), NumberFormat.getInstance()));
            burnupRenderer.setBaseSeriesVisibleInLegend(!chooseInputForm.getSecondaryYAxisLegend().isEmpty());
            // GUIDE
            if (chooseInputForm.getBurnupGuideCheckBox().isSelected()) {
                CategoryDataset burnupGuideDataset = createBurnupGuideDataset();
                plot.setDataset(2, burnupGuideDataset);
                if (chooseInputForm.getBurndownChartCheckBox().isSelected()) { // when burndown, range axis for the target on the right
                    plot.mapDatasetToRangeAxis(2, 1);
                } else { // no burndown, range axis for the target on the left
                    plot.mapDatasetToRangeAxis(2, 0);
                }
                LineAndShapeRenderer burnupGuideRenderer = new LineAndShapeRenderer(true, false);
                burnupGuideRenderer.setDrawOutlines(false);
                burnupGuideRenderer.setSeriesStroke(
                        0, new BasicStroke(
                                2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                                1.0f, new float[]{10.0f, 6.0f}, 0.0f));
                burnupGuideRenderer.setToolTipGenerator(new StandardCategoryToolTipGenerator());
                burnupGuideRenderer.setSeriesPaint(0, chooseInputForm.getBurnupGuideColor());
                // Disable tooltip (also deprecated setToolTipGenerator(null) removes the tool tip here; setBaseToolTipGenerator(null) doesn't)
                burnupGuideRenderer.setToolTipGenerator(null);
                burnupGuideRenderer.setBaseSeriesVisibleInLegend(chooseInputForm.getBurnupGuideCheckBox().isSelected() && !chooseInputForm.getBurnupGuideLegend().isEmpty());
                plot.setRenderer(2, burnupGuideRenderer);
            }
            // SCOPE
            if (chooseInputForm.getScopeCheckBox().isSelected()) {
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
                scopeRenderer.setSeriesPaint(0, chooseInputForm.getScopeColor());
                // the following two lines make values being displayed onto the chart along the scope line
                scopeRenderer.setBaseItemLabelsVisible(true);
                scopeRenderer.setBaseItemLabelGenerator((CategoryItemLabelGenerator) new StandardCategoryItemLabelGenerator());
                // Disable tooltip (also deprecated setToolTipGenerator(null) removes the tool tip here; setBaseToolTipGenerator(null) doesn't)
                scopeRenderer.setToolTipGenerator(null);
                scopeRenderer.setBaseSeriesVisibleInLegend(chooseInputForm.getScopeCheckBox().isSelected() && !chooseInputForm.getScopeLegend().isEmpty());
                //scopeRenderer.setSeriesToolTipGenerator(0, new StandardCategoryToolTipGenerator("{2}" + (burnupChartPercentage ? "%" : ""), NumberFormat.getInstance()));
                plot.setRenderer(1, scopeRenderer);
            }
        }
        return chart;
    }

    private float getBurndownValue(Activity activity) {
        float value = burndownchartType.getValue(activity);
        if (burndownChartPercentage) {
            value = (value / totalForBurndownInPercentage) * 100;
        }
        return value;
    }

    private float getBurnupValue(Activity activity) {
        float value = burnupchartType.getValue(activity);
        if (burnupChartPercentage) {
            value = (value / totalForBurnupInPercentage) * 100;
        }
        return value;
    }

    private ArrayList<Float> getSumDateRangeForScope() {
        ArrayList<Float> sum = sumForScope;
        if (burnupChartPercentage) {
            for (int i = 0; i < sum.size(); i++) {
                sum.set(i, new Float(Math.round((sum.get(i) / totalForBurnupInPercentage) * 100)));
            }
        }
        return sum;
    }

    private ArrayList<Float> getSumIterationRangeForScope() {
        ArrayList<Float> sum = sumForScope;
        if (burnupChartPercentage) {
            for (int i = 0; i < sum.size(); i++) {
                sum.set(i, new Float(Math.round((sum.get(i) / totalForBurnupInPercentage) * 100)));
            }
        }
        return sum;
    }
}
