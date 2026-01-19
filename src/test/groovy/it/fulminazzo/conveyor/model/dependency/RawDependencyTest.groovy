package it.fulminazzo.conveyor.model.dependency

import it.fulminazzo.conveyor.model.Properties
import spock.lang.Specification

class RawDependencyTest extends Specification {

    def 'test that applyProperties correctly parses all properties'() {
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
        rawDependency.exclusionsManager
                .add('${first.excluded.groupId}', '${first.excluded.artifactId}')
                .add('${second.excluded.groupId}', '${second.excluded.artifactId}')
                .add('${third.excluded.groupId}', '${third.excluded.artifactId}')

        and:
        def properties = new Properties([
                'groupId'                   : 'it.fulminazzo',
                'artifactId'                : 'conveyor',
                'classifier'                : 'sources',
                'version'                   : '1.0',
                'type'                      : 'war',
                'scope'                     : 'test',
                'optional'                  : 'true',
                'first.excluded.groupId'    : 'org.projectlombok',
                'first.excluded.artifactId' : 'lombok',
                'second.excluded.groupId'   : 'org.jetbrains',
                'second.excluded.artifactId': 'annotations',
                'third.excluded.groupId'    : 'org.spockframework',
                'third.excluded.artifactId' : 'spock-core',
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
                .exclusionsManager(new ExclusionsManager()
                        .add('org.projectlombok', 'lombok')
                        .add('org.jetbrains', 'annotations')
                        .add('org.spockframework', 'spock-core')
                )
                .build()

        when:
        def actual = rawDependency.applyProperties(properties)

        then:
        actual == expected
    }

}
