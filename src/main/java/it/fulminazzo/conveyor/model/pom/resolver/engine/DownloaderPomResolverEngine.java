package it.fulminazzo.conveyor.model.pom.resolver.engine;

import it.fulminazzo.conveyor.downloader.DownloadSource;
import it.fulminazzo.conveyor.downloader.Downloader;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.xml.XmlParser;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;
import java.util.Collection;

/**
 * A {@link Downloader} based {@link PomResolverEngine}.
 *
 * @param <D> the type of the used downloader
 */
@RequiredArgsConstructor
abstract class DownloaderPomResolverEngine<D extends Downloader> implements PomResolverEngine {
    protected final @NotNull D downloader;

    @Override
    public @NotNull Pom resolve(final @NotNull Artifact artifact) {
        final String artifactPath = artifact.getFullPath("pom");
        InputStream pomData = resolve(artifactPath);
        XmlParser parser = XmlParser.newParser(pomData);
        return Pom.builder(parser).build();
    }

    /**
     * Resolves the artifact pom data.
     *
     * @param artifactPath the artifact path
     * @return the raw data of the pom
     */
    protected abstract @NotNull InputStream resolve(final @NotNull String artifactPath);

    @Override
    public @NotNull PomResolverEngine addSources(final @NotNull Collection<DownloadSource> sources) {
        this.downloader.addDownloadSources(sources);
        return this;
    }

}
