package it.fulminiazzo.conveyor.artifact.pom.dependency;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents the exclusions of a {@link Dependency}.
 */
public final class Exclusions {
    private static final @NotNull String wildcard = "*";

    private final @NotNull Map<String, String> map = new ConcurrentHashMap<>();

    /**
     * Checks if the given coordinates are excluded from the exclusions list.
     *
     * @param groupId    the group id
     * @param artifactId the artifact id
     * @return true if they are (supports wildcard character)
     */
    public boolean isExcluded(final @NotNull String groupId,
                              final @NotNull String artifactId) {
        String stored = this.map.get(groupId);
        if (stored != null) {
            if (stored.equals(artifactId) || stored.equals(wildcard))
                return true;
        }
        stored = this.map.get(wildcard);
        return stored.equals(wildcard) || stored.equals(artifactId);
    }

    /**
     * Adds a new exclusion to the current list.
     *
     * @param groupId    the group id
     * @param artifactId the artifact id
     */
    public void add(final @NotNull String groupId,
                    final @NotNull String artifactId) {
        this.map.put(groupId, artifactId);
    }

}
