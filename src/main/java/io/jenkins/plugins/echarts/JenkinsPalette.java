package io.jenkins.plugins.echarts;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

/**
 * Jenkins color palette. Each color is represented as a CSS variable that will be rendered according to the selected
 * theme.
 *
 * @author Ullrich Hafner
 * @see "JS method resolveJenkinsColors in file echarts-scope.js"
 * @see <a href="https://github.com/jenkinsci/jenkins/blob/master/war/src/main/scss/abstracts/_theme.scss">Jenkins
 *         colors</a>
 */
public enum JenkinsPalette {
    BLACK(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY),
    BLUE,
    BROWN,
    CYAN,
    GREY("light-", "medium-", "dark-"),
    GREEN,
    INDIGO,
    ORANGE,
    PINK,
    PURPLE,
    RED,
    WHITE(StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY),
    YELLOW;

    static final List<JenkinsPalette> CHART_COLORS = List.of(RED, BLUE, YELLOW, GREEN, CYAN, INDIGO,
            ORANGE, PINK, PURPLE, BROWN);

    /**
     * Returns a chart color that can be used to render element {@code n} in a chart. If {@code n} is greater than
     * the number of available colors then the color will be selected from the beginning of the list,
     * and so on.
     *
     * @param n
     *         the n-th element to render
     *
     * @return a color to be used for rendering the n-th element
     */
    public static JenkinsPalette chartColor(final int n) {
        return CHART_COLORS.get(n % CHART_COLORS.size());
    }

    private final String infixLight;
    private final String infixNormal;
    private final String infixDark;

    JenkinsPalette() {
        this(LIGHT_INFIX, StringUtils.EMPTY, DARK_INFIX);
    }

    JenkinsPalette(final String infixLight, final String infixNormal, final String infixDark) {
        this.infixLight = infixLight;
        this.infixNormal = infixNormal;
        this.infixDark = infixDark;
    }

    private static final String PREFIX = "--";
    private static final String DARK_INFIX = "dark-";
    private static final String LIGHT_INFIX = "light-";

    /**
     * Returns the CSS variable name for this color (light variation).
     *
     * @return the CSS variable name
     */
    public String light() {
        return compose(infixLight);
    }

    /**
     * Returns the CSS variable name for this color.
     *
     * @return the CSS variable name
     */
    public String normal() {
        return compose(infixNormal);
    }

    /**
     * Returns the CSS variable name for this color (dark variation).
     *
     * @return the CSS variable name
     */
    public String dark() {
        return compose(infixDark);
    }

    private String compose(final String infix) {
        return PREFIX + infix + name().toLowerCase(Locale.ENGLISH);
    }
}
