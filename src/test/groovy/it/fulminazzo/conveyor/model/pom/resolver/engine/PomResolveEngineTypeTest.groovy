package it.fulminazzo.conveyor.model.pom.resolver.engine

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.downloader.DelegateDownloader
import it.fulminazzo.conveyor.downloader.DownloadSource
import it.fulminazzo.conveyor.downloader.Downloader
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class PomResolveEngineTypeTest extends Specification {
    private static final File baseDir = new File(TestUtils.BASE_DIR, 'pom_resolver/engine')

    private static final Artifact artifact = Artifact.builder()
            .groupId('org.projectlombok')
            .artifactId('lombok')
            .version('1.18.42')
            .build()

    void setup() {
        if (baseDir.exists()) baseDir.deleteDir()
        baseDir.mkdirs()
    }

    def 'test that MEMORY always downloads the requested pom'() {
        given:
        def workDir = new File(baseDir, 'memory')

        and:
        def engine = PomResolveEngineType.MEMORY.create(workDir, log)
                .addSources([new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)])

        when:
        def pom1 = engine.resolve(artifact)

        then:
        pom1 != null

        and:
        !new File(workDir, artifact.getFullPath('pom')).exists()
    }

    def 'test that DISK only downloads once the requested pom'() {
        given:
        def workDir = new File(baseDir, 'disk')

        and:
        def engine = PomResolveEngineType.DISK.create(workDir, log)
                .addSources([new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)])

        and:
        def downloader = injectDownloader(engine)

        when:
        def pom1 = engine.resolve(artifact)

        then:
        pom1 != null

        and:
        1 * downloader.resolveToFile(_)

        and:
        new File(workDir, artifact.getFullPath('pom')).exists()

        when:
        def pom2 = engine.resolve(artifact)

        then:
        pom2 != null

        and:
        0 * downloader.resolveToFile(_)
    }

    def 'test that CHECKSUM only downloads once the requested pom'() {
        given:
        def workDir = new File(baseDir, 'checksum')

        and:
        def engine = PomResolveEngineType.CHECKSUM.create(workDir, log)
                .addSources([new DownloadSource(TestUtils.MAVEN_CENTRAL_URL)])

        when:
        def pom1 = engine.resolve(artifact)

        then:
        pom1 != null

        and:
        new File(workDir, artifact.getFullPath('pom')).exists()

        when:
        def pom2 = engine.resolve(artifact)

        then:
        pom2 != null
    }

    def 'test that #type create does not throw'() {
        when:
        type.create(TestUtils.BASE_DIR, log)

        then:
        noExceptionThrown()

        where:
        type << PomResolveEngineType.values()
    }

    private Downloader injectDownloader(DownloaderPomResolverEngine engine) {
        def mockDownloader = Spy(DelegateDownloader, constructorArgs: [engine.downloader])
        def field = DownloaderPomResolverEngine.getDeclaredField('downloader')
        field.setAccessible(true)
        field.set(engine, mockDownloader)
        return mockDownloader
    }

}
