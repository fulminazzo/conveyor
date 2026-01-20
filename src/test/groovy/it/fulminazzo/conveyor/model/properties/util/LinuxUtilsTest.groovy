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
        file << """
NAME="Mock Linux"
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

}
