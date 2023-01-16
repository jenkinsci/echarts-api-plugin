package io.jenkins.plugins.echarts;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import edu.hm.hafner.echarts.Build;
import edu.hm.hafner.echarts.BuildResult;

import hudson.model.Run;

import io.jenkins.plugins.util.BuildAction;

/**
 * Iterates over a collection of builds that contain results of a given generic type. These results are available via a
 * given subtype of {@link BuildAction} that has to be attached to each build of the selected job. A new iterator starts
 * from a baseline build where it selects the attached action of the given type. Then it moves back in the build history
 * until no more builds are available.
 *
 * @param <A>
 *         the type of the action
 * @param <R>
 *         the type of the result
 *
 * @author Ullrich Hafner
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public class GenericBuildActionIterator<A extends BuildAction<?>, R> implements Iterator<BuildResult<R>> {
    private final ActionSelector<A> actionSelector;
    private Optional<A> latestAction;
    private final Supplier<R> supplier;

    GenericBuildActionIterator(final Class<A> actionType, final Optional<A> latestAction,
            final Predicate<A> predicate, final Supplier<R> supplier) {
        this.latestAction = latestAction;
        this.supplier = supplier;
        actionSelector = new ActionSelector<>(actionType, predicate);
    }

    @Override
    public boolean hasNext() {
        return latestAction.isPresent();
    }

    @Override
    public BuildResult<R> next() {
        if (latestAction.isEmpty()) {
            throw new NoSuchElementException(
                    "There is no action available anymore. Use hasNext() before calling next().");
        }

        A buildAction = latestAction.get();
        Run<?, ?> run = buildAction.getOwner();
        latestAction = actionSelector.apply(run.getPreviousBuild());

        int buildTimeInSeconds = (int) (run.getTimeInMillis() / 1000);
        Build build = new Build(run.getNumber(), run.getDisplayName(), buildTimeInSeconds);

        return new BuildResult<>(build, supplier.get());
    }

    /**
     * Selects a specific action from the all actions that are attached to the given build. The action is selected by a
     * generic predicate that works on the expected concrete action type. If the baseline build does not contain the
     * action then previous builds will be inspected until the action is found.
     *
     * @param <T>
     *         the type of the action to select
     */
    private static class ActionSelector<T extends BuildAction<?>> implements Function<Run<?, ?>, Optional<T>> {
        private final Class<T> actionType;
        private final Predicate<? super T> predicate;

        /**
         * Creates a new instance of {@link ActionSelector}. This selector will select the first action of the given
         * type that matches.
         *
         * @param actionType
         *         the type of the action to select
         */
        ActionSelector(final Class<T> actionType) {
            this(actionType, action -> true);
        }

        /**
         * Creates a new instance of {@link ActionSelector}.
         *
         * @param actionType
         *         the type of the action to select
         * @param predicate
         *         the predicate that selects the action (if there are multiple actions of the same type)
         */
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

    /**
     * An iterable that provides an iterator for build results of a specific type.
     *
     * @param <A>
     *         the type of the action
     * @param <R>
     *         the type of the result
     */
    public static class BuildActionIterable<A extends BuildAction<?>, R> implements Iterable<BuildResult<R>> {
        private final Class<A> actionType;
        private final Optional<A> latestAction;
        private final Predicate<A> filter;
        private final Supplier<R> supplier;

        /**
         * Creates a new instance of {@link BuildActionIterable}.
         *
         * @param actionType
         *         the type of the action to select
         * @param latestAction
         *         the latest action that will be used as starting point for the sequence of results
         * @param filter
         *         filter that selects the action (if there are multiple actions of the same type)
         * @param supplier
         *         the supplier that extracts the specific results from the action
         */
        public BuildActionIterable(final Class<A> actionType, final Optional<A> latestAction,
                final Predicate<A> filter, final Supplier<R> supplier) {
            this.actionType = actionType;
            this.latestAction = latestAction;
            this.filter = filter;
            this.supplier = supplier;
        }

        @Override
        public Iterator<BuildResult<R>> iterator() {
            return new GenericBuildActionIterator<>(actionType, latestAction, filter, supplier);
        }
    }
}
