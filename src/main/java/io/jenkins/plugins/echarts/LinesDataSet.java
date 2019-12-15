package io.jenkins.plugins.echarts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Model of a line chart with multiple data sets. Each data set is represented by a unique ID. The actual data of each
 * data set is stored in a list of integer values which represent a value for an X-axis tick. In order to get multiple
 * data sets correctly aligned, the data points for each data set must contain exactly the same number of values.
 *
 * @author Ullrich Hafner
 */
public class LinesDataSet {
    private final Map<String, List<Integer>> dataSetSeries = new HashMap<>();
    private final List<String> domainAxisLabels = new ArrayList<>();
    private final List<Integer> buildNumbers = new ArrayList<>();

    public int getDomainAxisSize() {
        return domainAxisLabels.size();
    }

    public List<String> getDomainAxisLabels() {
        return domainAxisLabels;
    }

    public Set<String> getDataSetIds() {
        return dataSetSeries.keySet();
    }

    boolean hasSeries(final String dataSetId) {
        return dataSetSeries.containsKey(dataSetId);
    }

    /**
     * Returns the data series of the specified dataSetId.
     *
     * @param dataSetId
     *         the ID of the series
     *
     * @return the series (list of integer values for each X-Axis label)
     */
    public List<Integer> getSeries(final String dataSetId) {
        if (!hasSeries(dataSetId)) {
            throw new NoSuchElementException(String.format("No dataset '%s' registered", dataSetId));
        }

        return dataSetSeries.get(dataSetId);
    }

    /**
     * Adds data points for a new domainAxisLabel. The data points for the X-axis tick are given by a map. Each dataSetId
     * provides one value for the specified X-axis label.
     *
     * @param domainAxisLabel
     *         the label of the X-axis
     * @param dataSetValues
     *         the values for each of the series at the given X-axis tick
     */
    public void add(final String domainAxisLabel, final Map<String, Integer> dataSetValues) {
        if (domainAxisLabels.contains(domainAxisLabel)) {
            throw new IllegalStateException("Label already registered: " + domainAxisLabel);
        }

        domainAxisLabels.add(domainAxisLabel);

        for (Entry<String, Integer> dataPoints : dataSetValues.entrySet()) {
            dataSetSeries.putIfAbsent(dataPoints.getKey(), new ArrayList<>());
            dataSetSeries.get(dataPoints.getKey()).add(dataPoints.getValue());
        }
    }

    /**
     * Adds data points for a new domainAxisLabel. The data points for the X-axis tick are given by a map. Each dataSetId
     * provides one value for the specified X-axis label.
     *
     * @param domainAxisLabel
     *         the label of the X-axis
     * @param dataSetValues
     *         the values for each of the series at the given X-axis tick
     * @param buildNumber
     *         the number of the associated build
     */
    public void add(final String domainAxisLabel, final Map<String, Integer> dataSetValues, final int buildNumber) {
        add(domainAxisLabel, dataSetValues);

        if (buildNumbers.contains(buildNumber)) {
            throw new IllegalStateException("Build number already registered: " + buildNumber);
        }


        buildNumbers.add(buildNumber);
    }

    public List<Integer> getBuildNumbers() {
        return buildNumbers;
    }
}
