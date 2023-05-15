package io.jenkins.plugins.echarts;

import java.util.Locale;

/**
 * Jenkins color palette. Each color is represented as a CSS variable that will be rendered according to the
 * selected theme.
 *
 * @author Ullrich Hafner
 * @see "JS method resolveJenkinsColors in file echarts-scope.js"
 * @see <a href="https://github.com/jenkinsci/jenkins/blob/master/war/src/main/scss/abstracts/theme.scss">Jenkins colors</a>
 */
public enum JenkinsPalette {
    BLUE,
    BROWN,
    CYAN,
    GREEN,
    INDIGO,
    ORANGE,
    PINK,
    PURPLE,
    RED,
    YELLOW;

    private static final String PREFIX = "--";
    private static final String DARK_ID = "dark-";
    private static final String LIGHT_ID = "light-";

    /**
     * Returns the CSS variable name for this color (light variation).
     *
     * @return the CSS variable name
     */
    public String light() {
        return PREFIX + LIGHT_ID + cssName();
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
        return PREFIX + DARK_ID + cssName();
    }

    private String cssName() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
