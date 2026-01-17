package it.fulminazzo.conveyor.model.pom.resolver

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.downloader.DownloadSource
import it.fulminazzo.conveyor.downloader.policy.ChecksumPolicies
import it.fulminazzo.conveyor.model.pom.resolver.engine.PomResolveEngineType
import it.fulminazzo.conveyor.model.repository.ChecksumPolicy
import it.fulminazzo.conveyor.model.repository.Repository
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class ReleasesRepositoryBasedPomResolverTest extends Specification {

    def 'test that releases resolver only adds releases repositories'() {
        given:
        def repositories = [
                *ChecksumPolicy.values().collect {
                    Repository.builder()
                            .id("releases-$it")
                            .url("https://releases-$it")
                            .releases(Repository.Policy.builder()
                                    .enabled(true)
                                    .checksumPolicy(it)
                                    .build())
                            .snapshots(Repository.Policy.builder().enabled(false).build())
                            .build()
                },
                *ChecksumPolicy.values().collect {
                    Repository.builder()
                            .id("snapshots-$it")
                            .url("https://snapshots-$it")
                            .releases(Repository.Policy.builder().enabled(false).build())
                            .snapshots(Repository.Policy.builder()
                                    .enabled(true)
                                    .checksumPolicy(it)
                                    .build())
                            .build()
                }
        ]

        and:
        def expected = ChecksumPolicy.values().collect {
            new DownloadSource("https://releases-$it")
                    .withCapability(ChecksumPolicies.valueOf(it.name()))
        }

        and:
        def builder = new ReleasesRepositoryBasedPomResolver(TestUtils.BASE_DIR, log)
                .setMode(PomResolveEngineType.MEMORY)

        when:
        builder.addRepositories(repositories)

        then:
        builder.engine.downloader.downloadSources.sort() == expected.sort()
    }

}
