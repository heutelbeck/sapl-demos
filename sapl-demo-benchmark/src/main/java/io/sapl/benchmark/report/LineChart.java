package io.sapl.benchmark.report;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtils;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static io.sapl.benchmark.report.Utilities.getMaxValue;

public class LineChart {
    private final JFreeChart chart;
    private final DefaultCategoryDataset dataset = new DefaultCategoryDataset( );

    public LineChart(String title, String valueAxisLabel) {
        chart = ChartFactory.createLineChart(
                title,
                "iteration", valueAxisLabel,
                dataset, PlotOrientation.VERTICAL,
                true, true, false);
    }

    public void addValue(Double y_value, String category, String x_value ){
        dataset.addValue( y_value, category, x_value);
    }


    public void arrangeYAxis(){
        var maxValue = getMaxValue(dataset);
        CategoryPlot plot = chart.getCategoryPlot();
        var yAxis = (NumberAxis)plot.getRangeAxis();
        yAxis.setAutoRange(false);
        yAxis.setUpperBound(maxValue*1.05);
        yAxis.setLowerBound(0);
        chart.getCategoryPlot().setRenderer(plot.getRenderer());

    }

    public void saveToPNGFile(File file) throws IOException {
        arrangeYAxis();
        saveToPNGFile(file, 640, 400);
    }

    public void saveToPNGFile(File file, int width, int height) throws IOException {
        arrangeYAxis();
        FileOutputStream fos = new FileOutputStream(file);
        ChartUtils.writeScaledChartAsPNG(fos, chart, width, height, 3, 3);
        fos.close();
    }
}
