package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.XmlObjectBuilderUtils
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.profile.Profile
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

    def 'test that parseProfiles returns correct profiles'() {
        given:
        def builder = newBuilder("""
            <profiles>
                <profile>
                    <id>first</id>
                    <activation></activation>
                </profile>
                <profile>
                    <id>second</id>
                    <activation></activation>
                </profile>
                <profile>
                    <id>third</id>
                    <activation></activation>
                </profile>
                <something>wrong</something>
            </profiles>
        """)
        XmlObjectBuilderUtils.getParser(builder).next()

        and:
        def expected = [newProfile('first'), newProfile('second'), newProfile('third')]

        when:
        builder.parseProfiles()

        and:
        def field = PomBuilder.getDeclaredField('profiles')
        field.accessible = true
        def profiles = field.get(builder)

        then:
        profiles.values().sort() == expected.sort()
    }

    private static Profile newProfile(final String id) {
        def data = "<profile><id>$id</id><activation></activation></profile>"
        def parser = newParser(data)
        parser.next()
        return Profile.builder(parser).build()
    }

    private static PomBuilder newBuilder(final String data) {
        return new PomBuilder(newParser(data))
    }

    private static XmlParser newParser(final String data) {
        def inputStream = new ByteArrayInputStream(data.bytes)
        return XmlParser.newParser(inputStream)
    }

}
