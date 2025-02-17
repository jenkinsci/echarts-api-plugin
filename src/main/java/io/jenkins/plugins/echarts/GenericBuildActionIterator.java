package io.jenkins.plugins.echarts;

import edu.hm.hafner.echarts.Build;
import edu.hm.hafner.echarts.BuildResult;
import edu.umd.cs.findbugs.annotations.NonNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

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
    private final Function<A, R> function;

    GenericBuildActionIterator(final Class<A> actionType, final Optional<A> latestAction,
            final Predicate<A> predicate, final Function<A, R> function) {
        this.latestAction = latestAction;
        this.function = function;
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

        return new BuildResult<>(build, function.apply(buildAction));
    }

    /**
     * An iterable that provides an iterator for specific values of build that should be rendered in a trend chart. The
     * build values must be stored in a concrete subclass of {@link BuildAction}. The property that contains the build value
     * is selected by a generic predicate.
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
        private final Function<A, R> function;

        /**
         * Creates a new instance of {@link BuildActionIterable}.
         *
         * @param actionType
         *         the type of the action to select
         * @param latestAction
         *         the latest action that will be used as starting point for the sequence of results
         * @param filter
         *         filter that selects the action (if there are multiple actions of the same type)
         * @param function
         *         the supplier that extracts the specific results from the action
         */
        public BuildActionIterable(final Class<A> actionType, final A latestAction,
                final Predicate<A> filter, final Function<A, R> function) {
            this(actionType, Optional.of(latestAction), filter, function);
        }

        /**
         * Creates a new instance of {@link BuildActionIterable}.
         *
         * @param actionType
         *         the type of the action to select
         * @param latestAction
         *         the latest action that will be used as starting point for the sequence of results
         * @param filter
         *         filter that selects the action (if there are multiple actions of the same type)
         * @param function
         *         the supplier that extracts the specific results from the action
         */
        public BuildActionIterable(final Class<A> actionType, final Optional<A> latestAction,
                final Predicate<A> filter, final Function<A, R> function) {
            this.actionType = actionType;
            this.latestAction = latestAction;
            this.filter = filter;
            this.function = function;
        }

        @NonNull
        @Override
        public Iterator<BuildResult<R>> iterator() {
            return new GenericBuildActionIterator<>(actionType, latestAction, filter, function);
        }
    }
}
