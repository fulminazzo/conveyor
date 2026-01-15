package it.fulminazzo.conveyor.downloader

import spock.lang.Specification

class DownloadSourceTest extends Specification {

    def 'test that DownloadSource correctly formats #url to #expected'() {
        when:
        def source = new DownloadSource(url)

        then:
        source.url == expected

        where:
        url                      || expected
        'https://fulminazzo.it/' || 'https://fulminazzo.it/'
        'https://fulminazzo.it'  || 'https://fulminazzo.it/'
        'http://fulminazzo.it/'  || 'http://fulminazzo.it/'
        'http://fulminazzo.it'   || 'http://fulminazzo.it/'
        'fulminazzo.it/'         || 'https://fulminazzo.it/'
        'fulminazzo.it'          || 'https://fulminazzo.it/'
    }

    def 'test that DownloadSource throws on invalid url'() {
        when:
        new DownloadSource('ftp://test^')

        then:
        thrown(MalformedURLException)
    }

}
