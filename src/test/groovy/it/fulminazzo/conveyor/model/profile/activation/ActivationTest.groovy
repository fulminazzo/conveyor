package it.fulminazzo.conveyor.model.profile.activation

import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.pom.Pom
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext
import it.fulminazzo.conveyor.model.profile.activation.context.CurrentActivationContext
import it.fulminazzo.conveyor.model.properties.Properties
import spock.lang.Specification

class ActivationTest extends Specification {

    def 'test that JdkActivation isEnabled with #jdk of #osJdk returns #expected'() {
        given:
        def activation = new JdkActivation(jdk)
        def context = newContext(osJdk)

        expect:
        activation.isEnabled(context) == expected

        where:
        jdk                 || osJdk      || expected
        '1.8'               || '1.8'      || true
        '!1.8'              || '1.8'      || false
        '1.8'               || '1.8.0_08' || true
        '!1.8'              || '1.8.0_08' || false
        '1.8'               || '1.8.2.07' || true
        '!1.8'              || '1.8.2.07' || false
        '1.8'               || '17'       || false
        '!1.8'              || '17'       || true
        '(1.8,)'            || '17'       || true
        '!(1.8,)'           || '17'       || false
        '[1.8,)'            || '17'       || true
        '![1.8,)'           || '17'       || false
        '(1.8,)'            || '1.8'      || false
        '!(1.8,)'           || '1.8'      || true
        '[1.8,)'            || '1.8'      || true
        '![1.8,)'           || '1.8'      || false
        '(,17)'             || '1.8'      || true
        '!(,17)'            || '1.8'      || false
        '(,17]'             || '1.8'      || true
        '!(,17]'            || '1.8'      || false
        '(,17)'             || '17'       || false
        '!(,17)'            || '17'       || true
        '(,17]'             || '17'       || true
        '!(,17]'            || '17'       || false
        '(1.8,17]'          || '14'       || true
        '!(1.8,17]'         || '14'       || false
        '[1.8,17]'          || '14'       || true
        '![1.8,17]'         || '14'       || false
        '(1.8,17]'          || '1.8'      || false
        '!(1.8,17]'         || '1.8'      || true
        '[1.8,17]'          || '1.8'      || true
        '![1.8,17]'         || '1.8'      || false
        '(1.8,17]'          || '14'       || true
        '!(1.8,17]'         || '14'       || false
        '[1.8,17]'          || '14'       || true
        '![1.8,17]'         || '14'       || false
        '(1.8,17]'          || '1.8'      || false
        '!(1.8,17]'         || '1.8'      || true
        '[1.8,17]'          || '1.8'      || true
        '![1.8,17]'         || '1.8'      || false
        '[1.8,17)'          || '14'       || true
        '![1.8,17)'         || '14'       || false
        '[1.8,17]'          || '14'       || true
        '![1.8,17]'         || '14'       || false
        '[1.8,17)'          || '17'       || false
        '![1.8,17)'         || '17'       || true
        '[1.8,17]'          || '17'       || true
        '![1.8,17]'         || '17'       || false
        '!1.8'              || '1.8'      || false
        '!1.8'              || '17'       || true
        '!17'               || '1.8'      || true
        '!17'               || '17'       || false
        '(1.8.8,)'          || '1.8.8'    || false
        '!(1.8.8,)'         || '1.8.8'    || true
        '[1.8.8,)'          || '1.8.8'    || true
        '![1.8.8,)'         || '1.8.8'    || false
        '[1.8.8,)'          || '1.8.9'    || true
        '![1.8.8,)'         || '1.8.9'    || false
        '[1.8.8,)'          || '1.8.10'   || true
        '![1.8.8,)'         || '1.8.10'   || false
        '(1.8.9,)'          || '1.8.8'    || false
        '!(1.8.9,)'         || '1.8.8'    || true
        '[1.8.9,)'          || '1.8.8'    || false
        '![1.8.9,)'         || '1.8.8'    || true
        '[1.8.-SNAPSHOT,)'  || '1.8.8'    || true
        '![1.8.-SNAPSHOT,)' || '1.8.8'    || false
        '[1,)'              || '1.8.8'    || true
        '![1,)'             || '1.8.8'    || false
        '[1.8.-SNAPSHOT,)'  || '1.8'      || true
        '![1.8.-SNAPSHOT,)' || '1.8'      || false
    }

    def 'test that JdkActivation with range #range throws IllegalArgumentException'() {
        given:
        def activation = new JdkActivation(range)
        def context = newContext('17')

        when:
        activation.isEnabled(context)

        then:
        thrown(IllegalArgumentException)

        where:
        range << [
                '[1.8,',
                '[1.8,17',
                '[1.8|17]',
                '[1.8|17)',
                '[1.8]',
                '[1.8)',
                '(1.8,',
                '(1.8,17',
                '(1.8|17]',
                '(1.8|17)',
                '(1.8]',
                '(1.8)',
                '![1.8,',
                '![1.8,17',
                '![1.8|17]',
                '![1.8|17)',
                '![1.8]',
                '![1.8)',
                '!(1.8,',
                '!(1.8,17',
                '!(1.8|17]',
                '!(1.8|17)',
                '!(1.8]',
                '!(1.8)',
        ]
    }

    def 'test that OsActivation isEnabled with #name, #family, #arch and #version of #osName, #osArch and #osVersion returns #expected'() {
        given:
        def activation = new OsActivation(name, family, arch, version)
        def context = newContext(osName, osArch, osVersion)

        expect:
        activation.isEnabled(context) == expected

        where:
        name             | family    | arch      | version               || osName           | osArch   | osVersion || expected
        'Linux'          | 'unix'    | 'x86-64'  | '6.2'                 || 'Mac'            | 'x86-64' | '6.2.1'   || false
        'Linux'          | 'unix'    | 'arm'     | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || false
        'Linux'          | 'unix'    | 'x86-64'  | '5.6'                 || 'Linux'          | 'x86-64' | '6.2.1'   || false
        'Windows'        | 'windows' | 'x86-64'  | '7'                   || 'Windows 7'      | 'x86-64' | '7'       || true
        'dos'            | 'dos'     | 'x86-64'  | '1.0'                 || 'dos'            | 'x86-64' | '1.0'     || true
        'ms-dos'         | 'dos'     | 'x86-64'  | '1.0'                 || 'ms-dos'         | 'x86-64' | '1.0'     || true
        'os/2'           | 'os/2'    | 'x86-64'  | '1.0'                 || 'os/2'           | 'x86-64' | '1.0'     || true
        'netware'        | 'netware' | 'x86-64'  | '1.0'                 || 'netware'        | 'x86-64' | '1.0'     || true
        'os/400'         | 'os/400'  | 'x86-64'  | '1.0'                 || 'os/400'         | 'x86-64' | '1.0'     || true
        'z/os'           | 'z/os'    | 'x86-64'  | '1.0'                 || 'z/os'           | 'x86-64' | '1.0'     || true
        'os/390'         | 'z/os'    | 'x86-64'  | '1.0'                 || 'os/390'         | 'x86-64' | '1.0'     || true
        'nonstop-kernel' | 'tandem'  | 'x86-64'  | '1.0'                 || 'nonstop-kernel' | 'x86-64' | '1.0'     || true
        'openvms'        | 'openvms' | 'x86-64'  | '1.0'                 || 'openvms'        | 'x86-64' | '1.0'     || true
        'Unix'           | 'unix'    | 'arm'     | '1.0'                 || 'unix'           | 'arm'    | '1.0'     || true
        'Mac'            | 'unix'    | 'x86-64'  | '15'                  || 'Mac'            | 'x86-64' | '15.4'    || true
        'Mac'            | 'mac'     | 'x86-64'  | '15'                  || 'Mac'            | 'x86-64' | '15.4'    || true
        'Linux'          | 'unix'    | 'x86-64'  | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || true
        'Linux'          | 'linux'   | 'x86-64'  | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || true
        '!Linux'         | 'linux'   | 'x86-64'  | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || false
        'Linux'          | '!unix'   | 'x86-64'  | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || false
        'Linux'          | '!linux'  | 'x86-64'  | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || false
        'Linux'          | 'linux'   | '!x86-64' | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || false
        'Linux'          | 'linux'   | 'x86-64'  | '!6.2'                || 'Linux'          | 'x86-64' | '6.2.1'   || false
        null             | 'linux'   | 'x86-64'  | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || true
        'Linux'          | null      | 'x86-64'  | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || true
        null             | null      | 'x86-64'  | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || true
        'Linux'          | 'linux'   | null      | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || true
        'Linux'          | null      | null      | '6.2'                 || 'Linux'          | 'x86-64' | '6.2.1'   || true
        'Linux'          | 'linux'   | 'x86-64'  | null                  || 'Linux'          | 'x86-64' | '6.2.1'   || true
        'Linux'          | 'linux'   | null      | null                  || 'Linux'          | 'x86-64' | '6.2.1'   || true
        'Linux'          | null      | null      | null                  || 'Linux'          | 'x86-64' | '6.2.1'   || true
        null             | null      | null      | null                  || 'Linux'          | 'x86-64' | '6.2.1'   || true
        null             | null      | null      | 'regex:[0-9]\\.[0-9]' || 'Linux'          | 'x86-64' | '6.2'     || true
        null             | 'unix'    | null      | null                  || 'unknown'        | 'x86-64' | '1.0'     || true
        null             | 'unknown' | null      | null                  || 'unknown'        | 'x86-64' | '1.0'     || false
        '!Linux'         | null      | null      | null                  || 'Linux'          | 'x86-64' | '1.0'     || false
        '!Mac'           | null      | null      | null                  || 'Linux'          | 'x86-64' | '1.0'     || true
        null             | 'unix'    | null      | null                  || 'Windows'        | 'x86-64' | '10'      || false
    }

    def 'test that PropertyActivation isEnabled with #name and #value returns #expected'() {
        given:
        def properties = [
                'first' : 'true',
                'second': ''
        ]
        properties.each { System.setProperty(it.key, it.value) }

        and:
        def activation = new PropertyActivation(name, value)
        def context = newContext()

        expect:
        activation.isEnabled(context) == expected

        cleanup:
        properties.each { System.clearProperty(it.key) }

        where:
        name         | value   || expected
        '!first'     | null    || false
        'first'      | null    || true
        '!first'     | 'true'  || false
        '!first'     | '!true' || false
        '!first'     | ''      || false
        'first'      | '!true' || false
        'first'      | ''      || false
        'first'      | 'true'  || true
        '!second'    | null    || false
        'second'     | null    || true
        '!second'    | 'true'  || false
        '!second'    | '!true' || false
        '!second'    | ''      || false
        'second'     | '!true' || false
        'second'     | ''      || false
        'second'     | 'true'  || true
        '!third'     | null    || true
        'third'      | null    || true
        '!third'     | 'true'  || true
        '!third'     | '!true' || true
        '!third'     | ''      || true
        'third'      | '!true' || true
        'third'      | ''      || false
        'third'      | 'true'  || false
        '!packaging' | null    || false
        '!packaging' | '!jar'  || false
        '!packaging' | 'jar'   || false
        '!packaging' | 'war'   || false
        'packaging'  | null    || true
        'packaging'  | '!jar'  || false
        'packaging'  | 'jar'   || true
        'packaging'  | 'war'   || false
    }

    def 'test that FileActivation isEnabled with #exists and #missing returns #expected'() {
        given:
        def activation = new FileActivation(exists, missing)
        def context = newContext(new File(file))

        expect:
        activation.isEnabled(context) == expected

        where:
        file | exists                                 | missing                                || expected
        '.'  | 'invalid'                              | null                                   || false
        '.'  | 'invalid'                              | 'invalid'                              || false
        '.'  | null                                   | 'src'                                  || false
        '.'  | 'src'                                  | 'src'                                  || false
        '.'  | null                                   | null                                   || true
        '.'  | 'src'                                  | null                                   || true
        '.'  | null                                   | 'invalid'                              || true
        '.'  | 'src'                                  | 'invalid'                              || true
        '/'  | null                                   | '${user.home}'                         || false
        '/'  | '${user.home}'                         | null                                   || true
        '/'  | null                                   | '${env.SHELL}'                         || false
        '/'  | '${env.SHELL}'                         | null                                   || true
        '/'  | null                                   | '${basedir}'                           || false
        '/'  | '${basedir}'                           | null                                   || true
        '/'  | null                                   | '${project.basedir}'                   || false
        '/'  | '${project.basedir}'                   | null                                   || true
        '/'  | null                                   | '${maven.multiModuleProjectDirectory}' || false
        '/'  | '${maven.multiModuleProjectDirectory}' | null                                   || true
    }

    def 'test that BooleanActivation isEnabled with #expected returns #expected'() {
        given:
        def activation = new BooleanActivation(expected)
        def context = newContext()

        expect:
        activation.isEnabled(context) == expected

        where:
        expected << [true, false]
    }

    private ActivationContext newContext(final String osJdk) {
        def context = Mock(ActivationContext)
        context.jdkVersion >> osJdk
        return context
    }

    private ActivationContext newContext(final String osName, final String osArch, final String osVersion) {
        def context = Mock(ActivationContext)
        context.osName >> osName
        context.osArch >> osArch
        context.osVersion >> osVersion
        return context
    }

    private ActivationContext newContext() {
        return newContext(new File('.').absoluteFile)
    }

    private ActivationContext newContext(final File currentDir) {
        def artifact = Mock(Artifact)
        artifact.getFullPath(_) >> 'src'
        def pom = Pom.builder()
                .project(artifact)
                .packaging('jar')
                .build()
        def activation = Spy(CurrentActivationContext, constructorArgs: [Properties.newProjectProperties(pom, currentDir)])
        activation.projectDir >> currentDir
        return activation
    }

}
