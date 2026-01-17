package it.fulminazzo.conveyor.model.pom.resolver

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.model.pom.resolver.engine.PomResolveEngineType
import it.fulminazzo.conveyor.model.repository.Repository
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class BaseRepositoryBasedPomResolverTest extends Specification {

    def 'test that setMode transfers download sources'() {
        given:
        def builder = new MockRepositoryBasedPomResolver(TestUtils.BASE_DIR, log)

        and:
        builder.setMode(PomResolveEngineType.MEMORY)
                .addRepositories([
                        Repository.builder().id('first').url('https://first/').build(),
                        Repository.builder().id('second').url('https://second/').build(),
                        Repository.builder().id('third').url('https://third/').build(),
                ])

        and:
        def firstSources = builder.engine.downloader.downloadSources

        when:
        builder.setMode(PomResolveEngineType.DISK)

        and:
        def secondSources = builder.engine.downloader.downloadSources

        then:
        firstSources == secondSources
    }

    def 'test that getEngine throws if not initialized'() {
        given:
        def builder = new MockRepositoryBasedPomResolver(TestUtils.BASE_DIR, log)

        when:
        builder.engine

        then:
        thrown(IllegalStateException)
    }

}
