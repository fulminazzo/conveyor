package it.fulminazzo.conveyor.model

import spock.lang.Specification

class PropertiesTest extends Specification {

    def 'test that properties correctly applies properties'() {
        given:
        def properties = new Properties([
                'simple'         : 'Hello',
                'first'          : 'world!',
                'second.property': '${first}',
                'third'          : '${second.property}'
        ])

        when:
        def actual = properties.apply('${simple}, ${third} ${unknown}')

        then:
        actual == 'Hello, world! ${unknown}'
    }

}
