package it.fulminazzo.conveyor.model.artifact.resolver.mode

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.downloader.DelegateDownloader
import it.fulminazzo.conveyor.downloader.Downloader
import it.fulminazzo.conveyor.manager.RepositoryManager
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.repository.Repository
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class ArtifactResolverModeTest extends Specification {
    private static final File baseDir = new File(TestUtils.BASE_DIR, 'artifact_resolver/mode')
    private static final Artifact artifact = new Artifact('org.projectlombok', 'lombok', '1.18.42')

    private RepositoryManager manager

    void setup() {
        this.manager = RepositoryManager.newManager(log).addRepositories(
                Repository.builder().id('main').url(TestUtils.MAVEN_CENTRAL_URL).build()
        )
    }

    def 'test that ArtifactResolver of CHECKSUM only downloads once'() {
        given:
        def workDir = new File(baseDir, 'disk')
        if (workDir.exists()) workDir.deleteDir()
        workDir.mkdirs()

        and:
        def resolver = ArtifactResolverMode.CHECKSUM.create(this.manager, workDir, log)
        def checksumDownloader = injectDownloader(resolver, 'downloader')
        def delegateDownloader = injectDownloader(checksumDownloader.delegate, 'delegate')

        when:
        def file1 = resolver.resolve(artifact, 'jar')

        then:
        file1.exists()

        and:
        1 * checksumDownloader.resolveToFile(_, _)
        1 * delegateDownloader.resolveToFile(_, _)

        when:
        def file2 = resolver.resolve(artifact, 'jar')

        then:
        file2.exists()

        and:
        1 * checksumDownloader.resolveToFile(_, _)
        0 * delegateDownloader.resolveToFile(_, _)
    }

    def 'test that ArtifactResolverMode correctly creates resolver of mode #mode'() {
        when:
        def resolver = mode.create(this.manager, baseDir, log)

        then:
        resolver != null

        where:
        mode << ArtifactResolverMode.values()
    }

    private Downloader injectDownloader(Object object, String fieldName) {
        def field = object.class.getDeclaredField(fieldName)
        field.accessible = true
        Downloader downloader = field.get(object)
        def spy = Spy(DelegateDownloader, constructorArgs: [downloader])
        field.set(object, spy)
        return spy
    }

}
