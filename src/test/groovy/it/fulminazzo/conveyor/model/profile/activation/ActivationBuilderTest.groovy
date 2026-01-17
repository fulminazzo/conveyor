package it.fulminazzo.conveyor.model.profile.activation

import it.fulminazzo.conveyor.model.XmlObjectBuilderUtils
import it.fulminazzo.conveyor.xml.XmlParser
import spock.lang.Specification

class ActivationBuilderTest extends Specification {

    def 'test build of complete activation'() {
        given:
        def data = """
            <activation>
                <activeByDefault>true</activeByDefault>
                <jdk>[1.8,17)</jdk>
                <os>
                    <name>name</name>
                    <family>family</family>
                    <arch>arch</arch>
                    <version>version</version>
                </os>
                <property>
                    <name>name</name>
                    <value>value</value>
                </property>
                <file>
                    <missing>missing</missing>
                    <exists>exists</exists>
                </file>
            </activation>"""

        and:
        def expected = new AndActivation()
        expected.addActivation('activeByDefault', new BooleanActivation(true))
        expected.addActivation('jdk', new JdkActivation('[1.8,17)'))
        expected.addActivation('os', OsActivation.builder()
                .name('name')
                .family('family')
                .arch('arch')
                .version('version')
                .build())
        expected.addActivation('property', PropertyActivation.builder()
                .name('name')
                .value('value')
                .build())
        expected.addActivation('file', FileActivation.builder()
                .missing('missing')
                .exists('exists')
                .build())

        and:
        def builder = newBuilder(data)
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def activation = builder.build()

        then:
        activation == expected
    }

    def 'test parseActiveByDefault of #data returns #expected'() {
        given:
        def builder = newBuilder(data)
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def actual = builder.parseActiveByDefault()

        then:
        actual == expected

        where:
        expected                     || data
        new BooleanActivation(true)  || '<activeByDefault>true</activeByDefault>'
        new BooleanActivation(false) || '<activeByDefault>false</activeByDefault>'
    }

    def 'test parseJdk of #data returns #expected'() {
        given:
        def builder = newBuilder(data)
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def actual = builder.parseJdk()

        then:
        actual == expected

        where:
        expected                      || data
        new JdkActivation('1.8')       | '<jdk>1.8</jdk>'
        new JdkActivation('17')        | '<jdk>17</jdk>'
        new JdkActivation('(1.8,)')    | '<jdk>(1.8,)</jdk>'
        new JdkActivation('[1.8,)')    | '<jdk>[1.8,)</jdk>'
        new JdkActivation('(,17)')     | '<jdk>(,17)</jdk>'
        new JdkActivation('(,17]')     | '<jdk>(,17]</jdk>'
        new JdkActivation('(1.8,17)')  | '<jdk>(1.8,17)</jdk>'
        new JdkActivation('[1.8,17)')  | '<jdk>[1.8,17)</jdk>'
        new JdkActivation('(1.8,17]')  | '<jdk>(1.8,17]</jdk>'
        new JdkActivation('[1.8,17]')  | '<jdk>[1.8,17]</jdk>'
        new JdkActivation('!1.8')      | '<jdk>!1.8</jdk>'
        new JdkActivation('!17')       | '<jdk>!17</jdk>'
        new JdkActivation('!(1.8,)')   | '<jdk>!(1.8,)</jdk>'
        new JdkActivation('![1.8,)')   | '<jdk>![1.8,)</jdk>'
        new JdkActivation('!(,17)')    | '<jdk>!(,17)</jdk>'
        new JdkActivation('!(,17]')    | '<jdk>!(,17]</jdk>'
        new JdkActivation('!(1.8,17)') | '<jdk>!(1.8,17)</jdk>'
        new JdkActivation('![1.8,17)') | '<jdk>![1.8,17)</jdk>'
        new JdkActivation('!(1.8,17]') | '<jdk>!(1.8,17]</jdk>'
        new JdkActivation('![1.8,17]') | '<jdk>![1.8,17]</jdk>'
    }

    def 'test parseOs of #data returns #expected'() {
        given:
        def builder = newBuilder(data)
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def actual = builder.parseOs()

        then:
        actual == expected

        where:
        expected                                          || data
        OsActivation.builder().build()                    || '<os></os>'
        OsActivation.builder().name('name').build()       || '<os><name>name</name></os>'
        OsActivation.builder().family('family').build()   || '<os><family>family</family></os>'
        OsActivation.builder().arch('arch').build()       || '<os><arch>arch</arch></os>'
        OsActivation.builder().version('version').build() || '<os><version>version</version></os>'
        OsActivation.builder().name('name').family('family')
                .arch('arch').version('version').build()  ||
                '<os><name>name</name><family>family</family><arch>arch</arch><version>version</version></os>'
    }

    def 'test parseProperty of #data returns #expected'() {
        given:
        def builder = newBuilder(data)
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def actual = builder.parseProperty()

        then:
        actual == expected

        where:
        expected                                                         || data
        PropertyActivation.builder().build()                             || '<property></property>'
        PropertyActivation.builder().name('name').build()                || '<property><name>name</name></property>'
        PropertyActivation.builder().value('value').build()              || '<property><value>value</value></property>'
        PropertyActivation.builder().name('name').value('value').build() || '<property><name>name</name><value>value</value></property>'
    }

    def 'test parseFile of #data returns #expected'() {
        given:
        def builder = newBuilder(data)
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def actual = builder.parseFile()

        then:
        actual == expected

        where:
        expected                                                             || data
        FileActivation.builder().build()                                     || '<file></file>'
        FileActivation.builder().missing('missing').build()                  || '<file><missing>missing</missing></file>'
        FileActivation.builder().exists('exists').build()                    || '<file><exists>exists</exists></file>'
        FileActivation.builder().missing('missing').exists('exists').build() || '<file><missing>missing</missing><exists>exists</exists></file>'
    }

    private static ActivationBuilder newBuilder(final String data) {
        def stream = new ByteArrayInputStream(data.bytes)
        def parser = XmlParser.newParser(stream)
        return new ActivationBuilder(parser)
    }

}
