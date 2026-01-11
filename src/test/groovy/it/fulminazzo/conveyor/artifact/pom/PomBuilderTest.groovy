package it.fulminazzo.conveyor.artifact.pom

import it.fulminazzo.conveyor.artifact.pom.repository.ChecksumPolicy
import it.fulminazzo.conveyor.artifact.pom.repository.Repository
import it.fulminazzo.conveyor.artifact.pom.repository.update.UpdatePolicy
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
                'hello'             : 'world',
                'dependency.version': '1.0',
                'dependency.name'   : 'conveyor',
                'name'              : '${dependency.name}'
        ]
    }

    def 'test that parseRepository returns correct repository'() {
        given:
        def builder = newBuilder("""
            <repository>
                <id>id</id>
                <name>name</name>
                <url>url</url>
                
                <layout>default</layout>
            
                <releases>
                    <enabled>true</enabled>
                    <updatePolicy>daily</updatePolicy>
                    <checksumPolicy>warn</checksumPolicy>
                </releases>
            
                <snapshots>
                    <enabled>true</enabled>
                    <updatePolicy>always</updatePolicy>
                    <checksumPolicy>fail</checksumPolicy>
                </snapshots>
            </repository>
        """)
        builder.reader.next()

        and:
        def expected = Repository.builder()
                .id('id')
                .name('name')
                .url('url')
                .releases(Repository.Policy.builder()
                        .enabled(true)
                        .updatePolicy(UpdatePolicy.of('daily'))
                        .checksumPolicy(ChecksumPolicy.WARN)
                        .build())
                .snapshots(Repository.Policy.builder()
                        .enabled(true)
                        .updatePolicy(UpdatePolicy.of('always'))
                        .checksumPolicy(ChecksumPolicy.FAIL)
                        .build())
                .build()

        when:
        def repository = builder.parseRepository()

        then:
        repository == expected
    }

    private static PomBuilder newBuilder(final String data) {
        return new PomBuilder(new ByteArrayInputStream(data.bytes))
    }

}
