package it.fulminazzo.conveyor.model


import it.fulminazzo.conveyor.pom.dependency.Dependency
import it.fulminazzo.conveyor.pom.repository.ChecksumPolicy
import it.fulminazzo.conveyor.pom.repository.Repository
import it.fulminazzo.conveyor.pom.repository.update.UpdatePolicy
import spock.lang.Specification

class MavenModelBuilderTest extends Specification {

    def 'test that parseProperties returns correct properties'() {
        given:
        def builder = MockMavenModelBuilder.newBuilder("""
            <properties>
                <hello>world</hello>
                <dependency.version>1.0</dependency.version>
                <dependency.name>conveyor</dependency.name>
                <name>\${dependency.name}</name>
            </properties>
        """)
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        builder.parseProperties()

        and:
        def field = MavenModelBuilder.getDeclaredField('properties')
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
        def builder = MockMavenModelBuilder.newBuilder("""
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
                <something>wrong</something>
            </repositories>
        """)
        XmlObjectBuilderUtils.getParser(builder).next()

        and:
        def expected = [
                Repository.builder().id('first').url('first.com').build(),
                Repository.builder().id('second').url('second.net').build(),
                Repository.builder().id('third').url('third.it').build()
        ]

        when:
        builder.parseRepositories()

        and:
        def field = MavenModelBuilder.getDeclaredField('repositories')
        field.accessible = true
        def repositories = field.get(builder)

        then:
        repositories.sort() == expected.sort()
    }

    def 'test that parseRepository returns correct repository'() {
        given:
        def builder = MockMavenModelBuilder.newBuilder("""
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
                    <something>wrong</something>
                </snapshots>
               
                <something>wrong</something>
            </repository>
        """)
        XmlObjectBuilderUtils.getParser(builder).next()

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
        def builder = MockMavenModelBuilder.newBuilder("""
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
                <something>wrong</something>
            </dependencyManagement>
        """)
        XmlObjectBuilderUtils.getParser(builder).next()

        and:
        def expected = [
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep1').version('1.0').build(),
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep2').version('1.0').build(),
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep3').version('1.0').build()
        ]

        when:
        builder.parseDependencyManagement()

        and:
        def field = MavenModelBuilder.getDeclaredField('dependencyManagement')
        field.accessible = true
        def dependencies = field.get(builder)

        then:
        dependencies.sort() == expected.sort()
    }

    def 'test that parseDependencies returns correct dependencies'() {
        given:
        def builder = MockMavenModelBuilder.newBuilder("""
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
                <something>wrong</something>
            </dependencies>
        """)
        XmlObjectBuilderUtils.getParser(builder).next()

        and:
        def expected = [
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep1').version('1.0').build(),
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep2').version('1.0').build(),
                Dependency.builder().groupId('it.fulminazzo').artifactId('dep3').version('1.0').build()
        ]

        when:
        builder.parseDependencies()

        and:
        def field = MavenModelBuilder.getDeclaredField('dependencies')
        field.accessible = true
        def dependencies = field.get(builder)

        then:
        dependencies.sort() == expected.sort()
    }

    def 'test that parseDependency returns correct dependency'() {
        given:
        def builder = MockMavenModelBuilder.newBuilder("""
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
                        <something>wrong</something>
                    </exclusion>
                    <something>wrong</something>
                </exclusions>
                
                <something>wrong</something>
            </dependency>
        """)
        XmlObjectBuilderUtils.getParser(builder).next()

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

}
