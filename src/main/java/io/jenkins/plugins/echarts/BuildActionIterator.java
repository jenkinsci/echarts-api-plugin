package io.jenkins.plugins.echarts;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.hm.hafner.echarts.Build;
import edu.hm.hafner.echarts.BuildResult;

import hudson.model.Run;

import io.jenkins.plugins.util.BuildAction;

/**
 * Iterates over a collection of builds that contain results of a given generic type. These results are available via a
 * given sub type of {@link BuildAction} that has to be attached to each build of the selected job. A new iterator
 * starts from a baseline build where it selects the attached action of the given type. Then it moves back in the build
 * history until no more builds are available.
 *
 * @param <T>
 *         type of the action that stores the result
 *
 * @author Ullrich Hafner
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class BuildActionIterator<T extends BuildAction<?>> implements Iterator<BuildResult<T>> {
    private Optional<T> latestAction;
    private final Function<Run<?, ?>, Optional<T>> actionSelector;

    /**
     * Creates a new iterator that selects the action of the given type {@code actionType}.
     *
     * @param actionType
     *         the type of the actions to select
     * @param baseline
     *         the baseline to start from
     */
    public BuildActionIterator(final Class<T> actionType, final Optional<T> baseline) {
        this(actionType, baseline, a -> true);
    }

    /**
     * Creates a new iterator that selects actions of the given type {@code actionType} and filters them using the
     * specified {@link Predicate}.
     *
     * @param actionType
     *         the type of the actions to select
     * @param baseline
     *         the baseline to start from
     * @param filter
     *         a predicate to filter the build actions of a build so that the selected action is unique
     */
    public BuildActionIterator(final Class<T> actionType, final Optional<T> baseline,
            final Predicate<? super T> filter) {
        latestAction = baseline;
        actionSelector = new ActionSelector<>(actionType, filter);
    }

    @Override
    public boolean hasNext() {
        return latestAction.isPresent();
    }

    @Override
    public BuildResult<T> next() {
        if (!latestAction.isPresent()) {
            throw new NoSuchElementException(
                    "There is no action available anymore. Use hasNext() before calling next().");
        }

        T buildAction = latestAction.get();
        Run<?, ?> run = buildAction.getOwner();
        latestAction = actionSelector.apply(run.getPreviousBuild());

        int buildTimeInSeconds = (int) (run.getTimeInMillis() / 1000);
        Build build = new Build(run.getNumber(), run.getDisplayName(), buildTimeInSeconds);
        return new BuildResult<>(build, buildAction);
    }

    private static class ActionSelector<T extends BuildAction<?>> implements Function<Run<?, ?>, Optional<T>> {
        private final Class<T> actionType;
        private final Predicate<? super T> predicate;

        ActionSelector(final Class<T> actionType, final Predicate<? super T> predicate) {
            this.actionType = actionType;
            this.predicate = predicate;
        }

        @Override
        public Optional<T> apply(final Run<?, ?> baseline) {
            for (Run<?, ?> run = baseline; run != null; run = run.getPreviousBuild()) {
                Optional<T> action = run.getActions(actionType)
                        .stream()
                        .filter(predicate)
                        .findAny();
                if (action.isPresent()) {
                    return action;
                }
            }

            return Optional.empty();
        }
    }
}
