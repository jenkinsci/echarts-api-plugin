package io.jenkins.plugins.echarts;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sf.json.JSONArray;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.*;

/**
 * Tests the class {@link PieData}.
 *
 * @author Ullrich Hafner
 */
class PieDataTest {
    @Test
    void shouldConvertListOfPointsToJson() {
        List<PieData> models = new ArrayList<>();
        PieData first = new PieData("ONE", 1);
        PieData second = new PieData("TWO", 2);
        models.add(first);
        models.add(second);

        JSONArray array = JSONArray.fromObject(models);

        assertThatJson(array).isArray().hasSize(2)
                .contains(first)
                .contains(second);
    }
}
