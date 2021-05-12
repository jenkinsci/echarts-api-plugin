package io.jenkins.plugins.echarts;

import edu.hm.hafner.echarts.LinesChartModel;

import org.kohsuke.stapler.bind.JavaScriptMethod;

/**
 * Provides the trend chart for a job. The trend chart model will be rendered by ECharts: the model of the chart will be
 * obtained using an asynchronous Ajax call.
 *
 * @author Ullrich Hafner
 */
public interface AsyncConfigurableTrendChart {
    /**
     * Returns the UI model for an ECharts chart that shows the build trend. The model has to be a JSON representation
     * of a corresponding {@link LinesChartModel} instance, that will be inserted into the client side model of the
     * corresponding ECharts instance.
     *
     * @param configuration
     *         JSON configuration of the chart (number of builds, etc.). It is up to an individual plugin to correctly
     *         create this configuration in the trend configuration dialog.
     *
     * @return the UI model as JSON
     * @see LinesChartModel for details on how to construct such a model
     * @see AsyncTrendJobAction for an example on how to provide the JSON representation
     */
    @JavaScriptMethod
    @SuppressWarnings("unused")
    // Called by jelly view
    String getConfigurableBuildTrendModel(String configuration);

    /**
     * Returns whether the trend report should be shown.
     *
     * @return {@code true} if the trend should be shown, {@code false} otherwise
     */
    @SuppressWarnings("unused")
    // Called by jelly view
    boolean isTrendVisible();
}
