package it.fulminazzo.conveyor.downloader

import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

class BaseDownloaderTest extends Specification {
    private static final File workingDir = new File(TestUtils.BASE_DIR, 'base_downloader')

    def 'test that resolve of #resourcePath does not throw'() {
        given:
        def downloader = new BaseDownloader(workingDir)
                .addDownloadSources(
                        new DownloadSource('fulminazzo.it'),
                        new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)
                )

        when:
        def data = downloader.resolve(resourcePath)

        then:
        noExceptionThrown()

        and:
        data.available() > 0

        cleanup:
        data.close()

        where:
        resourcePath << [TestUtils.LOMBOK_PATH, "/$TestUtils.LOMBOK_PATH"]
    }

    def 'test that resolve throws DownloadException on not found'() {
        given:
        def downloader = new BaseDownloader(workingDir)
                .addDownloadSources(new DownloadSource(TestUtils.MAVEN_CENTRAL_URL))

        when:
        downloader.resolve('path')

        then:
        def e = thrown(DownloadException)
        (e.cause instanceof FileNotFoundException)
    }

    def 'test that resolve throws DownloadException on no sources given'() {
        given:
        def downloader = new BaseDownloader(workingDir)

        when:
        downloader.resolve('path')

        then:
        def e = thrown(DownloadException)
        e.message.contains('No download source')
    }

}
