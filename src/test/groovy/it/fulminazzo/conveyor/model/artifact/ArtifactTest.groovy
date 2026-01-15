package it.fulminazzo.conveyor.model.artifact

import spock.lang.Specification

class ArtifactTest extends Specification {

    def 'test that getFullPath of #extension and #classifier returns #expected'() {
        given:
        def artifact = Artifact.builder()
                .groupId('it.fulminazzo')
                .artifactId('conveyor')
                .classifier(classifier)
                .version('1.0')
                .build()

        when:
        def actual = artifact.getFullPath(extension)

        then:
        actual == expected

        where:
        extension | classifier || expected
        'pom'     | null       || 'it/fulminazzo/conveyor/1.0/conveyor-1.0.pom'
        'pom'     | 'sources'  || 'it/fulminazzo/conveyor/1.0/conveyor-1.0-sources.pom'
        'jar'     | null       || 'it/fulminazzo/conveyor/1.0/conveyor-1.0.jar'
        'jar'     | 'sources'  || 'it/fulminazzo/conveyor/1.0/conveyor-1.0-sources.jar'
    }

}
