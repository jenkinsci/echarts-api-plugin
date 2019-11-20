package io.jenkins.plugins.echarts.api.charts;

/**
 * Provides results of a build that should be rendered.
 */
public interface BuildResult<T> {
    /**
     * Returns the associated build that this run was part of.
     *
     * @return the associated build
     */
    Build getBuild();

    T getResult();
}
