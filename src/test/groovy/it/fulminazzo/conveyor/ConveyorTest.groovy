package it.fulminazzo.conveyor

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class ConveyorTest extends Specification {

    def 'buildDependencyTree integration tests'() {
        given:
        def workDir = new File(TestUtils.BASE_DIR, 'conveyor')
        if (workDir.exists()) workDir.deleteDir()

        and:
        def conveyor = new Conveyor(
                ActivationContext.current(workDir),
                workDir,
                log
        )

        when:
        conveyor.buildDependencyTree(new Artifact('org.springframework', 'spring-core', '7.0.3'))

        then:
        noExceptionThrown()
    }

    def 'test that testing environment works'() {
        expect:
        true
    }

}
