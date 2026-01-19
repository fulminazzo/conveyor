package it.fulminazzo.conveyor.model.properties

import spock.lang.Specification

class SystemPropertiesTest extends Specification {

    def 'test that SystemProperties returns system property'() {
        given:
        def properties = new SystemProperties()

        when:
        def property = properties['os.name']

        then:
        property != null

        and:
        property == System.getProperty('os.name')
    }

}
