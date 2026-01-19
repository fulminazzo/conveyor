package it.fulminazzo.conveyor.model.dependency


import spock.lang.Specification

class ExclusionsManagerTest extends Specification {

    def 'test that isExcluded returns #expected for #groupId and #artifactId (wildcard: #wildcard)'() {
        given:
        def exclusions = new ExclusionsManager()

        and:
        exclusions.add('it.fulminazzo', 'first')
        exclusions.add('it.fulminazzo', 'second')
        exclusions.add('it.wildcard', '*')
        exclusions.add('*', 'wildcard')
        if (wildcard) exclusions.add('*', '*')

        when:
        def actual = exclusions.isExcluded(groupId, artifactId)

        then:
        actual == expected

        where:
        groupId         | artifactId | wildcard || expected
        'it.fulminazzo' | 'first'    | false    || true
        'it.fulminazzo' | 'second'   | false    || true
        'it.fulminazzo' | 'third'    | false    || false
        'it.wildcard'   | 'first'    | false    || true
        '*'             | 'wildcard' | false    || true
        'it.fulminazzo' | 'third'    | true     || true
    }

}
