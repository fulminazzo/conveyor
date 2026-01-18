package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.XmlObjectBuilderUtils
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.dependency.RawDependency
import it.fulminazzo.conveyor.model.dependency.Scope
import it.fulminazzo.conveyor.model.profile.Profile
import it.fulminazzo.conveyor.model.profile.activation.AndActivation
import it.fulminazzo.conveyor.model.profile.activation.BooleanActivation
import it.fulminazzo.conveyor.model.profile.activation.PropertyActivation
import it.fulminazzo.conveyor.model.repository.ChecksumPolicy
import it.fulminazzo.conveyor.model.repository.RawRepository
import it.fulminazzo.conveyor.util.TestUtils
import it.fulminazzo.conveyor.xml.XmlParser
import spock.lang.Specification

class XmlPomBuilderTest extends Specification {

    def 'test that build returns correct profile'() {
        given:
        def expected = Pom.builder()
                .project(new Artifact(
                        'com.example.superapp',
                        'super-app-core',
                        '1.0.0-SNAPSHOT',
                ))
                .name('Super Application Core')
                .description('The core logic for the Super Application suite.')
                .url('https://www.example.com/superapp')
                .packaging('war')
                .parent(new Artifact(
                        'com.example.superapp',
                        'super-app-parent',
                        '1.0.0-SNAPSHOT',
                ))
                .profiles(Set.copyOf([
                        Profile.builder()
                                .id('development')
                                .activation(new AndActivation()
                                        .addActivation('activeByDefault', new BooleanActivation(true))
                                        .addActivation('property', new PropertyActivation('env', 'dev'))
                                )
                                .properties([
                                        'db.url': 'jdbc:mysql://localhost:3306/dev_db'
                                ])
                                .build(),
                        Profile.builder()
                                .id('production')
                                .activation(new AndActivation()
                                        .addActivation('property', new PropertyActivation('env', 'prod'))
                                )
                                .properties([
                                        'db.url': 'jdbc:mysql://prod-db:3306/prod_db'
                                ])
                                .build()
                ]))
                .properties([
                        'project.build.sourceEncoding': 'UTF-8',
                        'java.version'                : '17',
                        'spring.version'              : '6.0.0',
                        'junit.version'               : '5.9.2'
                ])
                .repositories(Set.copyOf([
                        RawRepository.builder()
                                .id('central')
                                .name('Central Repository')
                                .url('https://repo.maven.apache.org/maven2')
                                .snapshots(RawRepository.Policy.builder().enabled('false').build())
                                .build(),
                        RawRepository.builder()
                                .id('internal-repo')
                                .name('Internal Company Repository')
                                .url('https://repo.example.com/releases')
                                .releases(
                                        RawRepository.Policy.builder()
                                                .enabled('true')
                                                .updatePolicy('always')
                                                .checksumPolicy(ChecksumPolicy.WARN.value())
                                                .build()
                                )
                                .snapshots(RawRepository.Policy.builder().enabled('false').build())
                                .build(),
                        RawRepository.builder()
                                .id('internal-snapshots')
                                .name('Internal Company Snapshots')
                                .url('https://repo.example.com/snapshots')
                                .releases(
                                        RawRepository.Policy.builder().enabled('false').build()
                                )
                                .snapshots(
                                        RawRepository.Policy.builder()
                                                .enabled('true')
                                                .updatePolicy('daily')
                                                .build()
                                )
                                .build()
                ]))
                .dependencyManagement(Set.copyOf([
                        RawDependency.builder()
                                .groupId('org.springframework')
                                .artifactId('spring-core')
                                .version('${spring.version}')
                                .build()
                ]))
                .dependencies(List.copyOf([
                        RawDependency.builder()
                                .groupId('org.springframework')
                                .artifactId('spring-context')
                                .version('${spring.version}')
                                .build(),
                        RawDependency.builder()
                                .groupId('org.junit.jupiter')
                                .artifactId('junit-jupiter-api')
                                .version('${junit.version}')
                                .scope(Scope.TEST.value())
                                .build(),
                        RawDependency.builder()
                                .groupId('javax.servlet')
                                .artifactId('javax.servlet-api')
                                .version('4.0.1')
                                .scope(Scope.PROVIDED.value())
                                .build(),
                ]))
                .build()

        and:
        def file = new File('build/resources/test/pom.xml')
        def parser = XmlParser.newParser(file.newInputStream())

        and:
        def builder = new XmlPomBuilder(parser)

        when:
        def actual = builder.build()

        then:
        actual == expected
    }

    def 'test that build gets groupId and version from parent if missing'() {
        given:
        def builder = newBuilder("""
            <project>
                <parent>
                    <groupId>it.fulminazzo</groupId>
                    <artifactId>parent</artifactId>
                    <version>1.0</version>
                </parent>
                <artifactId>conveyor</artifactId>
            </project>
        """)

        when:
        def project = builder.build().project

        then:
        project.groupId == 'it.fulminazzo'
        project.artifactId == 'conveyor'
        project.version == '1.0'
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
        def expected = [
                Profile.builder()
                        .id('first')
                        .activation(new AndActivation())
                        .build(),
                Profile.builder()
                        .id('second')
                        .activation(new AndActivation())
                        .build(),
                Profile.builder()
                        .id('third')
                        .activation(new AndActivation())
                        .build()
        ]

        when:
        builder.parseProfiles()

        and:
        def field = XmlPomBuilder.getDeclaredField('profiles')
        field.accessible = true
        def profiles = field.get(builder)

        then:
        profiles.values().sort() == expected.sort()
    }

    /**
     * INTEGRATION TESTS
     */

    def 'test that build does not throw on commons-parent-81.pom'() {
        given:
        def file = new File(TestUtils.BASE_DIR, 'commons-parent-81.pom')

        and:
        def builder = new XmlPomBuilder(XmlParser.newParser(file.newInputStream()))

        when:
        builder.build()

        then:
        noExceptionThrown()
    }

    private static XmlPomBuilder newBuilder(final String data) {
        return new XmlPomBuilder(newParser(data))
    }

    private static XmlParser newParser(final String data) {
        def inputStream = new ByteArrayInputStream(data.bytes)
        return XmlParser.newParser(inputStream)
    }

}
