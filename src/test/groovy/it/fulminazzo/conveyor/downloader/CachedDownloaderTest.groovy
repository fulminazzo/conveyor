package it.fulminazzo.conveyor.downloader

import spock.lang.Specification

class CachedDownloaderTest extends Specification {

    private CachedDownloader downloader

    void setup() {
        def file = new File('build/resources/test/downloader/cached_downloader')
        this.downloader = new CachedDownloader(file)
    }

    def 'test that computeChecksum of file with #algorithm returns #expected'() {
        given:
        def path = 'checksum.txt'

        when:
        def actual = this.downloader.computeChecksum(path, algorithm)

        then:
        actual == expected

        where:
        algorithm                || expected
        ChecksumAlgorithm.MD5    || '6cd3556deb0da54bca060b4c39479839'
        ChecksumAlgorithm.SHA1   || '943a702d06f34599aee1f8da8ef9f7296031d699'
        ChecksumAlgorithm.SHA256 || '315f5bdb76d078c43b8ac0064e4a0164612b1fce77c869345bfc94c75894edd3'
        ChecksumAlgorithm.SHA512 || 'c1527cd893c124773d811911970c8fe6e857d6df5dc9226bd8a160614c0cd963a4ddea2b94bb7d36021ef9d865d5cea294a82dd49a0bb269f51f6e7a57f79421'
    }

}
