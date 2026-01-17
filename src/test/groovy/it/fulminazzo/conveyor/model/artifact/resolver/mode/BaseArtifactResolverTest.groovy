package it.fulminazzo.conveyor.model.artifact.resolver.mode

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.downloader.DownloadException
import it.fulminazzo.conveyor.manager.RepositoryManager
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.artifact.resolver.ArtifactResolverException
import spock.lang.Specification

@Slf4j
class BaseArtifactResolverTest extends Specification {

    def 'test that resolve correctly reroutes requests to repositories'() {
        given:
        def repositoryManager = Mock(RepositoryManager)
        repositoryManager.releasesRepositories >> []
        repositoryManager.snapshotsRepositories >> []

        and:
        def resolver = new MockArtifactResolver(repositoryManager)

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
        def resolver = Spy(MockArtifactResolver, constructorArgs: [RepositoryManager.newManager(log)])

        and:
        resolver.resolve(_ as String, _ as Collection) >> {
            throw new DownloadException('Download exception')
        }

        when:
        resolver.resolve(new Artifact('it.fulminazzo', 'conveyor', '1.0-SNAPSHOT'), 'jar')

        then:
        thrown(ArtifactResolverException)
    }

}
