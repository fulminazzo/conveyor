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

    def 'test that properties does not override on put and putAll'() {
        given:
        def properties = new Properties()

        and:
        properties.putAll([a: '1', b: '2', c: '3'])

        when:
        properties.putAll([a: '4', b: '5', c: '6', d: '7'])

        and:
        properties << [a: '8', b: '9', c: '10', d: '11', e: '12']

        and:
        for (def i in 12..17) {
            char c = (char) 'a'
            c += i - 12
            properties.put(String.valueOf(c), String.valueOf(i))
        }

        then:
        properties == [a: '1', b: '2', c: '3', d: '7', e: '12', f: '17']
    }

}
