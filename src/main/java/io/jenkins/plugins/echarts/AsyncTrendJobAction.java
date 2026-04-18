package io.jenkins.plugins.echarts;

import edu.hm.hafner.echarts.BuildResult;
import edu.hm.hafner.echarts.LinesChartModel;
import edu.umd.cs.findbugs.annotations.CheckForNull;

import java.util.Iterator;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

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
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

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
        return OBJECT_MAPPER.writeValueAsString(createChartModel());
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

    /**
     * Returns the text value of the specified JSON property.
     *
     * @param json
     *         the JSON object to extract the property value from
     * @param property
     *         the name of the property
     * @param defaultValue
     *         the default value if the property is undefined or invalid
     *
     * @return the value of the property
     */
    protected String getStringFromJson(final String json, final String property, final String defaultValue) {
        try {
            var typeNode = getPropertyAsNode(json, property);
            if (typeNode != null) {
                return typeNode.asString(defaultValue);
            }
        }
        catch (JacksonException exception) {
            // ignore
        }

        return defaultValue;
    }

    /**
     * Returns the text value of the specified JSON property.
     *
     * @param json
     *         the JSON object to extract the property value from
     * @param property
     *         the name of the property
     * @param defaultValue
     *         the default value if the property is undefined or invalid
     *
     * @return the value of the property
     */
    protected int getIntegerFromJson(final String json, final String property, final int defaultValue) {
        try {
            var typeNode = getPropertyAsNode(json, property);
            if (typeNode != null) {
                return typeNode.asInt(defaultValue);
            }
        }
        catch (JacksonException exception) {
            // ignore
        }

        return defaultValue;
    }

    @CheckForNull
    private JsonNode getPropertyAsNode(final String json, final String property)
            throws JacksonException {
        var node = OBJECT_MAPPER.readValue(json, ObjectNode.class);
        return node.get(property);
    }
}
