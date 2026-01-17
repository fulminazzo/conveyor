package it.fulminazzo.conveyor

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class ConveyorTest extends Specification {

    def 'downloadArtifact integration tests'() {
        given:
        def workDir = new File(TestUtils.BASE_DIR, 'conveyor')

        and:
        def conveyor = Conveyor.newConveyor(
                ActivationContext.current(workDir),
                workDir,
                log
        )

        and:
        def artifact = new Artifact('org.springframework', 'spring-core', '7.0.3')

        when:
        def files = conveyor.downloadArtifact(artifact)

        and:
        def key = files.keySet().find {
            def dependency = it.dependency()
            return dependency.groupId == artifact.groupId &&
                    dependency.artifactId == artifact.artifactId &&
                    dependency.version == artifact.version
        }

        then:
        files.size() > 1

        and:
        files.values().every { it.exists() }

        and:
        key != null
        key.depth() == 0

        and:
        files.get(key).exists()
    }

    def 'buildDependenciesTree integration tests'() {
        given:
        def workDir = new File(TestUtils.BASE_DIR, 'conveyor')

        and:
        def conveyor = Conveyor.newConveyor(
                ActivationContext.current(workDir),
                workDir,
                log
        )

        when:
        conveyor.buildDependenciesTree(
                new Artifact('org.springframework', 'spring-core', '7.0.3')
        )

        then:
        noExceptionThrown()
    }

    def 'test that testing environment works'() {
        expect:
        true
    }

}
