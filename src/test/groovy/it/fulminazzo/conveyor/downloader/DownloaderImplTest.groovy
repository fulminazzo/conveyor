package it.fulminazzo.conveyor.downloader

import spock.lang.Specification

class DownloaderImplTest extends Specification {
    private Downloader downloader

    void setup() {
        def file = new File('build/resources/test/downloader/downloader_impl')
        this.downloader = new DownloaderImpl(file)
    }

    def 'test that resolveToFile is able to download #path'() {
        given:
        def workDir = this.downloader.workingDir
        if (workDir.isDirectory()) workDir.deleteDir()

        and:
        this.downloader.addBaseUrls(
                'invalidoffline.com',
                'repo.maven.apache.org/maven2'
        )

        and:
        def expected = new File(this.downloader.workingDir, path)

        when:
        this.downloader.resolveToFile(path)

        then:
        expected.exists()

        and:
        expected.size() > 0

        where:
        path << [
                'org/projectlombok/lombok/1.18.42/lombok-1.18.42.pom',
                '/org/projectlombok/lombok/1.18.42/lombok-1.18.42.pom'
        ]
    }

    def 'test that resolveToFile throws on download error'() {
        given:
        def workDir = this.downloader.workingDir
        if (workDir.isDirectory()) workDir.deleteDir()

        and:
        this.downloader.addBaseUrls(
                'invalidoffline.com',
                'invalidoffline2.com'
        )

        when:
        this.downloader.resolveToFile('path')

        then:
        def e = thrown(DownloadException)
        e.message.contains("'path'")
    }

    def 'test that resolve throws on missing URLs'() {
        when:
        this.downloader.resolve('path')

        then:
        def e = thrown(DownloadException)
        e.message.contains('No base URL')
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
