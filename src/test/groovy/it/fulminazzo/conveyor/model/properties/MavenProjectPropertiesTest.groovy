package it.fulminazzo.conveyor.model.properties

import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.pom.Pom
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

class MavenProjectPropertiesTest extends Specification {

    def 'test that immutable does not return custom variable'() {
        given:
        def properties = new MavenProjectProperties(
                Pom.builder().project(new Artifact('it.fulminazzo', 'conveyor', '1.0')).build(),
                TestUtils.BASE_DIR
        ).add('first', 'Hello, world!')

        expect:
        properties['first'] == 'Hello, world!'

        when:
        def immutable = properties.toImmutable()

        then:
        immutable['first'] == null
    }

}
