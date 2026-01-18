package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.artifact.Artifact
import spock.lang.Specification

class PomTest extends Specification {

    def 'test that getProperty of #key returns #expected'() {
        given:
        def pom = Pom.builder()
                .parent(new Artifact('it.fulminazzo', 'conveyor-parent', '1.0'))
                .project(new Artifact('it.fulminazzo', 'conveyor', '1.0'))
                .packaging('war')
                .name('conveyor')
                .description('A maven library...')
                .url('fulminazzo.it')
                .build()

        when:
        def value = pom.getProperty(key)

        then:
        value == expected

        where:
        key                 || expected
        'modelVersion'      || null //TODO:
        'parent.groupId'    || 'it.fulminazzo'
        'parent.artifactId' || 'conveyor-parent'
        'parent.version'    || '1.0'
        // 'parent.relativePath' || NOT SUPPORTED
        'groupId'           || 'it.fulminazzo'
        'artifactId'        || 'conveyor'
        'version'           || '1.0'
        'packaging'         || 'war'
        'name'              || 'conveyor'
        'description'       || 'A maven library...'
        'url'               || 'fulminazzo.it'
        'inceptionYear'     || null //TODO:
    }

}
