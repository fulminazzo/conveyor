package it.fulminazzo.conveyor.downloader

import spock.lang.Specification

class DownloaderImplTest extends Specification {
    private Downloader downloader

    void setup() {
        def file = new File('build/resources/test/downloader/downloader_impl')
        this.downloader = new DownloaderImpl(file)
    }

    def 'test that addBaseUrls adds URLs in correct format'() {
        given:
        def urls = [
                'https://url1.com/',
                'https://url2.com',
                'http://url3.net/',
                'http://url4.net',
                'url5.it/',
                'url6.it'
        ]

        and:
        def expected = (1..6).collect {
            "${it != 3 && it != 4 ? 'https' : 'http'}://url${it}.${it < 3 ? 'com' : it < 5 ? 'net' : 'it'}/"
        }

        when:
        this.downloader.addBaseUrls(*urls)

        then:
        this.downloader.baseUrls.toList() == expected
    }

    def 'test that addBaseUrls throws on invalid URLs'() {
        given:
        def urls = [
                'https://url1.com/',
                'url2^.com'
        ]

        when:
        this.downloader.addBaseUrls(*urls)

        then:
        thrown(MalformedURLException)
    }

}
