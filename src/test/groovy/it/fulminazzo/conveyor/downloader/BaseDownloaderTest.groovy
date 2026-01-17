package it.fulminazzo.conveyor.downloader

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class BaseDownloaderTest extends Specification {
    private static final File workingDir = new File(TestUtils.BASE_DIR, 'base_downloader')

    def 'test that resolveToFile correctly saves to path'() {
        given:
        if (workingDir.exists()) workingDir.deleteDir()

        and:
        def downloader = Spy(BaseDownloader, constructorArgs: [workingDir, log])
        downloader.resolve(_, _) >> { a ->
            return new ByteArrayInputStream("Data of '${a[0]}'".bytes)
        }

        when:
        def file = downloader.resolveToFile('path/to/resource.txt', [])

        then:
        file.exists()

        and:
        file.absolutePath == workingDir.absolutePath + '/path/to/resource.txt'

        and:
        file.readLines() == ['Data of \'path/to/resource.txt\'']
    }

    def 'test that resolve of #resourcePath does not throw'() {
        given:
        def downloader = new BaseDownloader(workingDir, log)

        when:
        def data = downloader.resolve(resourcePath, [
                new DownloadSource('fulminazzo.it'),
                new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)
        ])

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
        def downloader = new BaseDownloader(workingDir, log)

        when:
        downloader.resolve('path', [new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)])

        then:
        def e = thrown(DownloadException)
        (e.cause instanceof FileNotFoundException)
    }

    def 'test that resolve throws DownloadException on no sources given'() {
        given:
        def downloader = new BaseDownloader(workingDir, log)

        when:
        downloader.resolve('path', [])

        then:
        def e = thrown(DownloadException)
        e.message.contains('No download source')
    }

}
