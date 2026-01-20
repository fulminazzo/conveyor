package it.fulminazzo.conveyor.downloader

import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

class DownloadSourceTest extends Specification {

    def 'test that DownloadSource correctly formats #url to #expected'() {
        when:
        def source = new DownloadSource(url)

        then:
        source.url == expected

        where:
        url                      || expected
        'https://fulminazzo.it/' || 'https://fulminazzo.it'
        'https://fulminazzo.it'  || 'https://fulminazzo.it'
        'http://fulminazzo.it/'  || 'http://fulminazzo.it'
        'http://fulminazzo.it'   || 'http://fulminazzo.it'
        'fulminazzo.it/'         || 'https://fulminazzo.it'
        'fulminazzo.it'          || 'https://fulminazzo.it'
    }

    def 'test that DownloadSource throws on invalid url'() {
        when:
        new DownloadSource('ftp://test^')

        then:
        thrown(MalformedURLException)
    }

    def 'test that resolveResource of #resourcePath does not throw'() {
        given:
        def source = new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)

        when:
        def data = source.resolveResource(resourcePath)

        then:
        noExceptionThrown()

        and:
        data.available() > 0

        cleanup:
        data.close()

        where:
        resourcePath << [TestUtils.LOMBOK_PATH, "/$TestUtils.LOMBOK_PATH"]
    }

    def 'test that resolveResource throws IOException on not found'() {
        given:
        def source = new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)

        when:
        source.resolveResource('path')

        then:
        thrown(IOException)
    }

    def 'test that getCapability returns exact type if present'() {
        given:
        def source = new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)
                .withCapability('Hello, world!')

        when:
        def capability = source.getCapability(String)

        then:
        capability.isPresent()

        and:
        capability.get() == 'Hello, world!'
    }

    def 'test that getCapability does not throw if not present'() {
        given:
        def source = new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)

        when:
        def capability = source.getCapability(String)

        then:
        !capability.isPresent()
    }

}
