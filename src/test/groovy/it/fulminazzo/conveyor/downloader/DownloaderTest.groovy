package it.fulminazzo.conveyor.downloader

import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

class DownloaderTest extends Specification {
    private static final File workingDir = new File(TestUtils.BASE_DIR, 'downloader')

    def 'test that resolveToFile correctly saves to path'() {
        given:
        if (workingDir.exists()) workingDir.deleteDir()

        and:
        def downloader = new MockDownloader(workingDir)

        when:
        def file = downloader.resolveToFile('path/to/resource.txt', [])

        then:
        file.exists()

        and:
        file.absolutePath == workingDir.absolutePath + '/path/to/resource.txt'

        and:
        file.readLines() == ['Data of \'path/to/resource.txt\'']
    }

}
