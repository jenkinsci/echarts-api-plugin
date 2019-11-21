package io.jenkins.plugins.echarts.api.charts;

/**
 * Provides results of a build that should be rendered.
 */
public class BuildResult<T> {
    private final Build build;
    private final T result;

    /**
     * Creates a new instance of {@link BuildResult}.
     *
     * @param build
     *         the build
     * @param result
     *         the result of the specified build
     */
    public BuildResult(final Build build, final T result) {
        this.build = build;
        this.result = result;
    }

    /**
     * Returns the associated build that this run was part of.
     *
     * @return the associated build
     */
    public Build getBuild() {
        return build;
    }

    /**
     * Returns the result for the specified build.
     *
     * @return the result
     */
    public T getResult() {
        return result;
    }
}
