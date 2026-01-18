package it.fulminazzo.conveyor.property

import spock.lang.Specification

class PropertyAccessorTest extends Specification {

    def 'test that getIndexed of #name with #index returns #expected'() {
        given:
        def object = new MockObject()

        when:
        def value = PropertyAccessor.getIndexed(object, name, index)

        then:
        value == expected

        where:
        name       | index || expected
        'indexed1' | 0     || 'Hello'
        'indexed1' | '0'   || 'Hello'
        'indexed2' | 2     || 3
        'indexed2' | '2'   || 3
        'indexed3' | 1     || false
        'indexed3' | '1'   || false
        'indexed4' | 1     || 'world'
        'indexed4' | '1'   || 'world'
    }

    def 'test that getIndexed of non indexable object throws'() {
        given:
        def object = new MockObject()

        when:
        PropertyAccessor.getIndexed(object, 'field1', 0)

        then:
        thrown(IllegalArgumentException)
    }

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
