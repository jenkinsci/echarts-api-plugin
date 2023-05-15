package io.jenkins.plugins.echarts;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class JenkinsPaletteTest {
    @Test
    void shouldCreateRotatingPalette() {
        var palette = JenkinsPalette.chartColors();

        for (int i = 0; i < palette.size(); i++) {
            assertThat(JenkinsPalette.chartColor(i)).isEqualTo(palette.get(i));
            assertThat(JenkinsPalette.chartColor(i + palette.size())).isEqualTo(palette.get(i));
        }
    }
}
