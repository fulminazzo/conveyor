package it.fulminazzo.conveyor.model.pom

import it.fulminazzo.conveyor.model.BuilderException
import it.fulminazzo.conveyor.model.XmlObjectBuilderUtils
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.dependency.RawDependency
import it.fulminazzo.conveyor.model.dependency.Scope
import it.fulminazzo.conveyor.model.metadata.DistributionManagement
import it.fulminazzo.conveyor.model.metadata.Plugin
import it.fulminazzo.conveyor.model.metadata.Reporting
import it.fulminazzo.conveyor.model.metadata.Resource
import it.fulminazzo.conveyor.model.pom.metadata.Build
import it.fulminazzo.conveyor.model.pom.metadata.Contributor
import it.fulminazzo.conveyor.model.pom.metadata.Developer
import it.fulminazzo.conveyor.model.pom.metadata.License
import it.fulminazzo.conveyor.model.pom.metadata.MailingList
import it.fulminazzo.conveyor.model.pom.metadata.Notifier
import it.fulminazzo.conveyor.model.pom.metadata.Organization
import it.fulminazzo.conveyor.model.pom.metadata.PomMetadata
import it.fulminazzo.conveyor.model.profile.Profile
import it.fulminazzo.conveyor.model.profile.activation.AndActivation
import it.fulminazzo.conveyor.model.profile.activation.BooleanActivation
import it.fulminazzo.conveyor.model.profile.activation.PropertyActivation
import it.fulminazzo.conveyor.model.profile.metadata.BuildBase
import it.fulminazzo.conveyor.model.profile.metadata.ProfileMetadata
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
                .metadata(PomMetadata.builder()
                        .modelVersion('4.0.0')
                        .name('Super Application Core')
                        .description('The core logic for the Super Application suite.')
                        .url('https://www.example.com/superapp')
                        .inceptionYear('2024')
                        .organization(Organization.builder()
                                .name('Example Corp')
                                .url('https://www.example.com')
                                .build())
                        .licenses([
                                License.builder()
                                        .name('Apache License, Version 2.0')
                                        .url('https://www.apache.org/licenses/LICENSE-2.0.txt')
                                        .distribution('repo')
                                        .comments('Apache license')
                                        .build()
                        ].toSet())
                        .developers([
                                Developer.builder()
                                        .id('jdoe')
                                        .name('John Doe')
                                        .email('jdoe@example.com')
                                        .roles(['Architect', 'Developer'])
                                        .timezone('America/New_York')
                                        .url('https://www.example.com')
                                        .organization('Example Corp')
                                        .organizationUrl('https://www.example.com')
                                        .properties(['hello': 'world'])
                                        .build()
                        ].toSet())
                        .contributors([
                                Contributor.builder()
                                        .name('Jane Smith')
                                        .email('jsmith@example.com')
                                        .url('https://github.com/jsmith')
                                        .organization('Freelance')
                                        .organizationUrl('https://www.jsmith.com')
                                        .roles(['Tester', 'Documentation'])
                                        .timezone('Europe/London')
                                        .properties(['contribution-type': 'UI/UX Design'])
                                        .build(),
                                Contributor.builder()
                                        .name('Mario Rossi')
                                        .roles(['Translator'])
                                        .timezone('Europe/Rome')
                                        .build()
                        ].toSet())
                        .mailingLists([
                                MailingList.builder()
                                        .name('SuperApp User List')
                                        .subscribe('users-subscribe@example.com')
                                        .unsubscribe('users-unsubscribe@example.com')
                                        .post('users@example.com')
                                        .archive('https://mail-archives.example.com/users/')
                                        .otherArchives(['https://www.mail-archive.com/users@example.com/'])
                                        .build(),
                                MailingList.builder()
                                        .name('SuperApp Developer List')
                                        .subscribe('dev-subscribe@example.com')
                                        .unsubscribe('dev-unsubscribe@example.com')
                                        .post('dev@example.com')
                                        .archive('https://mail-archives.example.com/dev/')
                                        .build()
                        ])
                        .prerequisites(new PomMetadata.Prerequisites('3.6.0'))
                        .scm(PomMetadata.SCManagement.builder()
                                .connection('scm:git:git://github.com/example/superapp.git')
                                .developerConnection('scm:git:ssh://github.com:example/superapp.git')
                                .url('https://github.com/example/superapp/tree/master')
                                .tag('HEAD')
                                .build())
                        .issueManagement(PomMetadata.IssueManagement.builder()
                                .system('JIRA')
                                .url('https://jira.example.com/browse/SUPERAPP')
                                .build())
                        .ciManagement(PomMetadata.CiManagement.builder()
                                .system('Jenkins')
                                .url('https://jenkins.example.com/job/superapp')
                                .notifiers([
                                        Notifier.builder()
                                                .type('mail')
                                                .sendOnError('true')
                                                .sendOnFailure('true')
                                                .sendOnSuccess('false')
                                                .sendOnWarning('false')
                                                .address('build-alerts@example.com')
                                                .configuration(['prefix': '[BUILD-REPORT]'])
                                                .build(),
                                        Notifier.builder()
                                                .type('irc')
                                                .sendOnFailure('true')
                                                .address('irc.freenode.net:6667 #superapp-devs')
                                                .build()
                                ])
                                .build())
                        .pluginRepositories([
                                RawRepository.builder()
                                        .id('central')
                                        .url('https://repo.maven.apache.org/maven2')
                                        .build()
                        ].toSet())
                        .reporting(Reporting.builder()
                                .excludeDefaults('false')
                                .outputDirectory('${project.basedir}/target')
                                .plugins([
                                        Plugin.builder()
                                                .groupId('org.apache.maven.plugins')
                                                .artifactId('maven-compiler-plugin')
                                                .version('3.10.1')
                                                .build()
                                ])
                                .build())
                        .build(Build.builder()
                                .sourceDirectory('${project.basedir}/src/main/java')
                                .scriptSourceDirectory('${project.basedir}/src/main/groovy')
                                .testSourceDirectory('${project.basedir}/src/test/java')
                                .outputDirectory('${project.basedir}/target/classes')
                                .testOutputDirectory('${project.basedir}/target/test/classes')
                                .finalName('${project.artifactId}-${project.version}')
                                .directory('${project.basedir}/target')
                                .filters(['**/*.xml'])
                                .extensions([
                                        new Artifact('it.fulminazzo', 'conveyor', '1.0')
                                ])
                                .resources([
                                        Resource.builder()
                                                .directory('src/main/resources')
                                                .filtering('true')
                                                .includes(['**/*.xml', '**/*.properties'])
                                                .excludes(['**/*.yml'])
                                                .build()
                                ])
                                .pluginManagement([
                                        Plugin.builder()
                                                .groupId('org.apache.maven.plugins')
                                                .artifactId('maven-compiler-plugin')
                                                .version('3.10.1')
                                                .extensions('conveyor')
                                                .build()
                                ].toSet())
                                .plugins([
                                        Plugin.builder()
                                                .groupId('org.apache.maven.plugins')
                                                .artifactId('maven-compiler-plugin')
                                                .build(),
                                        Plugin.builder()
                                                .groupId('org.apache.maven.plugins')
                                                .artifactId('maven-surefire-plugin')
                                                .version('3.0.0-M7')
                                                .build()
                                ])
                                .build())
                        .distributionManagement(DistributionManagement.builder()
                                .downloadUrl('https://example.com')
                                .status('verified')
                                .repository(RawRepository.builder()
                                        .id('internal-releases')
                                        .name('Internal Releases')
                                        .url('https://repo.example.com/releases')
                                        .build())
                                .snapshotRepository(RawRepository.builder()
                                        .id('internal-snapshots')
                                        .name('Internal Snapshots')
                                        .url('https://repo.example.com/snapshots')
                                        .build())
                                .site(DistributionManagement.Site.builder()
                                        .id('website')
                                        .name('Website name')
                                        .url('scp://www.example.com/www/docs/project/')
                                        .build())
                                .relocation(DistributionManagement.Relocation.builder()
                                        .groupId('it.fulminazzo')
                                        .artifactId('conveyor')
                                        .version('1.0')
                                        .message('Relocating to conveyor')
                                        .build())
                                .build())
                        .modules(['extra-module-for-prod'])
                        .build())
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
                                .metadata(ProfileMetadata.builder()
                                        .build(BuildBase.builder()
                                                .plugins([
                                                        Plugin.builder()
                                                                .groupId('com.github.wvengen')
                                                                .artifactId('proguard-maven-plugin')
                                                                .version('2.5.3')
                                                                .executions([
                                                                        Plugin.Execution.builder()
                                                                                .phase('package')
                                                                                .goals(['proguard'])
                                                                                .inherited('false')
                                                                                .build()
                                                                ])
                                                                .build()
                                                ])
                                                .build())
                                        .build())
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

    def 'test that parseDocument throws BuilderException on XmlParserException'() {
        given:
        def builder = newBuilder('')

        when:
        builder.build()

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
