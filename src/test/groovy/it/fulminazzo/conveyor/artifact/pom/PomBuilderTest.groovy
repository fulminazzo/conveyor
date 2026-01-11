package it.fulminazzo.conveyor.artifact.pom

import spock.lang.Specification

class PomBuilderTest extends Specification {

    def 'test that parseProperties returns correct properties'() {
        given:
        def builder = newBuilder("""
            <properties>
                <hello>world</hello>
                <dependency.version>1.0</dependency.version>
                <dependency.name>conveyor</dependency.name>
                <name>\${dependency.name}</name>
            </properties>
        """)
        builder.reader.next()

        when:
        builder.parseProperties()

        and:
        def field = PomBuilder.getDeclaredField('properties')
        field.accessible = true
        def properties = field.get(builder)

        then:
        properties == [
                'hello': 'world',
                'dependency.version': '1.0',
                'dependency.name': 'conveyor',
                'name': '${dependency.name}'
        ]
    }

    private static PomBuilder newBuilder(final String data) {
        return new PomBuilder(new ByteArrayInputStream(data.bytes))
    }

}
