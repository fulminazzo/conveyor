package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.pom.metadata.License
import it.fulminazzo.conveyor.model.pom.metadata.Organization
import it.fulminazzo.conveyor.model.pom.metadata.PomMetadata
import spock.lang.Specification

class PomTest extends Specification {

    def 'test that getProperty of #key returns #expected'() {
        given:
        def pom = Pom.builder()
                .parent(new Artifact('it.fulminazzo', 'conveyor-parent', '1.0'))
                .project(new Artifact('it.fulminazzo', 'conveyor', '1.0'))
                .packaging('war')
                .metadata(PomMetadata.builder()
                        .modelVersion('4.0.0')
                        .name('conveyor')
                        .description('A maven library...')
                        .url('fulminazzo.it')
                        .inceptionYear('2024')
                        .organization(Organization.builder()
                                .name('fulminazzo')
                                .url('fulminazzo.it')
                                .build())
                        .licenses([
                                License.builder()
                                        .name('license1')
                                        .url('license1.com')
                                        .distribution('distribution1')
                                        .comments('comments1')
                                        .build(),
                                License.builder()
                                        .name('license2')
                                        .url('license2.com')
                                        .distribution('distribution2')
                                        .comments('comments2')
                                        .build()
                        ].toSet())
                        .build())
                .build()

        when:
        def value = pom.getProperty(key)

        then:
        value == expected

        where:
        key                        || expected
        'modelVersion'             || '4.0.0'
        'parent.groupId'           || 'it.fulminazzo'
        'parent.artifactId'        || 'conveyor-parent'
        'parent.version'           || '1.0'
        // 'parent.relativePath' || NOT SUPPORTED
        'groupId'                  || 'it.fulminazzo'
        'artifactId'               || 'conveyor'
        'version'                  || '1.0'
        'packaging'                || 'war'
        'name'                     || 'conveyor'
        'description'              || 'A maven library...'
        'url'                      || 'fulminazzo.it'
        'inceptionYear'            || '2024'
        'organization.name'        || 'fulminazzo'
        'organization.url'         || 'fulminazzo.it'
        'licenses[0].name'         || 'license1'
        'licenses[0].url'          || 'license1.com'
        'licenses[0].distribution' || 'distribution1'
        'licenses[0].comments'     || 'comments1'
        'licenses[1].name'         || 'license2'
        'licenses[1].url'          || 'license2.com'
        'licenses[1].distribution' || 'distribution2'
        'licenses[1].comments'     || 'comments2'
    }

}
