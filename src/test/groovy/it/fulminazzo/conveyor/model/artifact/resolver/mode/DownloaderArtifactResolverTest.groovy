package it.fulminazzo.conveyor.model.artifact.resolver.mode

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.downloader.DownloadException
import it.fulminazzo.conveyor.downloader.Downloader
import it.fulminazzo.conveyor.manager.RepositoryManager
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.artifact.resolver.ArtifactResolverException
import spock.lang.Specification

@Slf4j
class DownloaderArtifactResolverTest extends Specification {

    def 'test that resolve correctly reroutes requests to repositories'() {
        given:
        def repositoryManager = Mock(RepositoryManager)
        repositoryManager.releasesRepositories >> []
        repositoryManager.snapshotsRepositories >> []

        and:
        def resolver = new DownloaderArtifactResolver(repositoryManager, Mock(Downloader))

        when:
        resolver.resolve(new Artifact('it.fulminazzo', 'conveyor', '1.0'), 'jar')

        then:
        1 * repositoryManager.getReleasesRepositories()
        0 * repositoryManager.getSnapshotsRepositories()

        when:
        resolver.resolve(new Artifact('it.fulminazzo', 'conveyor', '1.0-SNAPSHOT'), 'jar')

        then:
        0 * repositoryManager.getReleasesRepositories()
        1 * repositoryManager.getSnapshotsRepositories()
    }

    def 'test that resolve throws DownloadException as ArtifactResolverException'() {
        given:
        def downloader = Mock(Downloader)
        downloader.resolveToFile(_, _) >> {
            throw new DownloadException('Download exception')
        }

        and:
        def resolver = Spy(DownloaderArtifactResolver, constructorArgs: [RepositoryManager.newManager(log), downloader])

        when:
        resolver.resolve(new Artifact('it.fulminazzo', 'conveyor', '1.0-SNAPSHOT'), 'jar')

        then:
        thrown(ArtifactResolverException)
    }

}
