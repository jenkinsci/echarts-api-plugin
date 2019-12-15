package io.jenkins.plugins.echarts;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

/**
 * UI model for the data series of an ECharts pie chart.
 * <p>
 * This class will be automatically converted to a JSON object.
 * </p>
 *
 * @author Ullrich Hafner
 */
public class PieChartModel {
    private final List<PieData> data = new ArrayList<>();
    private final List<String> colors = new ArrayList<>();
    private final String name;

    /**
     * Creates a new {@link PieChartModel} that does not provide a name.
     */
    public PieChartModel() {
        this(StringUtils.EMPTY);
    }

    /**
     * Creates a new {@link PieChartModel} with the specified human readable name.
     *
     * @param name
     *         the name of the chart
     */
    public PieChartModel(final String name) {
        this.name = name;
    }

    /**
     * Adds the specified data element to the existing set of data elements.
     *
     * @param pieData
     *         the data element to add
     * @param color
     *         the color of the element
     */
    public void add(final PieData pieData, final Palette color) {
        data.add(pieData);
        colors.add(color.getNormal());
    }

    public String getName() {
        return name;
    }

    public List<PieData> getData() {
        return data;
    }

    public List<String> getColors() {
        return colors;
    }
}
