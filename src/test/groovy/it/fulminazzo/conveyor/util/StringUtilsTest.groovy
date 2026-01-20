package it.fulminazzo.conveyor.util

import spock.lang.Specification

class StringUtilsTest extends Specification {

    def 'test that findCommonSuffix with #target and #suffix returns #expected'() {
        expect:
        StringUtils.findCommonSuffix(target, suffix) == expected

        where:
        target  | suffix       || expected
        'hello' | 'hello'      || 0
        'hello' | 'llo'        || 2
        'hello' | 'rip'        || 5
        'hello' | 'worldhello' || -1
    }

}
