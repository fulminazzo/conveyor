package it.fulminazzo.conveyor.downloader

import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

class ChecksumDownloaderTest extends Specification {
    private static final Map<ChecksumAlgorithm, String> checksum = [
            (ChecksumAlgorithm.MD5)   : '6cd3556deb0da54bca060b4c39479839',
            (ChecksumAlgorithm.SHA1)  : '943a702d06f34599aee1f8da8ef9f7296031d699',
            (ChecksumAlgorithm.SHA256): '315f5bdb76d078c43b8ac0064e4a0164612b1fce77c869345bfc94c75894edd3',
            (ChecksumAlgorithm.SHA512): 'c1527cd893c124773d811911970c8fe6e857d6df5dc9226bd8a160614c0cd963a4ddea2b94bb7d36021ef9d865d5cea294a82dd49a0bb269f51f6e7a57f79421'
    ]
    private final Map<ChecksumAlgorithm, DownloadSource> downloadSources = ChecksumAlgorithm.values()
            .collectEntries { [(it): mockSource("${it.extension}.com", it)] }

    private ChecksumDownloader downloader

    void setup() {
        def delegate = new BaseDownloader(new File(TestUtils.BASE_DIR, 'checksum_downloader'))
        this.downloader = new ChecksumDownloader(delegate)
    }

    def 'test that resolveChecksum with algorithm #algorithm returns expected'() {
        when:
        def actual = this.downloader
                .addDownloadSources(downloadSources.values())
                .resolveChecksum('checksum.txt', algorithm)

        then:
        actual.checksum() == checksum[algorithm]
        actual.source() == downloadSources[algorithm]

        where:
        algorithm << ChecksumAlgorithm.values()
    }

    def 'test that resolveChecksum throws if it could not find the checksum with algorithm #algorithm'() {
        when:
        this.downloader.resolveChecksum('checksum.txt', algorithm)

        then:
        thrown(DownloadException)

        where:
        algorithm << ChecksumAlgorithm.values()
    }

    def 'test that computeChecksum of file with #algorithm returns expected'() {
        given:
        def path = 'checksum.txt'

        when:
        def actual = this.downloader.computeChecksum(path, algorithm)

        then:
        actual == checksum[algorithm]

        where:
        algorithm << ChecksumAlgorithm.values()
    }

    private DownloadSource mockSource(final String url, final ChecksumAlgorithm algorithm) {
        def source = Mock(DownloadSource)
        source.url >> url
        source.resolveResource(_) >> { a ->
            String resourcePath = a[0]
            if (resourcePath.endsWith(algorithm.extension))
                return new ByteArrayInputStream(checksum[algorithm].bytes)
            else throw new FileNotFoundException(resourcePath)
        }
        return source
    }

}
