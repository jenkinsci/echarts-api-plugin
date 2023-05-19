package io.jenkins.plugins.echarts;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JenkinsPaletteTest {
    @Test
    void shouldCreateRotatingPalette() {
        var palette = JenkinsPalette.CHART_COLORS;

        for (int i = 0; i < palette.size(); i++) {
            assertThat(JenkinsPalette.chartColor(i)).isEqualTo(JenkinsPalette.chartColor(i + 10));
            assertThat(JenkinsPalette.chartColor(i + palette.size())).isEqualTo(palette.get(i));
        }

        assertThat(JenkinsPalette.GREY.light()).isEqualTo("--light-grey");
        assertThat(JenkinsPalette.GREY.normal()).isEqualTo("--medium-grey");
        assertThat(JenkinsPalette.GREY.dark()).isEqualTo("--dark-grey");
    }
}
