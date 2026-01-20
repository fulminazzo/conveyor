package it.fulminazzo.conveyor.model.properties

import spock.lang.Specification

class OSMavenPropertiesTest extends Specification {

    def 'test that os-detected-name property with #value returns #expected'() {
        given:
        def mavenProperties = new OSMavenProperties(mockSystemProperties('os.name', value))

        when:
        def result = mavenProperties.get('os.detected.name')

        then:
        result == expected

        where:
        value            || expected
        'aix'            || 'aix'
        'hpux'           || 'hpux'
        'os400'          || 'os400'
        'os400_'         || 'os400'
        'os4001'         || 'unknown'
        'linux'          || 'linux'
        'mac'            || 'osx'
        'osx'            || 'osx'
        'freebsd'        || 'freebsd'
        'openbsd'        || 'openbsd'
        'netbsd'         || 'netbsd'
        'solaris'        || 'sunos'
        'sunos'          || 'sunos'
        'windows'        || 'windows'
        'zos'            || 'zos'
        'something_else' || 'unknown'
        null             || 'unknown'
    }

    def 'test that os-detected-arch property with #value returns #expected'() {
        given:
        def mavenProperties = new OSMavenProperties(mockSystemProperties('os.arch', value))

        when:
        def result = mavenProperties.get('os.detected.arch')

        then:
        result == expected

        where:
        value            || expected
        'x8664'          || 'x86_64'
        'amd64'          || 'x86_64'
        'ia32e'          || 'x86_64'
        'em64t'          || 'x86_64'
        'x64'            || 'x86_64'
        'x8632'          || 'x86_32'
        'x86'            || 'x86_32'
        'i386'           || 'x86_32'
        'i486'           || 'x86_32'
        'i586'           || 'x86_32'
        'i686'           || 'x86_32'
        'ia32'           || 'x86_32'
        'x32'            || 'x86_32'
        'ia64w'          || 'itanium_64'
        'ia64'           || 'itanium_64'
        'itanium64'      || 'itanium_64'
        'ia64n'          || 'itanium_32'
        'sparc'          || 'sparc_32'
        'sparc32'        || 'sparc_32'
        'sparcv9'        || 'sparc_64'
        'sparc64'        || 'sparc_64'
        'arm'            || 'arm_32'
        'arm32'          || 'arm_32'
        'aarch64'        || 'aarch_64'
        'mips'           || 'mips_32'
        'mips32'         || 'mips_32'
        'mipsel'         || 'mipsel_32'
        'mips32el'       || 'mipsel_32'
        'mips64'         || 'mips_64'
        'mips64el'       || 'mipsel_64'
        'ppc'            || 'ppc_32'
        'ppc32'          || 'ppc_32'
        'ppcle'          || 'ppcle_32'
        'ppc32le'        || 'ppcle_32'
        'ppc64'          || 'ppc_64'
        'ppc64le'        || 'ppcle_64'
        's390'           || 's390_32'
        's390x'          || 's390_64'
        'riscv'          || 'riscv'
        'riscv32'        || 'riscv'
        'riscv64'        || 'riscv64'
        'e2k'            || 'e2k'
        'loongarch64'    || 'loongarch_64'
        'something_else' || 'unknown'
        null             || 'unknown'
    }

    private SystemProperties mockSystemProperties(final String key, final String value) {
        def properties = Mock(SystemProperties)
        properties.get(_) >> { a ->
            return a[0] == key ? value : null
        }
        return properties
    }

}
