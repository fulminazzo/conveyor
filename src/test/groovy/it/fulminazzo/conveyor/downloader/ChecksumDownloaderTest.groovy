package it.fulminazzo.conveyor.downloader

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.downloader.policy.ChecksumPolicies
import it.fulminazzo.conveyor.util.TestUtils
import org.slf4j.Logger
import spock.lang.Specification

@Slf4j
class ChecksumDownloaderTest extends Specification {
    private static final Map<ChecksumAlgorithm, String> checksum = [
            (ChecksumAlgorithm.MD5)   : '6cd3556deb0da54bca060b4c39479839',
            (ChecksumAlgorithm.SHA1)  : '943a702d06f34599aee1f8da8ef9f7296031d699',
            (ChecksumAlgorithm.SHA256): '315f5bdb76d078c43b8ac0064e4a0164612b1fce77c869345bfc94c75894edd3',
            (ChecksumAlgorithm.SHA512): 'c1527cd893c124773d811911970c8fe6e857d6df5dc9226bd8a160614c0cd963a4ddea2b94bb7d36021ef9d865d5cea294a82dd49a0bb269f51f6e7a57f79421'
    ]
    private final Map<ChecksumAlgorithm, DownloadSource> downloadSources = ChecksumAlgorithm.values()
            .collectEntries { [(it): mockSource("${it.extension}.com", it)] }

    private Downloader delegate
    private ChecksumDownloader downloader

    void setup() {
        this.delegate = new BaseDownloader(new File(TestUtils.BASE_DIR, 'checksum_downloader'), log)
        this.downloader = new ChecksumDownloader(delegate, log)
    }

    def 'test resolveToFile only downloads once'() {
        given:
        def workingDir = new File(this.delegate.workingDir, 'integration_tests')
        if (workingDir.exists()) workingDir.deleteDir()

        and:
        def path = TestUtils.LOMBOK_PATH
        def source = new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)

        and:
        def delegate = Spy(BaseDownloader, constructorArgs: [workingDir, log])

        and:
        def downloader = (ChecksumDownloader) Spy(ChecksumDownloader, constructorArgs: [delegate, log])

        when:
        downloader.resolveToFile(path, [source])

        then:
        0 * downloader.verifyChecksum(path, [source])
        0 * downloader.resolveChecksum(path, ChecksumAlgorithm.MD5, [source])
        1 * delegate.resolveToFile(path, [source])

        when:
        downloader.resolveToFile(path, [source])

        then:
        1 * downloader.verifyChecksum(path, [source])
        1 * downloader.resolveChecksum(path, ChecksumAlgorithm.MD5, [source])
        0 * delegate.resolveToFile(path, [source])
    }

    def 'test that verifyChecksum with failure and WARN checksum policy returns true'() {
        given:
        def log = Mock(Logger)

        and:
        def downloadSource = new DownloadSource('fulminazzo.it').withCapability(ChecksumPolicies.WARN)

        and:
        def downloader = (ChecksumDownloader) Spy(ChecksumDownloader, constructorArgs: [this.delegate, log])

        and:
        downloader.computeChecksum(_, _) >> 'compute'
        downloader.resolveChecksum(_, _, _ as Collection) >> new ChecksumDownloader.ChecksumResult('resolved', downloadSource)

        when:
        def result = downloader.verifyChecksum('path', [downloadSource])

        then:
        result

        and:
        1 * log.warn(_ as String, _ as String)
        1 * log.warn(_ as String)
    }

    def 'test that verifyChecksum with failure and FAIL checksum policy throws DownloadException'() {
        given:
        def downloadSource = new DownloadSource('fulminazzo.it').withCapability(ChecksumPolicies.FAIL)

        and:
        def downloader = (ChecksumDownloader) Spy(ChecksumDownloader, constructorArgs: [this.delegate, log])

        and:
        downloader.computeChecksum(_, _) >> 'compute'
        downloader.resolveChecksum(_, _, _ as Collection) >> new ChecksumDownloader.ChecksumResult('resolved', downloadSource)

        when:
        downloader.verifyChecksum('path', [downloadSource])

        then:
        thrown(DownloadException)
    }

    def 'test that verifyChecksum with failure and IGNORE checksum policy returns true'() {
        given:
        def downloadSource = new DownloadSource('fulminazzo.it').withCapability(ChecksumPolicies.IGNORE)

        and:
        def downloader = (ChecksumDownloader) Spy(ChecksumDownloader, constructorArgs: [this.delegate, log])

        and:
        downloader.computeChecksum(_, _) >> 'compute'
        downloader.resolveChecksum(_, _, _ as Collection) >> new ChecksumDownloader.ChecksumResult('resolved', downloadSource)

        when:
        def result = downloader.verifyChecksum('path', [downloadSource])

        then:
        result
    }

    def 'test that verifyChecksum with failure and no checksum policy returns false'() {
        given:
        def downloadSource = new DownloadSource('fulminazzo.it')

        and:
        def downloader = (ChecksumDownloader) Spy(ChecksumDownloader, constructorArgs: [this.delegate, log])

        and:
        downloader.computeChecksum(_, _) >> 'compute'
        downloader.resolveChecksum(_, _, _ as Collection) >> new ChecksumDownloader.ChecksumResult('resolved', downloadSource)

        when:
        def result = downloader.verifyChecksum('path', [downloadSource])

        then:
        !result
    }

    def 'test that verifyChecksum with #algorithm returns true'() {
        given:
        def downloader = Spy(ChecksumDownloader, constructorArgs: [this.delegate, log])

        and:
        downloader.computeChecksum(_, _) >> checksum[algorithm]
        downloader.resolveChecksum(_, _, _ as Collection) >> { a ->
            ChecksumAlgorithm alg = a[1]
            if (alg == algorithm) return new ChecksumDownloader.ChecksumResult(checksum[alg], downloadSources[alg])
            else throw new DownloadException('Checksum not found')
        }

        when:
        def result = downloader.verifyChecksum('path', [])

        then:
        result

        where:
        algorithm << ChecksumAlgorithm.values()
    }

    def 'test that verifyChecksum returns false if no checksum could be resolved'() {
        given:
        def downloader = Spy(ChecksumDownloader, constructorArgs: [this.delegate, log])

        and:
        downloader.resolveChecksum(_, _, _) >> { a ->
            throw new DownloadException('Checksum not found')
        }

        when:
        def result = downloader.verifyChecksum('path', [])

        then:
        !result
    }

    def 'test that resolveChecksum with algorithm #algorithm returns expected'() {
        when:
        def actual = this.downloader
                .resolveChecksum('checksum.txt', algorithm, downloadSources.values())

        then:
        actual.checksum() == checksum[algorithm]
        actual.source() == downloadSources[algorithm]

        where:
        algorithm << ChecksumAlgorithm.values()
    }

    def 'test that resolveChecksum throws if it could not find the checksum with algorithm #algorithm'() {
        when:
        this.downloader.resolveChecksum('checksum.txt', algorithm, [])

        then:
        thrown(DownloadException)

        where:
        algorithm << ChecksumAlgorithm.values()
    }

    def 'test that resolveChecksum does not throw on IOException'() {
        given:
        def sources = []

        and:
        final algorithm = ChecksumAlgorithm.MD5

        and:
        def first = Mock(DownloadSource)
        first.resolveResource(_) >> {
            throw new IOException('Test exception')
        }
        sources.add(first)

        and:
        sources.add(downloadSources[algorithm])

        when:
        this.downloader.resolveChecksum('checksum.txt', algorithm, sources)

        then:
        noExceptionThrown()
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
