package it.fulminazzo.conveyor.model.dependency;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents the exclusions of a {@link Dependency}.
 */
@ToString(includeFieldNames = false)
@EqualsAndHashCode
public final class Exclusions {
    private static final @NotNull String wildcard = "*";
    private static final @NotNull String separator = ":";

    private final @NotNull Set<String> exclusions = new HashSet<>();

    /**
     * Checks if the given coordinates are excluded from the exclusions list.
     *
     * @param groupId    the group id
     * @param artifactId the artifact id
     * @return true if they are (supports wildcard character)
     */
    public boolean isExcluded(final @NotNull String groupId,
                              final @NotNull String artifactId) {
        if (this.exclusions.contains(getIdentifier(groupId, artifactId))) return true;
        else if (this.exclusions.contains(getIdentifier(groupId, wildcard))) return true;
        else if (this.exclusions.contains(getIdentifier(wildcard, artifactId))) return true;
        else return this.exclusions.contains(getIdentifier(wildcard, wildcard));
    }

    /**
     * Adds a new exclusion to the current list.
     *
     * @param groupId    the group id
     * @param artifactId the artifact id
     * @return this object for method chaining
     */
    public @NotNull Exclusions add(final @NotNull String groupId,
                                   final @NotNull String artifactId) {
        this.exclusions.add(getIdentifier(groupId, artifactId));
        return this;
    }

    private @NotNull String getIdentifier(final @NotNull String groupId, final @NotNull String artifactId) {
        return groupId + separator + artifactId;
    }

}
