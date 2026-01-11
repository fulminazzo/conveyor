package it.fulminazzo.conveyor.artifact.pom.repository.update

import spock.lang.Specification

class UpdatePolicyTest extends Specification {

    def 'test that #policy shouldUpdate of #time returns #expected'() {
        when:
        def actual = policy.shouldUpdate(time)

        then:
        actual == expected

        where:
        policy                       | time                                             || expected
        UpdatePolicyEnum.ALWAYS      | System.currentTimeMillis()                       || true
        UpdatePolicyEnum.DAILY       | System.currentTimeMillis()                       || false
        UpdatePolicyEnum.DAILY       | System.currentTimeMillis() - 24 * 60 * 60 * 1000 || true
        UpdatePolicyEnum.NEVER       | System.currentTimeMillis()                       || false
        new IntervalUpdatePolicy(60) | System.currentTimeMillis() - 61 * 60 * 1000      || true
        new IntervalUpdatePolicy(60) | System.currentTimeMillis()                       || false
    }

    def 'test that UpdatePolicy of #rawPolicy returns #expected'() {
        when:
        def actual = UpdatePolicy.of(rawPolicy)

        then:
        actual == expected

        where:
        rawPolicy     || expected
        'always'      || UpdatePolicyEnum.ALWAYS
        'daily'       || UpdatePolicyEnum.DAILY
        'never'       || UpdatePolicyEnum.NEVER
        'interval:60' || new IntervalUpdatePolicy(60)
    }

    def 'test that UpdatePolicy of #rawPolicy throws IllegalArgumentException'() {
        when:
        UpdatePolicy.of(rawPolicy)

        then:
        thrown(IllegalArgumentException)

        where:
        rawPolicy << [
                'interval', 'interval:',
                'interval:hello', 'interval:3.14',
                'unknown'
        ]
    }

}
