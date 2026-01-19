package it.fulminazzo.conveyor.model.profile

import it.fulminazzo.conveyor.model.dependency.RawDependency
import it.fulminazzo.conveyor.model.dependency.Scope
import it.fulminazzo.conveyor.model.metadata.DistributionManagement
import it.fulminazzo.conveyor.model.metadata.Plugin
import it.fulminazzo.conveyor.model.metadata.Reporting
import it.fulminazzo.conveyor.model.metadata.Resource
import it.fulminazzo.conveyor.model.profile.activation.Activation
import it.fulminazzo.conveyor.model.profile.metadata.BuildBase
import it.fulminazzo.conveyor.model.profile.metadata.ProfileMetadata
import it.fulminazzo.conveyor.model.repository.RawRepository
import it.fulminazzo.conveyor.xml.XmlParser
import spock.lang.Specification

class XmlProfileBuilderTest extends Specification {
    static final String ACTIVATION = """
    <activation>
        <activeByDefault>false</activeByDefault>
        <jdk>[11,17)</jdk>
        <os>
            <name>linux</name>
            <family>unix</family>
            <arch>amd64</arch>
            <version>5.15.0-generic</version>
        </os>
        <property>
            <name>env</name>
            <value>!dev</value>
        </property>
        <file>
            <exists>\${project.basedir}/src/main/resources/prod.properties</exists>
            <missing>\${project.basedir}/STAGING_LOCK</missing>
        </file>
    </activation>
    """

    def 'test that build returns correct profile'() {
        given:
        def inputStream = new ByteArrayInputStream(ACTIVATION.bytes)
        def parser = XmlParser.newParser(inputStream)
        parser.next()
        def expectedActivation = Activation.builder(parser).build()

        and:
        def expected = Profile.builder()
                .id('complete-profile')
                .activation(expectedActivation)
                .properties([
                        'maven.test.skip': 'true',
                        'config.api.url' : 'https://api.project.it'
                ])
                .repositories(Set.copyOf([
                        RawRepository.builder()
                                .id('enterprise-repo')
                                .url('https://nexus.company.it/repository/maven-public/')
                                .releases(RawRepository.Policy.builder().enabled('true').build())
                                .snapshots(RawRepository.Policy.builder().enabled('false').build())
                                .build()
                ]))
                .dependencyManagement(Set.copyOf([
                        RawDependency.builder()
                                .groupId('org.springframework.cloud')
                                .artifactId('spring-cloud-dependencies')
                                .version('2021.0.3')
                                .type('pom')
                                .scope(Scope.IMPORT.value())
                                .build()
                ]))
                .dependencies(List.copyOf([
                        RawDependency.builder()
                                .groupId('org.postgresql')
                                .artifactId('postgresql')
                                .version('42.5.0')
                                .build()
                ]))
                .metadata(ProfileMetadata.builder()
                        .modules(['extra-module-for-prod'])
                        .pluginRepositories([
                                RawRepository.builder()
                                        .id('enterprise-plugin-repo')
                                        .url('https://nexus.company.it/repository/maven-plugins/')
                                        .build()
                        ].toSet())
                        .distributionManagement(DistributionManagement.builder()
                                .repository(RawRepository.builder()
                                        .id('prod-release')
                                        .url('https://nexus.company.it/repository/maven-releases/')
                                        .build())
                                .build())
                        .reporting(Reporting.builder()
                                .plugins([Plugin.builder()
                                                  .groupId('org.apache.maven.plugins')
                                                  .artifactId('maven-javadoc-plugin')
                                                  .version('3.4.1')
                                                  .build()
                                ])
                                .build())
                        .build(BuildBase.builder()
                                .defaultGoal('install')
                                .finalName('${project.artifactId}-${project.version}-PROD')
                                .directory('target/production-build')
                                .plugins([
                                        Plugin.builder()
                                                .groupId('org.apache.maven.plugins')
                                                .artifactId('maven-compiler-plugin')
                                                .build()
                                ])
                                .resources([
                                        Resource.builder()
                                                .directory('src/main/resources-prod')
                                                .filtering('true')
                                                .build()
                                ])
                                .testResources([
                                        Resource.builder()
                                                .directory('src/main/resources-prod')
                                                .filtering('true')
                                                .build()
                                ])
                                .filters(['null'])
                                .build())
                        .build())
                .build()

        and:
        def file = new File('build/resources/test/profile.xml')
        parser = XmlParser.newParser(file.newInputStream())
        parser.next()

        and:
        def builder = new XmlProfileBuilder(parser)

        when:
        def actual = builder.build()

        then:
        actual == expected
    }

}
