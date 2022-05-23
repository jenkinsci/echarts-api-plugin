package io.jenkins.plugins.echarts;

import java.util.Arrays;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.echarts.BuildResult;

import hudson.model.Run;

import io.jenkins.plugins.util.BuildAction;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests the class {@link BuildActionIterator}.
 *
 * @author Ullrich Hafner
 */
class BuildActionIteratorTest {
    @Test
    void shouldCreateEmptyIterator() {
        BuildActionIterator<TestAction> iterator = new BuildActionIterator<>(TestAction.class, Optional.empty());

        assertThat(iterator).isExhausted();
        assertThatExceptionOfType(NoSuchElementException.class).isThrownBy(iterator::next);
    }

    @Test
    void shouldReturnResultOfCurrentBuild() {
        TestAction testAction = mock(TestAction.class);
        Run<?, ?> baseline = mock(Run.class);
        when(testAction.getOwner()).thenAnswer(i -> baseline);

        BuildActionIterator<TestAction> iterator = new BuildActionIterator<>(TestAction.class, Optional.of(testAction));

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, testAction));
        assertThat(iterator).isExhausted();
    }

    @Test
    void shouldReturnResultOfPreviousBuild() {
        TestAction testAction = mock(TestAction.class);
        Run<?, ?> baseline = mock(Run.class);
        when(testAction.getOwner()).thenAnswer(i -> baseline);

        BuildActionIterator<TestAction> iterator = new BuildActionIterator<>(TestAction.class, Optional.of(testAction));

        TestAction previousAction = mock(TestAction.class);
        Run<?, ?> previousBuild = mock(Run.class);
        when(previousAction.getOwner()).thenAnswer(i -> previousBuild);

        when(previousBuild.getActions(TestAction.class)).thenReturn(Collections.singletonList(previousAction));
        when(baseline.getPreviousBuild()).thenAnswer(i -> previousBuild);

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, testAction));

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, previousAction));
        assertThat(iterator).isExhausted();
    }

    @Test
    void shouldSkipBuildWithNoResult() {
        TestAction testAction = mock(TestAction.class);
        Run<?, ?> baseline = mock(Run.class);
        when(testAction.getOwner()).thenAnswer(i -> baseline);

        BuildActionIterator<TestAction> iterator = new BuildActionIterator<>(TestAction.class, Optional.of(testAction));

        Run<?, ?> skippedBuild = mock(Run.class);
        when(skippedBuild.getActions(TestAction.class)).thenReturn(Collections.emptyList());
        when(baseline.getPreviousBuild()).thenAnswer(i -> skippedBuild);

        TestAction previousAction = mock(TestAction.class);
        Run<?, ?> previousBuild = mock(Run.class);
        when(previousAction.getOwner()).thenAnswer(i -> previousBuild);

        when(previousBuild.getActions(TestAction.class)).thenReturn(Collections.singletonList(previousAction));
        when(skippedBuild.getPreviousBuild()).thenAnswer(i -> previousBuild);

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, testAction));

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, previousAction));
        assertThat(iterator).isExhausted();
    }

    @Test
    void shouldSkipBuildInThemMiddle() {
        TestAction testAction = mock(TestAction.class);
        Run<?, ?> baseline = mock(Run.class);
        when(testAction.getOwner()).thenAnswer(i -> baseline);

        TestAction middleAction = mock(TestAction.class);
        Run<?, ?> middleBuild = mock(Run.class);
        when(middleAction.getOwner()).thenAnswer(i -> middleBuild);

        when(middleBuild.getActions(TestAction.class)).thenReturn(Collections.singletonList(middleAction));
        when(baseline.getPreviousBuild()).thenAnswer(i -> middleBuild);

        Run<?, ?> skippedBuild = mock(Run.class);
        when(skippedBuild.getActions(TestAction.class)).thenReturn(Collections.emptyList());
        when(middleBuild.getPreviousBuild()).thenAnswer(i -> skippedBuild);

        TestAction previousAction = mock(TestAction.class);
        Run<?, ?> previousBuild = mock(Run.class);
        when(previousAction.getOwner()).thenAnswer(i -> previousBuild);

        when(previousBuild.getActions(TestAction.class)).thenReturn(Collections.singletonList(previousAction));
        when(skippedBuild.getPreviousBuild()).thenAnswer(i -> previousBuild);

        BuildActionIterator<TestAction> iterator = new BuildActionIterator<>(TestAction.class, Optional.of(testAction));

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, testAction));

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, middleAction));

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, previousAction));

        assertThat(iterator).isExhausted();
    }

    @Test
    void shouldReturnResultOfPreviousBuildWithFilter() {
        TestAction first = mock(TestAction.class);
        TestAction second = mock(TestAction.class);

        when(first.getUrlName()).thenReturn("match!");
        when(second.getUrlName()).thenReturn("no match");

        shouldSelectResultByFilter(first, second, first);

        when(first.getUrlName()).thenReturn("no match");
        when(second.getUrlName()).thenReturn("match!");

        shouldSelectResultByFilter(first, second, second);
    }

    private void shouldSelectResultByFilter(final TestAction first, final TestAction second,
            final TestAction expectedResult) {
        TestAction testAction = mock(TestAction.class);
        Run<?, ?> baseline = mock(Run.class);
        when(testAction.getOwner()).thenAnswer(i -> baseline);

        BuildActionIterator<TestAction> iterator = new BuildActionIterator<>(
                TestAction.class, Optional.of(testAction), b -> "match!".equals(b.getUrlName()));

        Run<?, ?> previousBuild = mock(Run.class);
        when(first.getOwner()).thenAnswer(i -> previousBuild);
        when(second.getOwner()).thenAnswer(i -> previousBuild);

        when(previousBuild.getActions(TestAction.class)).thenReturn(Arrays.asList(first, second));
        when(baseline.getPreviousBuild()).thenAnswer(i -> previousBuild);

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, testAction));

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, expectedResult));
        assertThat(iterator).isExhausted();
    }

    private abstract static class TestAction extends BuildAction<String> {
        private static final long serialVersionUID = -8129555082892128959L;

        TestAction(final Run<?, ?> owner, final String result) {
            super(owner, result);
        }
    }
}
