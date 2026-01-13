package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.BuilderException
import it.fulminazzo.conveyor.model.XmlObjectBuilderUtils
import it.fulminazzo.conveyor.model.artifact.Artifact
import spock.lang.Specification

class PomBuilderTest extends Specification {

    def 'test that PomBuilder throws BuilderException on initialization error'() {
        given:
        def mockStream = Mock(InputStream)
        mockStream.read() >> {
            throw new IOException('Test exception')
        }

        when:
        PomBuilder.of(mockStream)

        then:
        thrown(BuilderException)
    }

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
        return PomBuilder.of(new ByteArrayInputStream(data.bytes))
    }

}
