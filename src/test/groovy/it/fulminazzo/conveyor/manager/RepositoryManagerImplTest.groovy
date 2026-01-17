package it.fulminazzo.conveyor.manager

import it.fulminazzo.conveyor.downloader.DownloadSource
import it.fulminazzo.conveyor.downloader.policy.ChecksumPolicies
import it.fulminazzo.conveyor.model.repository.ChecksumPolicy
import it.fulminazzo.conveyor.model.repository.Repository
import spock.lang.Specification

class RepositoryManagerImplTest extends Specification {

    def 'test that addRepositories correctly reroutes repositories'() {
        given:
        def repositories = [
                *ChecksumPolicy.values().collect {
                    Repository.builder()
                            .id("releases-$it")
                            .url("https://releases.$it")
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
                            .url("https://snapshots.$it")
                            .releases(Repository.Policy.builder().enabled(false).build())
                            .snapshots(Repository.Policy.builder()
                                    .enabled(true)
                                    .checksumPolicy(it)
                                    .build())
                            .build()
                }
        ]

        and:
        def manager = new RepositoryManagerImpl()

        when:
        manager.addRepositories(repositories)

        then:
        manager.releasesRepositories.sort() == ChecksumPolicies.values()
                .collect {
                    new DownloadSource("https://releases.$it")
                            .withCapability(it)
                }.sort()

        then:
        manager.snapshotsRepositories.sort() == ChecksumPolicies.values()
                .collect {
                    new DownloadSource("https://snapshots.$it")
                            .withCapability(it)
                }.sort()
    }

}
