package it.fulminazzo.conveyor.old

import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

class CachedDownloaderTest extends Specification {
    static Map<ChecksumAlgorithm, String> CHECKSUMS = [
            (ChecksumAlgorithm.MD5)   : '6cd3556deb0da54bca060b4c39479839',
            (ChecksumAlgorithm.SHA1)  : '943a702d06f34599aee1f8da8ef9f7296031d699',
            (ChecksumAlgorithm.SHA256): '315f5bdb76d078c43b8ac0064e4a0164612b1fce77c869345bfc94c75894edd3',
            (ChecksumAlgorithm.SHA512): 'c1527cd893c124773d811911970c8fe6e857d6df5dc9226bd8a160614c0cd963a4ddea2b94bb7d36021ef9d865d5cea294a82dd49a0bb269f51f6e7a57f79421'
    ]

    private CachedDownloader downloader

    void setup() {
        def file = new File('build/resources/test/downloader/cached_downloader')
        this.downloader = new CachedDownloader(file)
    }

    def 'test resolveToFile only downloads once'() {
        given:
        def workingDir = new File(this.downloader.workingDir, 'resolve_to_file')
        if (workingDir.exists()) workingDir.deleteDir()

        and:
        def path = TestUtils.LOMBOK_PATH

        and:
        def downloader = (CachedDownloader) Spy(CachedDownloader, constructorArgs: [workingDir])
                .addBaseUrls(TestUtils.MAVEN_CENTRAL_URL)

        when:
        downloader.resolveToFile(path)

        then:
        0 * downloader.verifyCachedResource(path)
        0 * downloader.resolve("$path.${ChecksumAlgorithm.MD5.extension}")
        1 * downloader.resolve(path)

        when:
        downloader.resolveToFile(path)

        then:
        1 * downloader.verifyCachedResource(path)
        1 * downloader.resolve("$path.${ChecksumAlgorithm.MD5.extension}")
        0 * downloader.resolve(path)
    }

    def 'test that resolveToFile with #verified and #redownload calls resolve #expected times'() {
        given:
        def workDir = this.downloader.workingDir
        def file = new File(workDir, 'checksum.txt')

        and:
        def downloader = (CachedDownloader) Spy(CachedDownloader, constructorArgs: [workDir])
                .setRedownloadOnUnverified(redownload)
                .addBaseUrls(TestUtils.MAVEN_CENTRAL_URL)

        and:
        downloader.verifyCachedResource(_) >> verified

        when:
        downloader.resolveToFile(file.name)

        then:
        expected * downloader.resolve(_) >> file.newInputStream()

        where:
        verified | redownload || expected
        false    | false      || 0
        true     | false      || 0
        false    | true       || 1
        true     | true       || 0
    }

    def 'test actual resolveChecksum'() {
        given:
        this.downloader.addBaseUrls(TestUtils.MAVEN_CENTRAL_URL)

        when:
        def checksum = this.downloader.resolveChecksum(TestUtils.LOMBOK_PATH, ChecksumAlgorithm.MD5)

        then:
        checksum == '959fc371f0582cfcea043ba8b90ef9bf'
    }

    def 'test that resolveChecksum of #algorithm returns expected'() {
        given:
        def downloader = Spy(CachedDownloader, constructorArgs: [this.downloader.workingDir])
        downloader.resolve(_) >> { a ->
            def extension = a[0].split('\\.')[-1]
            def alg = ChecksumAlgorithm.fromExtension(extension)
            def checksum = CHECKSUMS[alg]
            return new ByteArrayInputStream(checksum.bytes)
        }

        when:
        def actual = downloader.resolveChecksum('online.txt', algorithm)

        then:
        actual == CHECKSUMS[algorithm]

        where:
        algorithm << ChecksumAlgorithm.values()
    }

    def 'test that computeChecksum of file with #algorithm returns expected'() {
        given:
        def path = 'checksum.txt'

        when:
        def actual = this.downloader.computeChecksum(path, algorithm)

        then:
        actual == CHECKSUMS[algorithm]

        where:
        algorithm << ChecksumAlgorithm.values()
    }

}
