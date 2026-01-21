package it.fulminazzo.conveyor.model.repository

import spock.lang.Specification

class ChecksumPolicyTest extends Specification {

    def 'test that of function returns #expected for #expected'() {
        when:
        def actual = ChecksumPolicy.of(expected.value())

        then:
        actual == expected

        where:
        expected << ChecksumPolicy.values()
    }

    def 'test that of function throws IllegalArgumentException for invalid value'() {
        when:
        ChecksumPolicy.of('not_existing')

        then:
        thrown(IllegalArgumentException)
    }

}
