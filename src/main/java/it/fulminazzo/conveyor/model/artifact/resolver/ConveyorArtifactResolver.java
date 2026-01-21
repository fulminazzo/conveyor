package it.fulminazzo.conveyor.model.artifact.resolver;

import it.fulminazzo.conveyor.manager.RepositoryManager;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.artifact.resolver.mode.ArtifactResolverMode;
import it.fulminazzo.conveyor.model.properties.Properties;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.io.File;

/**
 * Conveyor official {@link ArtifactResolver}.
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConveyorArtifactResolver implements ArtifactResolver {
    private final @NotNull Properties osMavenProperties = Properties.newOSMavenProperties();

    private final @NotNull RepositoryManager repositoryManager;
    private final @NotNull File workingDir;
    private final @NotNull Logger logger;

    private @Nullable ArtifactResolver delegate;

    @Override
    public @NotNull File resolve(final @NotNull Artifact artifact,
                                 final @NotNull String packaging) throws ArtifactResolverException {
        @NotNull ArtifactResolver delegate = getDelegate();
        try {
            return delegate.resolve(artifact, packaging);
        } catch (ArtifactResolverException e) {
            String classifier = artifact.getClassifier();
            if (classifier != null) {
                // check if the classifier is too specific, fallback to a broader one.
                String osClassifier = this.osMavenProperties.get("os.detected.classifier");
                if (osClassifier != null && classifier.contains(osClassifier))
                    try {
                        String newClassifier = classifier.substring(0, classifier.indexOf(osClassifier)) + osClassifier;
                        Artifact newArtifact = Artifact.builder()
                                .groupId(artifact.getGroupId())
                                .artifactId(artifact.getArtifactId())
                                .version(artifact.getVersion())
                                .classifier(newClassifier)
                                .build();
                        return delegate.resolve(newArtifact, packaging);
                    } catch (ArtifactResolverException ignored) {
                    }
            }
            throw e;
        }
    }

    /**
     * Sets the operating mode for this resolver.
     *
     * @param mode the mode
     * @return this resolver
     */
    public @NotNull ConveyorArtifactResolver setMode(final @NotNull ArtifactResolverMode mode) {
        this.delegate = mode.create(this.repositoryManager, this.workingDir, this.logger);
        return this;
    }

    /**
     * Gets the internal delegate.
     *
     * @return the delegate
     * @throws ArtifactResolverException if the delegate has not been initialized yet
     */
    @NotNull ArtifactResolver getDelegate() throws ArtifactResolverException {
        if (this.delegate == null)
            throw new ArtifactResolverException("No resolving mode has been set yet. Please use setMode before calling this method");
        return this.delegate;
    }

    /**
     * Instantiates a new Conveyor ArtifactResolver.
     *
     * @param repositoryManager the repository manager that will keep track of all the repositories
     * @param workingDir        the directory where the resolver should operate (for storing data)
     * @param logger            the logger
     * @return the conveyor artifact resolver
     */
    public static @NotNull ConveyorArtifactResolver newResolver(final @NotNull RepositoryManager repositoryManager,
                                                                final @NotNull File workingDir,
                                                                final @NotNull Logger logger) {
        return new ConveyorArtifactResolver(repositoryManager, workingDir, logger);
    }

}
