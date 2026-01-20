package it.fulminazzo.conveyor.util

import spock.lang.Specification

class RedirectInfoTest extends Specification {

    def 'test that getRedirects returns only valid redirects'() {
        given:
        def info = new HttpUtils.RedirectInfo('fulminazzo.it')

        and:
        for (i in 1..10) info.addRedirect()

        and:
        def now = System.currentTimeMillis()
        for (i in 1..10) info.timestamps.add(now - HttpUtils.RedirectInfo.REDIRECT_LIFE_TIME - 10)

        when:
        def redirects = info.redirects

        then:
        redirects == 10
    }

    def 'test that getRedirects returns the correct number of redirects'() {
        given:
        def info = new HttpUtils.RedirectInfo('fulminazzo.it')

        expect:
        for (i in 1..10) {
            info.addRedirect()
            assert info.redirects == i
        }
    }

}
