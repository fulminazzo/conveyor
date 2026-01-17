package it.fulminazzo.conveyor.model.pom.new_resolver;

import it.fulminazzo.conveyor.downloader.DownloadException;
import it.fulminazzo.conveyor.downloader.Downloader;
import it.fulminazzo.conveyor.manager.RepositoryManager;
import it.fulminazzo.conveyor.model.BuilderException;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.xml.XmlParser;
import it.fulminazzo.conveyor.xml.XmlParserException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

/**
 * An abstract {@link PomResolver} that handles parsing and
 * building logic internally, exposing a {@link #resolve(String)} method
 * and using a {@link Downloader}.
 */
@RequiredArgsConstructor
abstract class DownloaderPomResolver implements PomResolver {
    protected final @NotNull RepositoryManager repositoryManager;
    protected final @NotNull Downloader downloader;

    @Override
    public @NotNull Pom resolve(final @NotNull Artifact artifact) throws PomResolverException {
        try {
            final String artifactPath = artifact.getFullPath("pom");
            InputStream pomData = resolve(artifactPath);
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
     * @param artifactPath the artifact path
     * @return the raw data of the pom
     * @throws DownloadException in case of download errors
     */
    protected abstract @NotNull InputStream resolve(final @NotNull String artifactPath) throws DownloadException;

}
