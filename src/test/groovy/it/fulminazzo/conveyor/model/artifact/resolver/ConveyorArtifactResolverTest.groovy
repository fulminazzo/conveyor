package it.fulminazzo.conveyor.model.artifact.resolver

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.manager.RepositoryManager
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.artifact.resolver.mode.ArtifactResolverMode
import it.fulminazzo.conveyor.model.properties.Properties
import it.fulminazzo.conveyor.model.repository.Repository
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class ConveyorArtifactResolverTest extends Specification {
    private static final File workDir = new File(TestUtils.BASE_DIR, 'artifact_resolver/conveyor')

    private RepositoryManager repositoryManager

    private ArtifactResolver resolver

    void setup() {
        this.repositoryManager = RepositoryManager.newManager(log)
                .addRepositories(Repository.builder().id('main').url(TestUtils.MAVEN_CENTRAL_URL).build())

        this.resolver = ConveyorArtifactResolver.newResolver(this.repositoryManager, workDir, log)
                .setMode(ArtifactResolverMode.CHECKSUM)
    }

    def 'test that resolve falls back to broader classifier in case of too specific one'() {
        when:
        def file = this.resolver.resolve(Artifact.builder()
                .groupId('io.netty')
                .artifactId('netty-tcnative')
                .version('2.0.65.Final')
                .classifier('linux-x86_64-arch')
                .build(), 'jar')

        then:
        file.exists()

        and:
        file.size() > 1
    }

    def 'test that resolve fall back fails if broader classifier is not found'() {
        given:
        def field = ConveyorArtifactResolver.getDeclaredField('osMavenProperties')
        field.accessible = true
        field.set(this.resolver, Properties.newProperties(['os.detected.classifier': 'linux']))

        when:
        this.resolver.resolve(Artifact.builder()
                .groupId('it.fulminazzo')
                .artifactId('conveyor')
                .version('1.0')
                .classifier('linux-x86_64-arch')
                .build(), 'jar')

        then:
        thrown(ArtifactResolverException)
    }

    def 'test that resolve fall back fails if classifier is not recognized'() {
        when:
        this.resolver.resolve(Artifact.builder()
                .groupId('it.fulminazzo')
                .artifactId('conveyor')
                .version('1.0')
                .classifier('windows')
                .build(), 'jar')

        then:
        thrown(ArtifactResolverException)
    }

    def 'test that resolve fall back fails if no os classifier has been specified'() {
        given:
        def field = ConveyorArtifactResolver.getDeclaredField('osMavenProperties')
        field.accessible = true
        field.set(this.resolver, Properties.newProperties([:]))

        when:
        this.resolver.resolve(Artifact.builder()
                .groupId('it.fulminazzo')
                .artifactId('conveyor')
                .version('1.0')
                .classifier('linux-x86_64-arch')
                .build(), 'jar')

        then:
        thrown(ArtifactResolverException)
    }

    def 'test that resolve fall back fails if no artifact classifier has been specified'() {
        when:
        this.resolver.resolve(new Artifact('hello', 'world', '1.0'), 'jar')

        then:
        thrown(ArtifactResolverException)
    }

    def 'integration test for ConveyorArtifactResolver'() {
        given:
        if (workDir.exists()) workDir.deleteDir()

        and:
        def artifact = new Artifact('org.projectlombok', 'lombok', '1.18.42')

        when:
        def artifact1 = this.resolver.resolve(artifact, 'jar')

        then:
        artifact1.exists()

        when:
        def artifact2 = this.resolver.resolve(artifact, 'jar')

        then:
        artifact2.exists()
    }

    def 'test that getDelegate throws if not initialized'() {
        given:
        def resolver = ConveyorArtifactResolver.newResolver(this.repositoryManager, workDir, log)

        when:
        resolver.delegate

        then:
        thrown(ArtifactResolverException)
    }

}
