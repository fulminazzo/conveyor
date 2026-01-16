package it.fulminazzo.conveyor.model.pom.resolver

import groovy.util.logging.Slf4j
import groovyjarjarantlr4.v4.runtime.misc.Tuple3
import it.fulminazzo.conveyor.model.pom.resolver.engine.PomResolveEngineType
import it.fulminazzo.conveyor.model.repository.Repository
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class ConveyorPomResolverTest extends Specification {

    def 'test that addRepositories calls on both releases and snapshots resolvers'() {
        given:
        def tuple = createMock()

        and:
        def resolver = tuple.item1
        def releases = tuple.item2
        def snapshots = tuple.item3

        and:
        def repositories = [
                Repository.builder().id('first').url('https://first/').build(),
                Repository.builder().id('second').url('https://second/').build(),
                Repository.builder().id('third').url('https://third/').build()
        ]

        when:
        resolver.addRepositories(repositories)

        then:
        1 * releases.addRepositories(repositories)
        1 * snapshots.addRepositories(repositories)
    }

    def 'test that setMode calls on both releases and snapshots resolvers'() {
        given:
        def tuple = createMock()

        and:
        def resolver = tuple.item1
        def releases = tuple.item2
        def snapshots = tuple.item3

        and:
        def mode = PomResolveEngineType.MEMORY

        when:
        resolver.setMode(mode)

        then:
        1 * releases.setMode(mode)
        1 * snapshots.setMode(mode)
    }

    private Tuple3<RepositoryBasedPomResolver, RepositoryBasedPomResolver, RepositoryBasedPomResolver> createMock() {
        def first = ConveyorPomResolver.newResolver(TestUtils.BASE_DIR, log)

        def second = Mock(ReleasesRepositoryBasedPomResolver)
        def secondField = ConveyorPomResolver.getDeclaredField('releasesResolver')
        secondField.accessible = true
        secondField.set(first, second)

        def third = Mock(SnapshotsRepositoryBasedPomResolver)
        def thirdField = ConveyorPomResolver.getDeclaredField('snapshotsResolver')
        thirdField.accessible = true
        thirdField.set(first, third)

        return new Tuple3<>(first, second, third)
    }

}
