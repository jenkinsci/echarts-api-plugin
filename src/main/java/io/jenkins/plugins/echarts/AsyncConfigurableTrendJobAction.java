package io.jenkins.plugins.echarts;

import edu.hm.hafner.echarts.JacksonFacade;
import edu.hm.hafner.echarts.LinesChartModel;

import org.kohsuke.stapler.bind.JavaScriptMethod;
import hudson.model.Job;

import io.jenkins.plugins.util.BuildAction;

/**
 * A job action displays a link on the side panel of a job that refers to the last build that contains results (i.e. a
 * {@link BuildAction} with a corresponding result). This action also is responsible to render the historical trend via
 * its associated 'floatingBox.jelly' view. The trend chart will be rendered by ECharts: the model of the chart will be
 * obtained using an asynchronous Ajax call.
 *
 * @param <T>
 *         type of the results
 *
 * @author Ullrich Hafner
 */
public abstract class AsyncConfigurableTrendJobAction<T extends BuildAction<?>> extends AsyncTrendJobAction<T>
        implements AsyncConfigurableTrendChart {

    private static final String EMPTY_CONFIGURATION = "{}";

    /**
     * Creates a new instance of {@link AsyncConfigurableTrendJobAction}.
     *
     * @param owner
     *         the job that owns this action
     * @param buildActionClass
     *         the type of the action to find
     */
    protected AsyncConfigurableTrendJobAction(final Job<?, ?> owner, final Class<T> buildActionClass) {
        super(owner, buildActionClass);
    }

    @JavaScriptMethod
    @Override
    public final String getBuildTrendModel() {
        return getConfigurableBuildTrendModel(EMPTY_CONFIGURATION);
    }

    @JavaScriptMethod
    @Override
    public String getConfigurableBuildTrendModel(final String configuration) {
        return new JacksonFacade().toJson(createChartModel(configuration));
    }

    /**
     * Creates the model of the trend chart. The returned model will be converted to JSON and inserted into the client
     * side model of the corresponding ECharts instance.
     *
     * @param configuration
     *         JSON configuration of the chart (number of builds, etc.). It is up to an individual plugin to correctly
     *         create this configuration in the trend configuration dialog.
     * @return the model of the trend chart
     * @see LinesChartModel for details on how to construct such a model
     */
    protected abstract LinesChartModel createChartModel(String configuration);

    @Override
    protected final LinesChartModel createChartModel() {
        return createChartModel(EMPTY_CONFIGURATION);
    }
}
