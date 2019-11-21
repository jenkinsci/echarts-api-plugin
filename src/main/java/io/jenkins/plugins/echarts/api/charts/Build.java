package io.jenkins.plugins.echarts.api.charts;

import java.util.Objects;

import hudson.model.Run;

/**
 * Represents the build that contains results that should be rendered using ECharts.
 *
 * @author Ullrich Hafner
 */
public class Build implements Comparable<Build> {
    private long timeInMillis;
    private int number;
    private String displayName;

    /**
     * Creates a new instance of {@link Build}.
     *
     * @param run
     *         the properties of the run
     */
    public Build(final Run<?, ?> run) {
        this(run.getNumber(), run.getDisplayName(), run.getTimeInMillis());
    }

    /**
     * Creates a new instance of {@link Build}.
     *
     * @param number
     *         build number
     * @param displayName
     *         human readable name of the build
     * @param timeInMillis
     *         the build time (in milli seconds)
     */
    public Build(final int number, final String displayName, final long timeInMillis) {
        this.timeInMillis = timeInMillis;
        this.number = number;
        this.displayName = displayName;
    }

    /**
     * Returns the start time value of this build in milliseconds.
     *
     * @return the time as UTC milliseconds from the epoch
     */
    public long getTimeInMillis() {
        return timeInMillis;
    }

    /**
     * Returns the number of this build as assigned by Jenkins' scheduler.
     *
     * @return the number of this build
     */
    public int getNumber() {
        return number;
    }

    /**
     * Returns a human readable label for this build.
     *
     * @return the name to be used in the user interface
     */
    public String getDisplayName() {
        return displayName;
    }

    @Override
    public int compareTo(final Build o) {
        return getNumber() - o.getNumber();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Build that = (Build) o;
        return number == that.number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
