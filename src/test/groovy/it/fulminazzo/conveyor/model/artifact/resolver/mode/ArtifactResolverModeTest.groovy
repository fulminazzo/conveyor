package it.fulminazzo.conveyor.model.artifact.resolver.mode

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.manager.RepositoryManager
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.repository.Repository
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class ArtifactResolverModeTest extends Specification {
    private static final RepositoryManager repositoryManager = RepositoryManager.newManager(log)
            .addRepositories(Repository.builder().id('main').url(TestUtils.MAVEN_CENTRAL_URL).build())
    private static final File baseDir = new File(TestUtils.BASE_DIR, 'artifact_resolver/mode')
    private static final Artifact artifact = new Artifact('org.projectlombok', 'lombok', '1.18.42')

    def 'test that ArtifactResolverMode correctly creates resolver of mode #mode'() {
        when:
        def resolver = mode.create(repositoryManager, baseDir, log)

        then:
        resolver != null

        where:
        mode << ArtifactResolverMode.values()
    }

}
