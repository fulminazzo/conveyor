package it.fulminazzo.conveyor.util

import it.fulminazzo.conveyor.downloader.DownloadSource
import spock.lang.Specification

class HttpUtilsTest extends Specification {

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

    def 'test that formatUrl correctly formats #url to #expected'() {
        when:
        def actual = HttpUtils.formatUrl(url)

        then:
        actual == expected

        where:
        url                      || expected
        'https://fulminazzo.it/' || 'https://fulminazzo.it/'
        'https://fulminazzo.it'  || 'https://fulminazzo.it/'
        'http://fulminazzo.it/'  || 'http://fulminazzo.it/'
        'http://fulminazzo.it'   || 'http://fulminazzo.it/'
        'fulminazzo.it/'         || 'https://fulminazzo.it/'
        'fulminazzo.it'          || 'https://fulminazzo.it/'
    }

    def 'test that formatUrl throws on invalid url'() {
        when:
        HttpUtils.formatUrl('ftp://test^')

        then:
        thrown(MalformedURLException)
    }

}
