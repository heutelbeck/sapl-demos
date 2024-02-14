package io.sapl.benchmark.report;

import org.jfree.data.category.CategoryDataset;

public class Utilities {
    public static Double getMaxValue(CategoryDataset dataset){
        double max = Double.MIN_VALUE;
        for (int r = 0; r < dataset.getRowCount(); r++) {
            for (int c = 0; c < dataset.getColumnCount(); c++) {
                Number number = dataset.getValue(r, c);
                double value = number == null ? Double.NaN : number.doubleValue();
                if (value > max) {
                    max = value;
                }
            }
        }
        return max;
    }
}
