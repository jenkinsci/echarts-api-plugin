package io.jenkins.plugins.echarts.api.charts;

/**
 * Color palette for charts.
 *
 * @author Ullrich Hafner
 */
public enum Palette {
    YELLOW("#FFF59D", "#FFF9C4"),
    LIME("#E6EE9C", "#F0F4C3"),
    GREEN("#A5D6A7", "#C8E6C9"),
    BLUE("#90CAF9", "#90CAF9"),
    TEAL("#80CBC4", "#B2DFDB"),
    ORANGE("#FFCC80", "#FFE0B2"),
    INDIGO("#9FA8DA", "#C5CAE9"),
    PURPLE("#CE93D8", "#E1BEE7"),
    RED("#EF9A9A", "#FFCDD2"),
    BROWN("#BCAAA4", "#D7CCC8");

    private final String normal;
    private final String hover;

    Palette(final String normal, final String hover) {
        this.normal = normal;
        this.hover = hover;
    }

    /**
     * Returns the color at the specified index.
     *
     * @param index
     *         the index to use
     *
     * @return the color at the index
     */
    public static Palette color(final int index) {
        Palette[] colors = Palette.values();
        return colors[index % (colors.length)];
    }

    public String getNormal() {
        return normal;
    }

    public String getHover() {
        return hover;
    }
}
