package it.fulminazzo.conveyor.model.properties

import spock.lang.Specification

class OSMavenPropertiesTest extends Specification {

    def 'test that os-detected-name property with #value returns #expected'() {
        given:
        def mavenProperties = OSMavenProperties
                .builder(mockSystemProperties('os.name', value))
                .build()

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
        def mavenProperties = OSMavenProperties
                .builder(mockSystemProperties('os.arch', value))
                .build()

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

    def 'test that os-detected-bitness property with #value returns #expected'() {
        given:
        def mavenProperties = OSMavenProperties.builder(mockSystemProperties(
                'os.arch', value,
                'sun.arch.data.model', sunArch,
                'com.ibm.vm.bitmode', ibmBitMode
        )).build()

        when:
        def result = mavenProperties.get('os.detected.bitness')

        then:
        result == expected

        where:
        sunArch | ibmBitMode | value   || expected
        '0'     | null       | null    || '0'
        '1'     | null       | null    || '1'
        '2'     | null       | null    || '2'
        '3'     | null       | null    || '3'
        '4'     | null       | null    || '4'
        '5'     | null       | null    || '5'
        '6'     | null       | null    || '6'
        '7'     | null       | null    || '7'
        '8'     | null       | null    || '8'
        '9'     | null       | null    || '9'
        '10'    | null       | null    || '10'
        '16'    | null       | null    || '16'
        '32'    | null       | null    || '32'
        '64'    | null       | null    || '64'
        null    | '0'        | null    || '0'
        null    | '1'        | null    || '1'
        null    | '2'        | null    || '2'
        null    | '3'        | null    || '3'
        null    | '4'        | null    || '4'
        null    | '5'        | null    || '5'
        null    | '6'        | null    || '6'
        null    | '7'        | null    || '7'
        null    | '8'        | null    || '8'
        null    | '9'        | null    || '9'
        null    | '10'       | null    || '10'
        null    | '16'       | null    || '16'
        null    | '32'       | null    || '32'
        null    | '64'       | null    || '64'
        null    | null       | 'x8664' || '64'
        null    | null       | 'x8632' || '32'
        null    | null       | null    || '32'
        'a'     | null       | null    || '32'
        null    | 'a'        | null    || '32'
    }

    def 'test that os-detected-version property with #value returns #expected'() {
        given:
        def mavenProperties = OSMavenProperties
                .builder(mockSystemProperties('os.version', value))
                .build()

        expect:
        mavenProperties.get('os.detected.version') == expected

        and:
        mavenProperties.get('os.detected.version.major') == expectedMajor

        and:
        mavenProperties.get('os.detected.version.minor') == expectedMinor

        where:
        value           || expected | expectedMajor | expectedMinor
        null            || null     | null          | null
        'SNAPSHOT'      || null     | null          | null
        '1.10'          || '1.10'   | '1'           | '10'
        '1.10-SNAPSHOT' || '1.10'   | '1'           | '10'
    }

    private Properties mockSystemProperties(final String... properties) {
        def propertiesObject = Mock(Properties)
        propertiesObject.get(_) >> { a ->
            String key = a[0]
            for (def i = 0; i < properties.length; i += 2) {
                if (properties[i] == key) return properties[i + 1]
            }
            return null
        }
        return propertiesObject
    }

}
