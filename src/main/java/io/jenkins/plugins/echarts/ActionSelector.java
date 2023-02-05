package io.jenkins.plugins.echarts;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import hudson.model.Run;

import io.jenkins.plugins.util.BuildAction;

/**
 * Selects a specific action from the all actions that are attached to the given build. The action is selected by a
 * generic predicate that works on the expected concrete action type. If the baseline build does not contain the action
 * then previous builds will be inspected until the action is found.
 *
 * @param <T>
 *         the type of the action to select
 */
public class ActionSelector<T extends BuildAction<?>> implements Function<Run<?, ?>, Optional<T>> {
    private final Class<T> actionType;
    private final Predicate<? super T> predicate;

    /**
     * Creates a new instance of {@link ActionSelector}. This selector will select the first action of the given type
     * that matches.
     *
     * @param actionType
     *         the type of the action to select
     */
    public ActionSelector(final Class<T> actionType) {
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
    public ActionSelector(final Class<T> actionType, final Predicate<? super T> predicate) {
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

    /**
     * Searches in the build history for the first action that matches the specified predicate.
     *
     * @param baseline
     *         the baseline build to start the search from
     *
     * @return the action, if found
     */
    public final Optional<T> findFirst(final Run<?, ?> baseline) {
        return apply(baseline);
    }
}
