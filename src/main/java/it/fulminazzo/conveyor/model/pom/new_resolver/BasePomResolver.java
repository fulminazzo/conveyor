package it.fulminazzo.conveyor.model.pom.new_resolver;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.xml.XmlParser;
import it.fulminazzo.conveyor.xml.XmlParserException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Collection;

/**
 * An abstract {@link PomResolver} that handles parsing and
 * building logic internally, exposing a {@link #resolve(String, Collection)} method.
 */
@RequiredArgsConstructor
abstract class BasePomResolver implements PomResolver {
    private final @NotNull RepositoryManager repositoryManager;

    @Override
    public @NotNull Pom resolve(final @NotNull Artifact artifact) throws PomResolverException {
        try {
            final String artifactPath = artifact.getFullPath("pom");
            boolean snapshots = artifact.getVersion().endsWith("-SNAPSHOT");
            InputStream pomData = resolve(artifactPath, snapshots ?
                    this.repositoryManager.getSnapshotsRepositories() :
                    this.repositoryManager.getReleasesRepositories()
            );
            XmlParser parser = XmlParser.newParser(pomData);
            return Pom.builder(parser).build();
        } catch (XmlParserException e) {
            throw new PomResolverException(artifact, e);
        } catch (BuilderException e) {
            throw new PomResolverException(artifact, e);
        } catch (DownloadException e) {
            throw new PomResolverException(artifact, e);
        }
    }

    /**
     * Resolves the artifact pom data.
     *
     * @param artifactPath    the artifact path
     * @param downloadSources the download sources
     * @return the raw data of the pom
     * @throws DownloadException in case of download errors
     */
    protected abstract @NotNull InputStream resolve(final @NotNull String artifactPath,
                                                    final @NotNull Collection<DownloadSource> downloadSources) throws DownloadException;

}
