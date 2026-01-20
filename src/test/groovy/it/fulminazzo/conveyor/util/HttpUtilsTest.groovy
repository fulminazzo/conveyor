package it.fulminazzo.conveyor.util

import spock.lang.Specification

class HttpUtilsTest extends Specification {

    def 'test that openHttpConnection redirects on too many redirects'() {
        given:
        final url = 'https://fulminazzo.it'
        final redirect = "https://$TestUtils.MAVEN_CENTRAL_URL"
        final path = "/$TestUtils.LOMBOK_PATH"

        and:
        def info = new HttpUtils.RedirectInfo(redirect)
        for (i in 1..HttpUtils.MAX_REDIRECTS) info.addRedirect()
        HttpUtils.redirects.put(url, info)

        when:
        HttpUtils.openHttpConnection(url, path)

        then:
        thrown(FileNotFoundException)

        when:
        info.addRedirect()

        and:
        def data = HttpUtils.openHttpConnection(url, path)

        then:
        noExceptionThrown()

        and:
        data.available() > 0

        cleanup:
        HttpUtils.redirects.clear()
    }

    def 'test that openHttpConnection of #resourcePath does not throw'() {
        when:
        def data = HttpUtils.openHttpConnection(website, resourcePath)

        then:
        noExceptionThrown()

        and:
        data.available() > 0

        cleanup:
        data.close()

        where:
        resourcePath              | website
        TestUtils.LOMBOK_PATH     | "https://$TestUtils.MAVEN_CENTRAL_URL"
        "/$TestUtils.LOMBOK_PATH" | "https://$TestUtils.MAVEN_CENTRAL_URL"
        TestUtils.LOMBOK_PATH     | "https://$TestUtils.MAVEN_CENTRAL_URL/"
        "/$TestUtils.LOMBOK_PATH" | "https://$TestUtils.MAVEN_CENTRAL_URL/"
    }

    def 'test that openHttpConnection throws IOException on not found'() {
        when:
        HttpUtils.openHttpConnection("https://$TestUtils.MAVEN_CENTRAL_URL/", 'path')

        then:
        thrown(IOException)
    }

    def 'test that handleRedirect of #url, #redirect and #resourcePath calls openHttpConnection with #expectedUrl, #expectedPath'() {
        given:
        SpyStatic(HttpUtils)

        when:
        HttpUtils.handleRedirect(url, redirect, resourcePath)

        then:
        1 * HttpUtils.openHttpConnection(_ as String, _ as String) >> { String actualUrl, String actualPath ->
            assert actualUrl == expectedUrl
            assert actualPath == expectedPath
            return null
        }

        where:
        url                                    | redirect                           | resourcePath                 || expectedUrl                 | expectedPath
        // RELATIVE FOUND SUFFIX
        'https://fulminazzo.it'                | '/it/conveyor'                     | '/it/fulminazzo/conveyor'    || 'https://fulminazzo.it/it'  | '/conveyor'
        'https://fulminazzo.it'                | '/it/conveyor'                     | '/it/fulminazzo/theconveyor' || 'https://fulminazzo.it/it/' | 'conveyor'
        // RELATIVE NOT FOUND SUFFIX
        'https://fulminazzo.it'                | '/maven/project'                   | '/it/fulminazzo/conveyor'    || 'https://fulminazzo.it'     | '/maven/project'
        'https://fulminazzo.it/'               | '/maven/project'                   | 'it/fulminazzo/conveyor'     || 'https://fulminazzo.it'     | '/maven/project'
        'https://fulminazzo.it/it/fulminazzo'  | '/maven/project'                   | '/conveyor'                  || 'https://fulminazzo.it'     | '/maven/project'
        'https://fulminazzo.it/it/fulminazzo/' | '/maven/project'                   | 'conveyor'                   || 'https://fulminazzo.it'     | '/maven/project'
        // ABSOLUTE NOT FOUND SUFFIX
        'https://fulminazzo.it'                | 'https://apache.org/maven/project' | '/it/fulminazzo/conveyor'    || 'https://apache.org'        | '/maven/project'
        'https://fulminazzo.it/'               | 'https://apache.org/maven/project' | 'it/fulminazzo/conveyor'     || 'https://apache.org'        | '/maven/project'
    }

    def 'test that extractUrl of #url returns #expected'() {
        when:
        def actual = HttpUtils.extractUrl(url)

        then:
        actual == expected

        where:
        url                                        || expected
        'https://fulminazzo.it'                    || 'https://fulminazzo.it'
        'https://fulminazzo.it/'                   || 'https://fulminazzo.it'
        'https://fulminazzo.it/first'              || 'https://fulminazzo.it'
        'https://fulminazzo.it/first/'             || 'https://fulminazzo.it'
        'https://fulminazzo.it/first/second'       || 'https://fulminazzo.it'
        'https://fulminazzo.it/first/second/'      || 'https://fulminazzo.it'
        'https://fulminazzo.it:8080'               || 'https://fulminazzo.it:8080'
        'https://fulminazzo.it:8080/'              || 'https://fulminazzo.it:8080'
        'https://fulminazzo.it:8080/first'         || 'https://fulminazzo.it:8080'
        'https://fulminazzo.it:8080/first/'        || 'https://fulminazzo.it:8080'
        'https://fulminazzo.it:8080/first/second'  || 'https://fulminazzo.it:8080'
        'https://fulminazzo.it:8080/first/second/' || 'https://fulminazzo.it:8080'
    }

    def 'test that extractUrl throws on invalid url'() {
        when:
        HttpUtils.extractUrl('ftp://test^')

        then:
        thrown(MalformedURLException)
    }

    def 'test that formatUrl correctly formats #url to #expected'() {
        when:
        def actual = HttpUtils.formatUrl(url)

        then:
        actual == expected

        where:
        url                      || expected
        'https://fulminazzo.it/' || 'https://fulminazzo.it'
        'https://fulminazzo.it'  || 'https://fulminazzo.it'
        'http://fulminazzo.it/'  || 'http://fulminazzo.it'
        'http://fulminazzo.it'   || 'http://fulminazzo.it'
        'fulminazzo.it/'         || 'https://fulminazzo.it'
        'fulminazzo.it'          || 'https://fulminazzo.it'
    }

    def 'test that formatUrl throws on invalid url'() {
        when:
        HttpUtils.formatUrl('ftp://test^')

        then:
        thrown(MalformedURLException)
    }

}
