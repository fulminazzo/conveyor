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

    private SystemProperties mockSystemProperties(final String key, final String value) {
        def properties = Mock(SystemProperties)
        properties.get(_) >> { a ->
            return a[0] == key ? value : null
        }
        return properties
    }

}
