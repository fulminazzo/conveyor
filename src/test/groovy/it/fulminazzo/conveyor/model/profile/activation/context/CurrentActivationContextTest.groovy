package it.fulminazzo.conveyor.model.profile.activation.context

import it.fulminazzo.conveyor.model.properties.Properties
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

class CurrentActivationContextTest extends Specification {

    def 'test that #method returns #expected'() {
        given:
        def context = new CurrentActivationContext(Properties.newProperties([
                'project.basedir'  : TestUtils.BASE_DIR.absolutePath,
                'java.version'     : '17',
                'os.name'          : 'linux',
                'os.arch'          : 'x86_64',
                'os.version'       : '6.12',
                'project.packaging': 'jar'
        ]))

        when:
        def actual = context."$method"()

        then:
        actual == expected

        where:
        method          || expected
        'getProjectDir' || TestUtils.BASE_DIR.absoluteFile
        'getJdkVersion' || '17'
        'getOsName'     || 'linux'
        'getOsArch'     || 'x86_64'
        'getOsVersion'  || '6.12'
        'getPackaging'  || 'jar'
    }

}
