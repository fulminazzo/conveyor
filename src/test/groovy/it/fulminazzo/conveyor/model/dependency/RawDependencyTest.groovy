package it.fulminazzo.conveyor.model.dependency

import it.fulminazzo.conveyor.model.Properties
import spock.lang.Specification

class RawDependencyTest extends Specification {

    def 'test that parseProperties correctly parses all properties'() {
        given:
        def rawDependency = RawDependency.builder()
                .groupId('${groupId}')
                .artifactId('${artifactId}')
                .classifier('${classifier}')
                .version('${version}')
                .type('${type}')
                .scope('${scope}')
                .optional('${optional}')
                .build()

        and:
        def properties = new Properties([
                'groupId'   : 'it.fulminazzo',
                'artifactId': 'conveyor',
                'classifier': 'sources',
                'version'   : '1.0',
                'type'      : 'war',
                'scope'     : 'test',
                'optional'  : 'true',
        ])

        and:
        def expected = Dependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('conveyor')
                .classifier('sources')
                .version('1.0')
                .type('war')
                .scope(Scope.TEST)
                .optional(true)
                .exclusions(new Exclusions())
                .build()

        when:
        def actual = rawDependency.parseProperties(properties)

        then:
        actual == expected
    }

}
