package it.fulminazzo.conveyor.model.dependency;

import it.fulminazzo.conveyor.model.Properties;
import it.fulminazzo.conveyor.model.RawObject;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Keeps track of all the exclusions of a dependency.
 */
@ToString(includeFieldNames = false)
@EqualsAndHashCode
public final class ExclusionsManager implements RawObject<ExclusionsManager> {
    private static final @NotNull String wildcard = "*";

    private final @NotNull Set<ExclusionNode> exclusions = new HashSet<>();

    @Override
    public @NotNull ExclusionsManager applyProperties(final @NotNull Properties properties) {
        ExclusionsManager exclusionsManager = new ExclusionsManager();
        for (ExclusionNode exclusion : this.exclusions)
            exclusionsManager.exclusions.add(new ExclusionNode(
                    properties.apply(exclusion.groupId()),
                    properties.apply(exclusion.artifactId())
            ));
        return exclusionsManager;
    }

    /**
     * Checks if the given coordinates are excluded from the exclusions list.
     *
     * @param groupId    the group id
     * @param artifactId the artifact id
     * @return true if they are (supports wildcard character)
     */
    public boolean isExcluded(final @NotNull String groupId,
                              final @NotNull String artifactId) {
        if (this.exclusions.contains(new ExclusionNode(groupId, artifactId))) return true;
        else if (this.exclusions.contains(new ExclusionNode(groupId, wildcard))) return true;
        else if (this.exclusions.contains(new ExclusionNode(wildcard, artifactId))) return true;
        else return this.exclusions.contains(new ExclusionNode(wildcard, wildcard));
    }

    /**
     * Adds a new exclusion to the current list.
     *
     * @param groupId    the group id
     * @param artifactId the artifact id
     * @return this object for method chaining
     */
    public @NotNull ExclusionsManager add(final @NotNull String groupId,
                                          final @NotNull String artifactId) {
        this.exclusions.add(new ExclusionNode(groupId, artifactId));
        return this;
    }

    /**
     * Adds all the exclusions to the current list.
     *
     * @param exclusionsManager the exclusions
     * @return this object for method chaining
     */
    public @NotNull ExclusionsManager addAll(final @NotNull ExclusionsManager exclusionsManager) {
        this.exclusions.addAll(exclusionsManager.exclusions);
        return this;
    }

    private record ExclusionNode(String groupId, String artifactId) {
    }

}
