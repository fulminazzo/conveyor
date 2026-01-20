package it.fulminazzo.conveyor.model.properties.util

import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

class LinuxUtilsTest extends Specification {

    def 'test that parseReleaseFile with #id, #version and #like returns #expected'() {
        given:
        def file = new File(TestUtils.BASE_DIR, 'linux_utils/parse_release_file')
        if (file.exists()) file.delete()
        file.parentFile.mkdirs()
        file.createNewFile()

        and:
        file << """NAME="Mock Linux"
PRETTY_NAME="Mock Linux"${id == null ? '' : "\n$LinuxUtils.idPrefix$id"}
BUILD_ID=rolling${like == null ? '' : "\n$LinuxUtils.idLikePrefix$like"}
ANSI_COLOR="32;1;24;144;200"${version == null ? '' : "\n$LinuxUtils.versionIdPrefix$version"}
HOME_URL="https://linux.org/"
DOCUMENTATION_URL="https://wiki.linux.org/"
SUPPORT_URL="https://forum.linux.org/"
BUG_REPORT_URL="https://linux.org/help/"
PRIVACY_POLICY_URL="https://linux.org/privacy-policy/"
LOGO=linux
"""

        when:
        def actual = LinuxUtils.parseReleaseFile(file.absolutePath)

        then:
        actual == expected

        where:
        id          | like             | version  || expected
        null        | null             | null     || null
        null        | null             | '6.12'   || null
        null        | 'arch'           | null     || null
        null        | 'arch'           | '6.12'   || null
        null        | 'arch manjaro'   | null     || null
        null        | 'arch manjaro'   | '6.12'   || null
        'manjaro'   | null             | null     || new LinuxUtils.Release('manjaro', null, ['manjaro'].toSet())
        'manjaro'   | null             | '6.12'   || new LinuxUtils.Release('manjaro', '6.12', ['manjaro'].toSet())
        'manjaro'   | 'arch'           | null     || new LinuxUtils.Release('manjaro', null, ['arch', 'manjaro'].toSet())
        'manjaro'   | 'arch'           | '6.12'   || new LinuxUtils.Release('manjaro', '6.12', ['arch', 'manjaro'].toSet())
        'manjaro'   | 'arch manjaro'   | null     || new LinuxUtils.Release('manjaro', null, ['arch', 'manjaro'].toSet())
        'manjaro'   | 'arch manjaro'   | '6.12'   || new LinuxUtils.Release('manjaro', '6.12', ['arch', 'manjaro'].toSet())
        '"manjaro"' | null             | null     || new LinuxUtils.Release('manjaro', null, ['manjaro'].toSet())
        '"manjaro"' | null             | '"6.12"' || new LinuxUtils.Release('manjaro', '6.12', ['manjaro'].toSet())
        '"manjaro"' | '"arch"'         | null     || new LinuxUtils.Release('manjaro', null, ['arch', 'manjaro'].toSet())
        '"manjaro"' | '"arch"'         | '"6.12"' || new LinuxUtils.Release('manjaro', '6.12', ['arch', 'manjaro'].toSet())
        '"manjaro"' | '"arch manjaro"' | null     || new LinuxUtils.Release('manjaro', null, ['arch', 'manjaro'].toSet())
        '"manjaro"' | '"arch manjaro"' | '"6.12"' || new LinuxUtils.Release('manjaro', '6.12', ['arch', 'manjaro'].toSet())
    }

    def 'test that parseRedhatReleaseFile with #id and #version returns #expected'() {
        given:
        def file = new File(TestUtils.BASE_DIR, 'linux_utils/parse_redhat_release_file')
        if (file.exists()) file.delete()
        file.parentFile.mkdirs()
        file.createNewFile()

        and:
        file << "${id ?: ''} ${version ?: ''} (Core)"

        when:
        def actual = LinuxUtils.parseRedhatReleaseFile(file.absolutePath)

        then:
        actual == expected

        where:
        id                                 | version    || expected
        'Rocky Linux release'              | null       || null
        'Rocky Linux release'              | '9.2'      || null
        'CentOS'                           | null       || new LinuxUtils.Release('centos', null, LinuxUtils.defaultRedhatVariants.toSet())
        'CentOS'                           | '7.9.2009' || new LinuxUtils.Release('centos', '7', LinuxUtils.defaultRedhatVariants.toSet())
        'Fedora'                           | null       || new LinuxUtils.Release('fedora', null, LinuxUtils.defaultRedhatVariants.toSet())
        'Fedora'                           | '16'       || new LinuxUtils.Release('fedora', '16', LinuxUtils.defaultRedhatVariants.toSet())
        'Red Hat Enterprise Linux release' | null       || new LinuxUtils.Release('rhel', null, LinuxUtils.defaultRedhatVariants.toSet())
        'Red Hat Enterprise Linux release' | '8.9'      || new LinuxUtils.Release('rhel', '8', LinuxUtils.defaultRedhatVariants.toSet())
    }

}
