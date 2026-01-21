package it.fulminazzo.conveyor.model.artifact.resolver.mode;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.downloader.Downloader;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.artifact.resolver.ArtifactResolver;
import it.fulminazzo.conveyor.model.artifact.resolver.ArtifactResolverException;
import it.fulminazzo.conveyor.model.properties.Properties;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Collection;

/**
 * An implementation of {@link ArtifactResolver} that
 * uses a {@link Downloader} to resolve the artifact.
 */
@RequiredArgsConstructor
abstract class BaseArtifactResolver implements ArtifactResolver {
    private final @NotNull RepositoryManager repositoryManager;
    private final @NotNull Properties osMavenProperties = Properties.newOSMavenProperties();

    @Override
    public @NotNull File resolve(final @NotNull Artifact artifact,
                                 final @NotNull String packaging) throws ArtifactResolverException {
        boolean snapshots = artifact.getVersion().endsWith("-SNAPSHOT");
        Collection<DownloadSource> sources = snapshots ?
                this.repositoryManager.getSnapshotsRepositories() :
                this.repositoryManager.getReleasesRepositories();
        try {
            return resolve(artifact.getFullPath(packaging), sources);
        } catch (DownloadException e) {
            String classifier = artifact.getClassifier();
            if (classifier != null) {
                // check if the classifier is too specific, fallback to a broader one.
                String osClassifier = this.osMavenProperties.get("os.detected.classifier");
                if (osClassifier != null && classifier.contains(osClassifier)) {
                    String newClassifier = classifier.substring(0, classifier.indexOf(osClassifier)) + osClassifier;
                    Artifact newArtifact = Artifact.builder()
                            .groupId(artifact.getGroupId())
                            .artifactId(artifact.getArtifactId())
                            .version(artifact.getVersion())
                            .classifier(newClassifier)
                            .build();
                    try {
                        return resolve(newArtifact.getFullPath(packaging), sources);
                    } catch (DownloadException ignored) {
                    }
                }
            }
            throw new ArtifactResolverException(artifact, e);
        }
    }

    /**
     * Resolves the artifact.
     *
     * @param artifactPath    the artifact path
     * @param downloadSources the download sources
     * @return the artifact file
     * @throws DownloadException in case of download errors
     */
    protected abstract @NotNull File resolve(final @NotNull String artifactPath,
                                             final @NotNull Collection<DownloadSource> downloadSources
    ) throws DownloadException;

}
