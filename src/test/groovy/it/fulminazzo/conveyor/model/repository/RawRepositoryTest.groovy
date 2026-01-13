package it.fulminazzo.conveyor.model.repository

import it.fulminazzo.conveyor.model.Properties
import it.fulminazzo.conveyor.model.repository.update.UpdatePolicy
import spock.lang.Specification

class RawRepositoryTest extends Specification {

    def 'test that applyProperties correctly parses all properties'() {
        given:
        def rawRepository = RawRepository.builder()
                .id('${id}')
                .url('${url}')
                .name('${name}')
                .releases(RawRepository.Policy.builder()
                        .enabled('${releases.enabled}')
                        .updatePolicy('${releases.update.policy}')
                        .checksumPolicy('${releases.checksum.policy}')
                        .build())
                .snapshots(RawRepository.Policy.builder()
                        .enabled('${snapshots.enabled}')
                        .updatePolicy('${snapshots.update.policy}')
                        .checksumPolicy('${snapshots.checksum.policy}')
                        .build())
                .build()

        and:
        def properties = new Properties([
                'id'                       : 'fulminazzo-repo',
                'url'                      : 'https://repo.fulminazzo.it/',
                'name'                     : 'Fulminazzo official repository',
                'releases.enabled'         : 'true',
                'releases.update.policy'   : 'never',
                'releases.checksum.policy' : 'fail',
                'snapshots.enabled'        : 'false',
                'snapshots.update.policy'  : 'interval:60',
                'snapshots.checksum.policy': 'warn',
        ])

        and:
        def expected = Repository.builder()
                .id('fulminazzo-repo')
                .url('https://repo.fulminazzo.it')
                .name('Fulminazzo official repository')
                .releases(Repository.Policy.builder()
                        .enabled(true)
                        .updatePolicy(UpdatePolicy.of('never'))
                        .checksumPolicy(ChecksumPolicy.FAIL)
                        .build())
                .snapshots(Repository.Policy.builder()
                        .enabled(false)
                        .updatePolicy(UpdatePolicy.of('interval:60'))
                        .checksumPolicy(ChecksumPolicy.WARN)
                        .build())
                .build()

        when:
        def actual = rawRepository.applyProperties(properties)

        then:
        actual == expected
    }

}
