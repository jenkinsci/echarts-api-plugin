package io.jenkins.plugins.echarts.api.charts;

import java.time.LocalDate;
import java.time.ZoneId;

import org.junit.jupiter.api.Test;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link ResultTime}.
 *
 * @author Ullrich Hafner
 */
class ResultTimeTest {
    private static final int DAY_COUNT = 2;

    /**
     * Verifies that the day count property is not evaluated if {@link ChartModelConfiguration#isDayCountDefined()} is
     * disabled.
     */
    @Test
    @SuppressFBWarnings("RV")
    void shouldNotEvaluateDayCountIfOptionIsDeactivated() {
        LocalDate today = LocalDate.now();
        stubToday(today);
        ResultTime time = new ResultTime();

        ChartModelConfiguration configuration = createConfiguration(false);

        BuildResult<?> run = createRunAt(today.minusYears(20));

        assertThat(time.isResultTooOld(configuration, run)).as("Result date marked as too old").isFalse();
        verify(configuration, never()).getDayCount();
    }

    /**
     * Verifies that the day count property is correctly evaluated if {@link ChartModelConfiguration#isDayCountDefined()} is
     * enabled.
     */
    @Test
    void shouldEvaluateDayCountIfOptionIsEnabled() {
        LocalDate today = LocalDate.now();
        stubToday(today);
        ResultTime time = new ResultTime();

        assertThatRunIsWithinDayCount(today, time);

        assertThatRunIsWithinDayCount(today.minusDays(1), time);
        assertThatRunIsWithinDayCount(today.minusDays(2), time);

        assertThatRunIsOutsideOfDayCount(today.minusDays(3), time);
        assertThatRunIsOutsideOfDayCount(today.minusDays(4), time);

        assertThatRunIsWithinDayCount(today.plusDays(1), time);
        assertThatRunIsWithinDayCount(today.plusDays(2), time);

        assertThatRunIsOutsideOfDayCount(today.plusDays(3), time);
        assertThatRunIsOutsideOfDayCount(today.plusDays(4), time);
    }

    private void stubToday(final LocalDate today) {
        TimeFacade timeFacade = mock(TimeFacade.class);
        when(timeFacade.getToday()).thenReturn(today);
        TimeFacade.setInstance(timeFacade);
    }

    private BuildResult<?> createRunAt(final LocalDate now) {
        BuildResult<?> run = mock(BuildResult.class);
        Build build = mock(Build.class);
        when(build.getBuildTime()).thenReturn(now.atStartOfDay(ZoneId.systemDefault()).toInstant().getEpochSecond());
        when(run.getBuild()).thenReturn(build);
        return run;
    }

    private ChartModelConfiguration createConfiguration(final boolean isDayCountDefined) {
        ChartModelConfiguration configuration = mock(ChartModelConfiguration.class);
        when(configuration.isDayCountDefined()).thenReturn(isDayCountDefined);
        return configuration;
    }

    private void assertThatRunIsOutsideOfDayCount(final LocalDate runDate, final ResultTime time) {
        ChartModelConfiguration configuration = createChartModelConfigurationWithDayCount();

        BuildResult<?> run = createRunAt(runDate);

        assertThat(time.isResultTooOld(configuration, run)).as("Result date marked as ok").isEqualTo(true);
    }

    private void assertThatRunIsWithinDayCount(final LocalDate runDate, final ResultTime time) {
        ChartModelConfiguration configuration = createChartModelConfigurationWithDayCount();

        BuildResult<?> run = createRunAt(runDate);

        assertThat(time.isResultTooOld(configuration, run)).as("Result date marked as too old").isEqualTo(false);
    }

    private ChartModelConfiguration createChartModelConfigurationWithDayCount() {
        ChartModelConfiguration configuration = createConfiguration(true);
        when(configuration.getDayCount()).thenReturn(DAY_COUNT);
        return configuration;
    }
}
