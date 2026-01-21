package it.fulminazzo.conveyor.model.properties

import spock.lang.Specification

class BasePropertiesTest extends Specification {

    def 'test that BaseProperties correctly applies properties'() {
        given:
        def properties = new MockProperties()

        and:
        properties.putAll([
                'simple'         : 'Hello',
                'first'          : 'world!',
                'second.property': '${first}',
                'third'          : '${second.property}'
        ])

        when:
        def actual = properties.apply('${unknown} ${simple}, ${unknown} ${third} ${unknown}')

        then:
        actual == '${unknown} Hello, ${unknown} world! ${unknown}'
    }

}
