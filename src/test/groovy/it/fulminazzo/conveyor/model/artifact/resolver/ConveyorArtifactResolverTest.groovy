package it.fulminazzo.conveyor.model.artifact.resolver

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.manager.RepositoryManager
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.artifact.resolver.mode.ArtifactResolverMode
import it.fulminazzo.conveyor.model.repository.Repository
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class ConveyorArtifactResolverTest extends Specification {
    private static final File workDir = new File(TestUtils.BASE_DIR, 'artifact_resolver/conveyor')

    private RepositoryManager repositoryManager

    void setup() {
        this.repositoryManager = RepositoryManager.newManager(log)
                .addRepositories(Repository.builder().id('main').url(TestUtils.MAVEN_CENTRAL_URL).build())
    }

    def 'integration test for ConveyorArtifactResolver'() {
        given:
        if (workDir.exists()) workDir.deleteDir()

        and:
        def resolver = ConveyorArtifactResolver.newResolver(this.repositoryManager, workDir, log)
                .setMode(ArtifactResolverMode.CHECKSUM)

        and:
        def artifact = new Artifact('org.projectlombok', 'lombok', '1.18.42')

        when:
        def artifact1 = resolver.resolve(artifact, 'jar')

        then:
        artifact1.exists()

        when:
        def artifact2 = resolver.resolve(artifact, 'jar')

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
