package it.fulminazzo.conveyor.property

import spock.lang.Specification

class PropertyAccessorTest extends Specification {

    def 'test that getField of #name returns #expected'() {
        given:
        def object = new MockObject()

        when:
        def value = PropertyAccessor.getField(object, name)

        then:
        value == expected

        where:
        name     || expected
        'field1' || 'Hello, world!'
        'field2' || 10
        'field3' || true
        'field4' || null
        'field5' || null
    }

    def 'test that invokeMethod of #name returns #expected'() {
        given:
        def object = new MockObject()

        when:
        def value = PropertyAccessor.invokeMethod(object, name)

        then:
        value == expected

        where:
        name      || expected
        'method1' || 'Hello, world!'
        'method2' || 10
        'method3' || true
        'method4' || null
        'method5' || null
        'method6' || null
    }

}
