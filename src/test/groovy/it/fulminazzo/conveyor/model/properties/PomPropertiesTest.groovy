package it.fulminazzo.conveyor.model.properties

import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.pom.Pom
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

class PomPropertiesTest extends Specification {

    def 'test that PomProperties of #key returns #expected'() {
        given:
        def pom = Pom.builder()
                .project(new Artifact('it.fulminazzo', 'conveyor', '1.0'))
                .build()

        and:
        def properties = new PomProperties(pom, TestUtils.BASE_DIR)

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
        'basedir'                || new File(TestUtils.BASE_DIR, 'it/fulminazzo/conveyor/1.0/').absolutePath
        'pom.basedir'            || new File(TestUtils.BASE_DIR, 'it/fulminazzo/conveyor/1.0/').absolutePath
        'project.basedir'        || new File(TestUtils.BASE_DIR, 'it/fulminazzo/conveyor/1.0/').absolutePath
        'baseUri'                || new File(TestUtils.BASE_DIR, 'it/fulminazzo/conveyor/1.0/').toURI().toString()
        'pom.baseUri'            || new File(TestUtils.BASE_DIR, 'it/fulminazzo/conveyor/1.0/').toURI().toString()
        'project.baseUri'        || new File(TestUtils.BASE_DIR, 'it/fulminazzo/conveyor/1.0/').toURI().toString()
    }

}
