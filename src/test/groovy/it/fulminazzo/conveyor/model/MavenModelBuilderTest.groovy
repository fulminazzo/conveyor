package it.fulminazzo.conveyor.model

import it.fulminazzo.conveyor.model.dependency.RawDependency
import it.fulminazzo.conveyor.model.dependency.Scope
import it.fulminazzo.conveyor.model.repository.ChecksumPolicy
import it.fulminazzo.conveyor.model.repository.RawRepository
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
                <empty />
            </properties>
        """)
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def properties = builder.parseProperties()

        then:
        properties == [
                'hello'             : 'world',
                'dependency.version': '1.0',
                'dependency.name'   : 'conveyor',
                'name'              : '${dependency.name}',
                'empty'             : ''
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
                RawRepository.builder().id('first').url('first.com').build(),
                RawRepository.builder().id('second').url('second.net').build(),
                RawRepository.builder().id('third').url('third.it').build()
        ]

        when:
        def repositories = builder.parseRepositories()

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
        def expected = RawRepository.builder()
                .id('id')
                .name('name')
                .url('url')
                .releases(RawRepository.Policy.builder()
                        .enabled('true')
                        .updatePolicy('daily')
                        .checksumPolicy(ChecksumPolicy.WARN.value())
                        .build())
                .snapshots(RawRepository.Policy.builder()
                        .enabled('true')
                        .updatePolicy('always')
                        .checksumPolicy(ChecksumPolicy.FAIL.value())
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
            </dependencyManagement>
        """)
        XmlObjectBuilderUtils.getParser(builder).next()

        and:
        def expected = [
                RawDependency.builder().groupId('it.fulminazzo').artifactId('dep1').version('1.0').build(),
                RawDependency.builder().groupId('it.fulminazzo').artifactId('dep2').version('1.0').build(),
                RawDependency.builder().groupId('it.fulminazzo').artifactId('dep3').version('1.0').build()
        ]

        when:
        def dependencyManagement = builder.parseDependencyManagement()

        then:
        dependencyManagement.sort() == expected.sort()
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
                RawDependency.builder().groupId('it.fulminazzo').artifactId('dep1').version('1.0').build(),
                RawDependency.builder().groupId('it.fulminazzo').artifactId('dep2').version('1.0').build(),
                RawDependency.builder().groupId('it.fulminazzo').artifactId('dep3').version('1.0').build()
        ]

        when:
        def dependencies = builder.parseDependencies()

        then:
        dependencies == expected
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
        def expected = RawDependency.builder()
                .groupId('it.fulminazzo')
                .artifactId('conveyor')
                .version('1.0')
                .type('war')
                .classifier('sources')
                .scope(Scope.PROVIDED.value())
                .optional(true.toString())
                .build()
        expected.exclusions.add('org.projectlombok', 'lombok')

        when:
        def dependency = builder.parseDependency()

        then:
        dependency == expected
    }

}
