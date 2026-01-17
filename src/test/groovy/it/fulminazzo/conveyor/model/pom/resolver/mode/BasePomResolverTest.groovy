package it.fulminazzo.conveyor.model.pom.resolver.mode

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.downloader.DownloadException
import it.fulminazzo.conveyor.manager.RepositoryManager
import it.fulminazzo.conveyor.model.BuilderException
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.pom.resolver.PomResolverException
import it.fulminazzo.conveyor.xml.XmlParserException
import spock.lang.Specification

@Slf4j
class BasePomResolverTest extends Specification {

    def 'test that resolve correctly reroutes requests to repositories'() {
        given:
        def repositoryManager = Mock(RepositoryManager)
        repositoryManager.releasesRepositories >> []
        repositoryManager.snapshotsRepositories >> []

        and:
        def resolver = new MockPomResolver(repositoryManager)

        when:
        resolver.resolve(new Artifact('it.fulminazzo', 'conveyor', '1.0'))

        then:
        1 * repositoryManager.getReleasesRepositories()
        0 * repositoryManager.getSnapshotsRepositories()

        when:
        resolver.resolve(new Artifact('it.fulminazzo', 'conveyor', '1.0-SNAPSHOT'))

        then:
        0 * repositoryManager.getReleasesRepositories()
        1 * repositoryManager.getSnapshotsRepositories()
    }

    def 'test that resolve throws #exception as PomResolverException'() {
        given:
        def resolver = Spy(MockPomResolver, constructorArgs: [RepositoryManager.newManager(log)])

        and:
        resolver.resolve(_ as String, _ as Collection) >> {
            throw exception
        }

        when:
        resolver.resolve(new Artifact('it.fulminazzo', 'conveyor', '1.0'))

        then:
        thrown(PomResolverException)

        where:
        exception << [
                new DownloadException('Download exception'),
                new XmlParserException('XmlParser exception'),
                new BuilderException('Builder exception')
        ]
    }

}
