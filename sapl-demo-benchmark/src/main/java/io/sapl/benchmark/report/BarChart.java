package io.sapl.benchmark.report;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static io.sapl.benchmark.report.Utilities.getMaxValue;

public class BarChart {
    private final JFreeChart chart;
    private final CategoryPlot categoryPlot;
    private final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

    public BarChart(String title, String valueAxisLabel){
        chart = ChartFactory.createBarChart(
                title,
                null, valueAxisLabel,
                dataset, PlotOrientation.VERTICAL,
                true, true, false);

        categoryPlot = chart.getCategoryPlot();
        BarRenderer br = (BarRenderer) categoryPlot.getRenderer();
        br.setMaximumBarWidth(.1); // set maximum width to 10% of chart
    }

    public void useLogAxis(){
        final NumberAxis rangeAxis = new LogarithmicAxis(categoryPlot.getRangeAxis().getLabel());
        rangeAxis.setUpperBound(getMaxValue(dataset)*2);
        categoryPlot.setRangeAxis(rangeAxis);
        categoryPlot.setRenderer(categoryPlot.getRenderer());
    }

    public void showLabels(){
        showLabels(new StandardCategoryItemLabelGenerator());
    }

    public void showLabels(CategoryItemLabelGenerator categoryItemLabelGenerator){
        for (int i=0; i<dataset.getRowCount(); i++){
            categoryPlot.getRenderer().setSeriesItemLabelGenerator(i, categoryItemLabelGenerator);
            categoryPlot.getRenderer().setSeriesItemLabelsVisible(i, true);
        }
        categoryPlot.setRenderer(categoryPlot.getRenderer());
    }

    public void addBenchmarkResult(String pdp, String authMethod, Double score){
        dataset.addValue( score, pdp, authMethod);
    }

    public void saveToPNGFile(File file) throws IOException {
        saveToPNGFile(file, 640, 400);
    }

    public void saveToPNGFile(File file, int width, int height) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        ChartUtils.writeScaledChartAsPNG(fos, chart, width, height, 3, 3);
        fos.close();
    }
}
