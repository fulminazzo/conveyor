package it.fulminazzo.conveyor.model.dependency

import spock.lang.Specification

class DependencyTest extends Specification {

    def 'test that getCoordinates returns correct coordinates'() {
        given:
        def dependency = Dependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('dependency')
                .version('1.0')
                .classifier('source')
                .type('war')
                .build()

        when:
        def coordinates = dependency.coordinates

        then:
        coordinates == 'it.fulminazzo:dependency:war:source'
    }

}
