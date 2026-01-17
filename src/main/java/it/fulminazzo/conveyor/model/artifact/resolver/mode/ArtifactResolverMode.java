package it.fulminazzo.conveyor.model.artifact.resolver.mode;

import it.fulminazzo.conveyor.manager.RepositoryManager;
import it.fulminazzo.conveyor.model.artifact.resolver.ArtifactResolver;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;

/**
 * Defines the operating mode of the resolver.
 */
public enum ArtifactResolverMode {
    /**
     * It will lookup the file on disk.
     * If present, it will be returned.
     * Otherwise, the download procedure will begin.
     * Subsequent requests will use the newly downloaded file.
     */
    DISK {
        @Override
        public @NotNull ArtifactResolver create(final @NotNull RepositoryManager repositoryManager,
                                                final @NotNull File workingDir,
                                                final @NotNull Logger logger) {
            return new DiskDownloaderArtifactResolver(repositoryManager, workingDir, logger);
        }
    },
    /**
     * It will lookup the file on disk.
     * If present, it will be verified with a checksum.
     * If the checksum cannot be verified,
     * the download source might specify the next course of action
     * (failure, ignoring or re-downloading the resource).
     */
    CHECKSUM {
        @Override
        public @NotNull ArtifactResolver create(final @NotNull RepositoryManager repositoryManager,
                                                final @NotNull File workingDir,
                                                final @NotNull Logger logger) {
            return new ChecksumDownloaderArtifactResolver(repositoryManager, workingDir, logger);
        }
    };

    /**
     * Creates a new Artifact resolver.
     *
     * @param repositoryManager the repository manager
     * @param workingDir        the working directory where the resolver should operate
     * @param logger            the logger
     * @return the artifact resolver
     */
    public abstract @NotNull ArtifactResolver create(final @NotNull RepositoryManager repositoryManager,
                                                     final @NotNull File workingDir,
                                                     final @NotNull Logger logger);

}
