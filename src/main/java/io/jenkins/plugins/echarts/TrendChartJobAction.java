package io.jenkins.plugins.echarts;

import edu.hm.hafner.echarts.BuildResult;
import edu.hm.hafner.echarts.ChartModelConfiguration;
import edu.hm.hafner.echarts.JacksonFacade;
import edu.hm.hafner.echarts.line.LinesChartModel;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Iterator;

import org.kohsuke.stapler.bind.JavaScriptMethod;
import hudson.model.Job;

import io.jenkins.plugins.util.BuildAction;
import io.jenkins.plugins.util.JobAction;

/**
 * A job action displays a link on the side panel of a job that refers to the last build that contains results (i.e., a
 * {@link BuildAction} with a corresponding result). This action also is responsible to render the historical trend via
 * its associated 'floatingBox.jelly' view. ECharts will render the trend chart: the model of the chart will be
 * obtained using an asynchronous Ajax call. This action basically is the same as {@link TrendChartJobAction},
 * it additionally provides a configuration of the trend chart using a JSON object (given as a string value). This
 * JSON object can be populated in the client using a configuration dialog (or using the browser's local storage).
 * For the default trend charts that are simply built around {@link ChartModelConfiguration} you can include the provided
 * global configuration dialog with {@code <st:adjunct includes="io.jenkins.plugins.echarts-trend-default-setup"/>}.
 *
 * @param <T>
 *         type of the results
 *
 * @author Ullrich Hafner
 */
public abstract class TrendChartJobAction<T extends BuildAction<?>> extends JobAction<T>
        implements AsyncConfigurableTrendChart {
    @NonNull // all actions that show a trend chart should get a unique ID
    @Override
    public abstract String getUrlName();

    /**
     * Creates a new instance of {@link TrendChartJobAction}.
     *
     * @param owner
     *         the job that owns this action
     * @param buildActionClass
     *         the type of the action to find
     */
    protected TrendChartJobAction(final Job<?, ?> owner, final Class<T> buildActionClass) {
        super(owner, buildActionClass);
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

    /**
     * This default implementation checks whether there are at least two build results available. Override if this
     * behavior does not make sense in your job action.
     *
     * @return {@code true} if there are at least two results, {@code false} otherwise
     */
    @Override
    @SuppressWarnings("unused") // Called by jelly view
    public boolean isTrendVisible() {
        return hasAtLeastTwoResults();
    }

    private boolean hasAtLeastTwoResults() {
        Iterable<? extends BuildResult<T>> results = createBuildHistory();
        Iterator<? extends BuildResult<T>> iterator = results.iterator();

        if (iterator.hasNext()) {
            iterator.next();
        }
        return iterator.hasNext();
    }

    /**
     * Creates a history of build results. This default implementation selects all actions that share the same URL and
     * returns these actions as value for the trend charts.
     *
     * @return the build history
     * @see GenericBuildActionIterator for details on how to construct such an iterator
     */
    protected Iterable<? extends BuildResult<T>> createBuildHistory() {
        return () -> new GenericBuildActionIterator<>(
                getBuildActionClass(), getLatestAction(),
                a -> getUrlName().equals(a.getUrlName()), a -> a);
    }
}
