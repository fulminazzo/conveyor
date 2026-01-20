package it.fulminazzo.conveyor

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class ConveyorTest extends Specification {

    def 'downloadArtifact integration tests with #artifact'() {
        given:
        def workDir = new File(TestUtils.BASE_DIR, 'conveyor')

        and:
        def conveyor = Conveyor.newConveyor(
                workDir,
                log
        ).addRawRepositories('repo.fulminazzo.it/releases')

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
        files.size() > 0

        and:
        files.values().every { it.exists() }

        and:
        key != null
        key.depth() == 0

        and:
        files.get(key).exists()

        where:
        artifact << [
                new Artifact('io.netty', 'netty-handler', '4.1.112.Final'),
                new Artifact('org.springframework', 'spring-core', '7.0.3'),
                new Artifact('it.fulminazzo', 'FulmiCollection', '1.8.2'),
                new Artifact('it.fulminazzo', 'Configurations', '1.6.4'),
                new Artifact('it.fulminazzo', 'yagl', '5.2')
        ]
    }

    def 'buildDependenciesTree integration tests with #artifact'() {
        given:
        def workDir = new File(TestUtils.BASE_DIR, 'conveyor')

        and:
        def conveyor = Conveyor.newConveyor(
                workDir,
                log
        ).addRawRepositories('repo.fulminazzo.it/releases')

        when:
        def files = conveyor.buildDependenciesTree(artifact)

        then:
        files.size() > 0

        where:
        artifact << [
                new Artifact('io.netty', 'netty-handler', '4.1.112.Final'),
                new Artifact('org.springframework', 'spring-core', '7.0.3'),
                new Artifact('it.fulminazzo', 'FulmiCollection', '1.8.2'),
                new Artifact('it.fulminazzo', 'Configurations', '1.6.4'),
                new Artifact('it.fulminazzo', 'yagl', '5.2')
        ]
    }

    def 'test that testing environment works'() {
        expect:
        true
    }

}
