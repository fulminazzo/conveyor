package it.fulminazzo.conveyor.model.pom.resolver

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.manager.RepositoryManager
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.pom.resolver.mode.PomResolverMode
import it.fulminazzo.conveyor.model.repository.Repository
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class ConveyorPomResolverTest extends Specification {
    private static final File workDir = new File(TestUtils.BASE_DIR, 'pom_resolver/conveyor')

    private RepositoryManager repositoryManager

    void setup() {
        this.repositoryManager = RepositoryManager.newManager(log)
    }

    def 'integration test for ConveyorPomResolver'() {
        given:
        if (workDir.exists()) workDir.deleteDir()

        and:
        def resolver = ConveyorPomResolver.newResolver(this.repositoryManager, workDir, log)
                .addRepositories([Repository.builder().id('main').url(TestUtils.MAVEN_CENTRAL_URL).build()])
                .setMode(PomResolverMode.CHECKSUM)

        and:
        def artifact = new Artifact('org.projectlombok', 'lombok', '1.18.42')

        when:
        def pom1 = resolver.resolve(artifact)

        then:
        pom1.project == artifact
        pom1.packaging == 'jar'

        when:
        def pom2 = resolver.resolve(artifact)

        then:
        pom2.project == artifact
        pom2.packaging == 'jar'
    }

    def 'test that getDelegate throws if not initialized'() {
        given:
        def resolver = ConveyorPomResolver.newResolver(this.repositoryManager, workDir, log)

        when:
        resolver.delegate

        then:
        thrown(PomResolverException)
    }

}
