package it.fulminazzo.conveyor.model.repository;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a general repository interface.
 */
public interface RepositoryLike {

    /**
     * Gets id.
     *
     * @return the id
     */
    @NotNull String getId();

    /**
     * Gets url.
     *
     * @return the url
     */
    @NotNull String getUrl();

    /**
     * Gets name.
     *
     * @return the name
     */
    @Nullable String getName();

}
