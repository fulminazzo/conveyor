package it.fulminazzo.conveyor.model.properties

import spock.lang.Specification

class EnvPropertiesTest extends Specification {

    def 'test that EnvProperties returns env property'() {
        given:
        def properties = new EnvProperties()

        when:
        def property = properties.get('env.PATH')

        then:
        property != null

        and:
        property == System.getenv('PATH')
    }

    def 'test that EnvProperties does not return if it does not start with prefix'() {
        given:
        def properties = new EnvProperties()

        when:
        def property = properties.get('PATH')

        then:
        property == null
    }

}
