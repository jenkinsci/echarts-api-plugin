package io.jenkins.plugins.echarts;

import edu.hm.hafner.echarts.BuildResult;
import edu.hm.hafner.echarts.JacksonFacade;
import edu.hm.hafner.echarts.LinesChartModel;

import java.util.Iterator;

import org.kohsuke.stapler.bind.JavaScriptMethod;
import hudson.model.Job;

import io.jenkins.plugins.util.BuildAction;
import io.jenkins.plugins.util.JobAction;

/**
 * A job action displays a link on the side panel of a job that refers to the last build that contains results (i.e., a
 * {@link BuildAction} with a corresponding result). This action also is responsible to render the historical trend via
 * its associated 'floatingBox.jelly' view. ECharts will render the trend chart: the model of the chart will be
 * obtained using an asynchronous Ajax call.
 *
 * @param <T>
 *         type of the results
 *
 * @author Ullrich Hafner
 */
public abstract class AsyncTrendJobAction<T extends BuildAction<?>> extends JobAction<T> implements AsyncTrendChart {
    /**
     * Creates a new instance of {@link AsyncTrendJobAction}.
     *
     * @param owner
     *         the job that owns this action
     * @param buildActionClass
     *         the type of the action to find
     */
    protected AsyncTrendJobAction(final Job<?, ?> owner, final Class<T> buildActionClass) {
        super(owner, buildActionClass);
    }

    @Override
    @JavaScriptMethod
    @SuppressWarnings("unused") // Called by jelly view
    public String getBuildTrendModel() {
        return new JacksonFacade().toJson(createChartModel());
    }

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
     * Creates the model of the trend chart. The returned model will be converted to JSON and inserted into the client
     * side model of the corresponding ECharts instance.
     *
     * @return the model of the trend chart
     * @see LinesChartModel for details on how to construct such a model
     */
    protected abstract LinesChartModel createChartModel();

    protected Iterable<? extends BuildResult<T>> createBuildHistory() {
        return () -> new BuildActionIterator<>(getBuildActionClass(), getLatestAction());
    }
}
