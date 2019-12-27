package io.jenkins.plugins.echarts;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

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
    private final Class<T> actionType;

    private Optional<T> latestAction;

    /**
     * Creates a new iterator that selects action of the given type {@code actionType}.
     *
     * @param actionType
     *         the type of the actions to select
     * @param baseline
     *         the baseline to start from
     */
    public BuildActionIterator(final Class<T> actionType, final Optional<T> baseline) {
        this.actionType = actionType;
        this.latestAction = baseline;
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
        latestAction = BuildAction.getBuildActionFromHistoryStartingFrom(run.getPreviousBuild(), actionType);

        int buildTimeInSeconds = (int) (run.getTimeInMillis() / 1000);
        Build build = new Build(run.getNumber(), run.getDisplayName(), buildTimeInSeconds);
        return new BuildResult<>(build, buildAction);
    }
}
