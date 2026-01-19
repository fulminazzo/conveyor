package it.fulminazzo.conveyor.model.properties

import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.pom.Pom
import spock.lang.Specification

class PomPropertiesTest extends Specification {

    def 'test that PomProperties of #key returns #expected'() {
        given:
        def pom = Pom.builder()
                .project(new Artifact('it.fulminazzo', 'conveyor', '1.0'))
                .build()

        and:
        def properties = new PomProperties(pom)

        when:
        def actual = properties.get(key)

        then:
        actual == expected

        where:
        key                      || expected
        'groupId'                || 'it.fulminazzo'
        'project.groupId'        || 'it.fulminazzo'
        'pom.groupId'            || 'it.fulminazzo'
        'pom.project.groupId'    || null
        'project.pom.groupId'    || null
        'artifactId'             || 'conveyor'
        'project.artifactId'     || 'conveyor'
        'pom.artifactId'         || 'conveyor'
        'pom.project.artifactId' || null
        'project.pom.artifactId' || null
    }

}
