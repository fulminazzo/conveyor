package it.fulminazzo.conveyor.pom

import it.fulminazzo.conveyor.pom.artifact.Artifact
import it.fulminazzo.conveyor.pom.dependency.Dependency
import it.fulminazzo.conveyor.pom.repository.ChecksumPolicy
import it.fulminazzo.conveyor.pom.repository.Repository
import it.fulminazzo.conveyor.pom.repository.update.UpdatePolicy
import spock.lang.Specification

class PomBuilderTest extends Specification {

    def 'test that RuntimeException on build is replaced by ParserException'() {
        given:
        def builder = newBuilder("""
            <parent>
                <groupId>it.fulminazzo</groupId>
                <artifactId>parent</artifactId>
            </parent>
        """)
        builder.reader.next()

        when:
        builder.parseParent()

        then:
        def e = thrown(ParserException)
        e.cause != null
        e.cause.class == NullPointerException
    }

    def 'test that parseParent returns correct parent'() {
        given:
        def builder = newBuilder("""
            <parent>
                <groupId>it.fulminazzo</groupId>
                <artifactId>parent</artifactId>
                <version>1.0</version>
            </parent>
        """)
        builder.reader.next()

        when:
        def parent = builder.parseParent()

        then:
        parent == Artifact.builder()
                .groupId('it.fulminazzo')
                .artifactId('parent')
                .version('1.0')
                .build()
    }

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

    def 'test that parseRepositories returns correct repositories'() {
        given:
        def builder = newBuilder("""
            <repositories>
                <repository>
                    <id>first</id>
                    <url>first.com</url>
                </repository>
                <repository>
                    <id>second</id>
                    <url>second.net</url>
                </repository>
                <repository>
                    <id>third</id>
                    <url>third.it</url>
                </repository>
            </repositories>
        """)
        builder.reader.next()

        and:
        def expected = [
                Repository.builder().id('first').url('first.com').build(),
                Repository.builder().id('second').url('second.net').build(),
                Repository.builder().id('third').url('third.it').build()
        ]

        when:
        builder.parseRepositories()

        and:
        def field = PomBuilder.getDeclaredField('repositories')
        field.accessible = true
        def repositories = field.get(builder)

        then:
        repositories.sort() == expected.sort()
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

    def 'test that parseDependencyManagement returns correct dependencies'() {
        given:
        def builder = newBuilder("""
            <dependencyManagement>
                <dependency>
                    <groupId>it.fulminazzo</groupId>
                    <artifactId>dep1</artifactId>
                    <version>1.0</version>
                </dependency>
                <dependency>
                    <groupId>it.fulminazzo</groupId>
                    <artifactId>dep2</artifactId>
                    <version>1.0</version>
                </dependency>
                <dependency>
                    <groupId>it.fulminazzo</groupId>
                    <artifactId>dep3</artifactId>
                    <version>1.0</version>
                </dependency>
            </dependencyManagement>
        """)
        builder.reader.next()

        and:
        def expected = [
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep1').version('1.0').build(),
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep2').version('1.0').build(),
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep3').version('1.0').build()
        ]

        when:
        builder.parseDependencyManagement()

        and:
        def field = PomBuilder.getDeclaredField('dependencyManagement')
        field.accessible = true
        def dependencies = field.get(builder)

        then:
        dependencies.sort() == expected.sort()
    }

    def 'test that parseDependencies returns correct dependencies'() {
        given:
        def builder = newBuilder("""
            <dependencies>
                <dependency>
                    <groupId>it.fulminazzo</groupId>
                    <artifactId>dep1</artifactId>
                    <version>1.0</version>
                </dependency>
                <dependency>
                    <groupId>it.fulminazzo</groupId>
                    <artifactId>dep2</artifactId>
                    <version>1.0</version>
                </dependency>
                <dependency>
                    <groupId>it.fulminazzo</groupId>
                    <artifactId>dep3</artifactId>
                    <version>1.0</version>
                </dependency>
            </dependencies>
        """)
        builder.reader.next()

        and:
        def expected = [
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep1').version('1.0').build(),
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep2').version('1.0').build(),
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep3').version('1.0').build()
        ]

        when:
        builder.parseDependencies()

        and:
        def field = PomBuilder.getDeclaredField('dependencies')
        field.accessible = true
        def dependencies = field.get(builder)

        then:
        dependencies.sort() == expected.sort()
    }

    def 'test that parseDependency returns correct dependency'() {
        given:
        def builder = newBuilder("""
            <dependency>
                <groupId>it.fulminazzo</groupId>
                <artifactId>conveyor</artifactId>
                <version>1.0</version>
            
                <type>war</type> 
                
                <classifier>sources</classifier>
            
                <scope>provided</scope>
            
                <optional>true</optional>
            
                <systemPath>\${project.basedir}/libs/custom-lib.jar</systemPath>
            
                <exclusions>
                    <exclusion>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                    </exclusion>
                </exclusions>
            </dependency>
        """)
        builder.reader.next()

        and:
        def expected = Dependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('conveyor')
                .version('1.0')
                .type('war')
                .classifier('sources')
                .scope(Dependency.Scope.PROVIDED)
                .optional(true)
                .build()
        expected.exclusions.add('org.projectlombok', 'lombok')

        when:
        def dependency = builder.parseDependency()

        then:
        dependency == expected
    }

    private static PomBuilder newBuilder(final String data) {
        return new PomBuilder(new ByteArrayInputStream(data.bytes))
    }

}
