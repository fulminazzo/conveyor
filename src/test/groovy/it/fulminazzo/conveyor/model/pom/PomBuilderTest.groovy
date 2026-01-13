package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.XmlObjectBuilderUtils
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.xml.XmlParser
import spock.lang.Specification

class PomBuilderTest extends Specification {

    def 'test that parseParent returns correct parent'() {
        given:
        def builder = newBuilder("""
            <parent>
                <groupId>it.fulminazzo</groupId>
                <artifactId>parent</artifactId>
                <version>1.0</version>
                <classifier>sources</classifier>
                <relativePath>../parent/pom.xml</relativePath>
            </parent>
        """)
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def parent = builder.parseParent()

        then:
        parent == Artifact.builder()
                .groupId('it.fulminazzo')
                .artifactId('parent')
                .version('1.0')
                .classifier('sources')
                .build()
    }

    private static PomBuilder newBuilder(final String data) {
        def inputStream = new ByteArrayInputStream(data.bytes)
        def parser = XmlParser.newParser(inputStream)
        return new PomBuilder(parser)
    }

}
