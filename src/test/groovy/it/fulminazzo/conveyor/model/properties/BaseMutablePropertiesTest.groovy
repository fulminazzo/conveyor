package it.fulminazzo.conveyor.model.properties

import spock.lang.Specification

class BaseMutablePropertiesTest extends Specification {

    def 'test that BaseMutableProperties returns all properties'() {
        given:
        def properties = new BaseMutableProperties().addAll([
                first : '1',
                second: '2'
        ])

        expect:
        properties['first'] == '1'

        and:
        properties['second'] == '2'

        and:
        properties['third'] == null
    }

}
