package it.fulminazzo.conveyor.model.dependency

import spock.lang.Specification

class ScopeTest extends Specification {

    def 'test that of function returns #expected for #expected'() {
        when:
        def actual = Scope.of(expected.value())

        then:
        actual == expected

        where:
        expected << Scope.values()
    }

    def 'test that of function throws IllegalArgumentException for invalid value'() {
        when:
        Scope.of('not_existing')

        then:
        thrown(IllegalArgumentException)
    }
    
}
