package io.jenkins.plugins.echarts;

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

        when(previousBuild.getAction(TestAction.class)).thenReturn(previousAction);
        when(baseline.getPreviousBuild()).thenAnswer(i -> previousBuild);

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, testAction));

        assertThat(iterator).hasNext();
        assertThat(iterator.next()).usingRecursiveComparison().ignoringFields("build")
                .isEqualTo(new BuildResult<>(null, previousAction));
    }

    private abstract static class TestAction extends BuildAction<String> {
        private static final long serialVersionUID = -8129555082892128959L;

        TestAction(final Run<?, ?> owner, final String result) {
            super(owner, result);
        }
    }
}
