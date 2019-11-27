package io.jenkins.plugins.echarts.api.charts;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.annotations.VisibleForTesting;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import io.jenkins.plugins.echarts.api.charts.ChartModelConfiguration.AxisType;

import static java.util.Arrays.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class for {@link SeriesBuilder}
 * <p>
 * The class is tested using the dumb implementation {@link TestSeriesBuilder} for testing purposes to not depend on any
 * concrete implementations.
 *
 * @author Florian Pirchmoser
 */
class SeriesBuilderTest {
    private static final ChartModelConfiguration CONFIG_BUILD_COUNT_NONE = createWithBuildCount(0);
    private static final ChartModelConfiguration CONFIG_BUILD_COUNT_ONE = createWithBuildCount(1);
    private static final ChartModelConfiguration CONFIG_BUILD_COUNT_TWO = createWithBuildCount(2);
    private static final ChartModelConfiguration CONFIG_BUILD_DATE = createWithBuildDate();

    private static final LocalDateTime DAY = LocalDateTime.of(2000, 1, 1, 0, 0);
    private static final LocalDateTime PREVIOUS_DAY = DAY.minusDays(1);
    private static final LocalDateTime SAME_DAY = DAY.plusHours(4);
    private static final LocalDateTime NEXT_DAY = DAY.plusDays(1);

    private static final BuildResult<?> RUN_PREVIOUS_DAY = createRun(1, PREVIOUS_DAY);
    private static final BuildResult<?> RUN_DAY = createRun(2, DAY);
    private static final BuildResult<?> RUN_SAME_DAY = createRun(3, SAME_DAY);
    private static final BuildResult<?> RUN_NEXT_DAY = createRun(4, NEXT_DAY);

    private static final List<Integer> FIRST_SERIES = series(0, 1, 2);
    private static final List<Integer> SECOND_SERIES = series(3, 4, 5);
    private static final List<Integer> FORTH_SERIES = series(9, 10, 11);
    private static final List<Integer> AVERAGE_SECOND_AND_THIRD_SERIES = series(4, 5, 6);
    private static final String FIRST_KEY = "high";
    private static final String SECOND_KEY = "normal";
    private static final String THIRD_KEY = "low";

    @SuppressWarnings("PMD.UnusedPrivateMethod")
    @SuppressFBWarnings("UPM")
    private static Iterable<Object> createDataSetData() {
        return asList(
                new TestArgumentsBuilder()
                        .setTestName("build count 0, 2 runs")
                        .setTime(resultTime(false))
                        .setConfig(CONFIG_BUILD_COUNT_NONE)
                        .setRuns(RUN_SAME_DAY, RUN_DAY)
                        .setExpected(FIRST_SERIES)
                        .build(),

                new TestArgumentsBuilder()
                        .setTestName("build count 1, 2 runs")
                        .setTime(resultTime(false))
                        .setConfig(CONFIG_BUILD_COUNT_ONE)
                        .setRuns(RUN_SAME_DAY, RUN_DAY)
                        .setExpected(FIRST_SERIES)
                        .build(),

                new TestArgumentsBuilder()
                        .setTestName("build count 2, 0 runs")
                        .setTime(resultTime(false))
                        .setConfig(CONFIG_BUILD_COUNT_TWO)
                        .setRuns()
                        .setExpected()
                        .build(),

                new TestArgumentsBuilder()
                        .setTestName("build count 2, 1 run")
                        .setTime(resultTime(false))
                        .setConfig(CONFIG_BUILD_COUNT_TWO)
                        .setRuns(RUN_DAY)
                        .setExpected(FIRST_SERIES)
                        .build(),

                new TestArgumentsBuilder()
                        .setTestName("build count 2, 2 runs")
                        .setTime(resultTime(false))
                        .setConfig(CONFIG_BUILD_COUNT_TWO)
                        .setRuns(RUN_DAY, RUN_NEXT_DAY)
                        .setExpected(FIRST_SERIES, SECOND_SERIES)
                        .build(),

                new TestArgumentsBuilder()
                        .setTestName("build date, never too old, 0 runs")
                        .setTime(resultTime(false))
                        .setConfig(CONFIG_BUILD_DATE)
                        .setRuns()
                        .setExpected()
                        .build(),

                new TestArgumentsBuilder()
                        .setTestName("build date, never too old, 1 runs")
                        .setTime(resultTime(false))
                        .setConfig(CONFIG_BUILD_DATE)
                        .setRuns(RUN_DAY)
                        .setExpected(FIRST_SERIES)
                        .build(),

                new TestArgumentsBuilder()
                        .setTestName("build date, never too old, 2 runs")
                        .setTime(resultTime(false))
                        .setConfig(CONFIG_BUILD_DATE)
                        .setRuns(RUN_PREVIOUS_DAY, RUN_DAY)
                        .setExpected(FIRST_SERIES, SECOND_SERIES)
                        .build(),

                new TestArgumentsBuilder()
                        .setTestName("build date, always true, 2 runs")
                        .setTime(resultTime(true))
                        .setConfig(CONFIG_BUILD_DATE)
                        .setRuns(RUN_PREVIOUS_DAY, RUN_DAY)
                        .setExpected()
                        .build(),

                new TestArgumentsBuilder()
                        .setTestName("build date, first too old, 2 runs")
                        .setTime(resultTime(false, true))
                        .setConfig(CONFIG_BUILD_DATE)
                        .setRuns(RUN_PREVIOUS_DAY, RUN_DAY)
                        .setExpected(FIRST_SERIES)
                        .build(),

                new TestArgumentsBuilder()
                        .setTestName("build date, never too old, average same days")
                        .setTime(resultTime(false))
                        .setConfig(CONFIG_BUILD_DATE)
                        .setRuns(RUN_PREVIOUS_DAY, RUN_DAY, RUN_SAME_DAY, RUN_NEXT_DAY)
                        .setExpected(FIRST_SERIES, AVERAGE_SECOND_AND_THIRD_SERIES, FORTH_SERIES)
                        .build()
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("createDataSetData")
    void shouldCreateDataSet(@SuppressWarnings("unused") final String testName,
            final ResultTime time, final ChartModelConfiguration config,
            final Iterable<? extends BuildResult<Object>> runs, final List<List<Integer>> expected) {
        TimeFacade.reset();
        SeriesBuilder<Object> seriesBuilder = new TestSeriesBuilder(time);

        LinesDataSet result = seriesBuilder.createDataSet(config, runs);

        if (expected.isEmpty()) {
            assertThat(result.getDataSetIds()).isEmpty();
        }
        else {
            assertThat(result.getSeries(FIRST_KEY)).isEqualTo(expected.get(0));
            assertThat(result.getSeries(SECOND_KEY)).isEqualTo(expected.get(1));
            assertThat(result.getSeries(THIRD_KEY)).isEqualTo(expected.get(2));
        }
    }

    private static ChartModelConfiguration createWithBuildCount(final int count) {
        ChartModelConfiguration config = createConfig();
        when(config.isBuildCountDefined()).thenReturn(true);
        when(config.getBuildCount()).thenReturn(count);
        return config;
    }

    private static ChartModelConfiguration createWithBuildDate() {
        ChartModelConfiguration config = createConfig();
        when(config.getAxisType()).thenReturn(AxisType.DATE);
        return config;
    }

    private static ChartModelConfiguration createConfig() {
        return mock(ChartModelConfiguration.class);
    }

    private static BuildResult<?> createRun(final int buildNumber, final LocalDateTime buildTime) {
        Build build = new Build(buildNumber, String.format("#%s", buildNumber),
                buildTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        return new BuildResult<>(build, DAY);
    }

    private static ResultTime resultTime(final Boolean value, final Boolean... continuations) {
        ResultTime time = mock(ResultTime.class);
        when(time.isResultTooOld(any(ChartModelConfiguration.class), any(BuildResult.class)))
                .thenReturn(value, continuations);
        return time;
    }

    private static List<Integer> series(final Integer... values) {
        return asList(values);
    }

    /**
     * Dumb test implementation returning integers starting with 1 to n as series, three at a time.
     */
    private static class TestSeriesBuilder extends SeriesBuilder<Object> {
        private int count;

        @VisibleForTesting
        TestSeriesBuilder(final ResultTime resultTime) {
            super(resultTime);
        }

        @Override
        protected Map<String, Integer> computeSeries(final Object current) {
            Map<String, Integer> values = new HashMap<>();
            values.put(FIRST_KEY, count++);
            values.put(SECOND_KEY, count++);
            values.put(THIRD_KEY, count++);
            return values;
        }
    }

    /**
     * Helps building arguments to parameterized test.
     */
    private static class TestArgumentsBuilder {
        private String testName;
        private ChartModelConfiguration config;
        private List<BuildResult<?>> runs;
        private List<List<Integer>> series;
        private ResultTime time;

        /**
         * Set the tests configuration.
         *
         * @param config
         *         to use in test
         *
         * @return this
         */
        TestArgumentsBuilder setConfig(final ChartModelConfiguration config) {
            this.config = config;

            return this;
        }

        /**
         * Set the name displayed as test name.
         *
         * @param name
         *         of the test
         *
         * @return this
         */
        TestArgumentsBuilder setTestName(final String name) {
            testName = name;

            return this;
        }

        /**
         * Set the result time used in test.
         *
         * @param time
         *         used in test.
         *
         * @return this
         */
        TestArgumentsBuilder setTime(final ResultTime time) {
            this.time = time;

            return this;
        }

        /**
         * Set the analysis runs used in test.
         *
         * @param runs
         *         used in test, defaults to empty list
         *
         * @return this
         */
        TestArgumentsBuilder setRuns(final BuildResult<?>... runs) {
            this.runs = asList(runs);

            return this;
        }

        /**
         * Set the tests expectations.
         *
         * @param expectedSeries
         *         to use in test, defaults to empty list
         *
         * @return this
         */
        @SafeVarargs
        final TestArgumentsBuilder setExpected(final List<Integer>... expectedSeries) {
            series = new ArrayList<>();

            if (expectedSeries.length == 0) {
                return this;
            }

            int dataSetSize = expectedSeries[0].size();

            for (int i = 0; i < dataSetSize; i++) {
                List<Integer> dataSetValues = new ArrayList<>();
                series.add(dataSetValues);
                dataSetValues.add(expectedSeries[0].get(i));
            }

            for (int s = 1; s < expectedSeries.length; s++) {
                for (int i = 0; i < dataSetSize; i++) {
                    List<Integer> dataSetValues = series.get(i);
                    dataSetValues.add(expectedSeries[s].get(i));
                }
            }

            return this;
        }

        /**
         * Builds the tests argument.
         *
         * @return test arg
         */
        Object build() {
            return Arguments.of(
                    testName,
                    time,
                    config,
                    runs,
                    series
            );
        }
    }
}
