package io.jenkins.plugins.echarts;

import java.util.List;
import java.util.Locale;

/**
 * Jenkins color palette. Each color is represented as a CSS variable that will be rendered according to the selected
 * theme.
 *
 * @author Ullrich Hafner
 * @see "JS method resolveJenkinsColors in file echarts-scope.js"
 * @see <a href="https://github.com/jenkinsci/jenkins/blob/master/war/src/main/scss/abstracts/theme.scss">Jenkins
 *         colors</a>
 */
public enum JenkinsPalette {
    BLACK(Variation.NO_VARIATION),
    BLUE,
    BROWN,
    CYAN,
    GREY,
    GREEN,
    INDIGO,
    ORANGE,
    PINK,
    PURPLE,
    RED,
    WHITE(Variation.NO_VARIATION),
    YELLOW;

    private static final List<JenkinsPalette> CHART_COLORS = List.of(JenkinsPalette.RED, JenkinsPalette.BLUE,
            JenkinsPalette.YELLOW, JenkinsPalette.GREEN, JenkinsPalette.CYAN, JenkinsPalette.INDIGO,
            JenkinsPalette.ORANGE, JenkinsPalette.PINK, JenkinsPalette.PURPLE, JenkinsPalette.BROWN);

    /**
     * Returns a list of different colors that can be used for charts.
     *
     * @return a list of colors
     */
    public static List<JenkinsPalette> chartColors() {
        return CHART_COLORS;
    }

    /**
     * Returns a chart color that can be used to render element {@code n} in a chart.
     *
     * @param n
     *         the n-th element to render
     *
     * @return a color to be used for rendering the n-th element
     */
    public static JenkinsPalette chartColor(final int n) {
        return CHART_COLORS.get(n % CHART_COLORS.size());
    }

    private final Variation variation;

    JenkinsPalette() {
        this(Variation.VARIATION);
    }

    JenkinsPalette(final Variation variation) {
        this.variation = variation;
    }

    private enum Variation {
        NO_VARIATION,
        VARIATION
    }

    private static final String PREFIX = "--";
    private static final String DARK_ID = "dark-";
    private static final String LIGHT_ID = "light-";

    /**
     * Returns the CSS variable name for this color (light variation).
     *
     * @return the CSS variable name
     */
    public String light() {
        return getVariation(LIGHT_ID);
    }

    /**
     * Returns the CSS variable name for this color.
     *
     * @return the CSS variable name
     */
    public String normal() {
        return PREFIX + cssName();
    }

    /**
     * Returns the CSS variable name for this color (dark variation).
     *
     * @return the CSS variable name
     */
    public String dark() {
        return getVariation(DARK_ID);
    }

    private String getVariation(final String prefix) {
        return variation == Variation.NO_VARIATION ? normal() : PREFIX + prefix + cssName();
    }

    private String cssName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
