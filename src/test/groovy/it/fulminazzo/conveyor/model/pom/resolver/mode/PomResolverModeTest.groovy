package it.fulminazzo.conveyor.model.pom.resolver.mode

import groovy.util.logging.Slf4j
import it.fulminazzo.conveyor.downloader.DownloadException
import it.fulminazzo.conveyor.downloader.Downloader
import it.fulminazzo.conveyor.manager.RepositoryManager
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.repository.Repository
import it.fulminazzo.conveyor.util.TestUtils
import spock.lang.Specification

@Slf4j
class PomResolverModeTest extends Specification {
    private static final File baseDir = new File(TestUtils.BASE_DIR, 'pom_resolver/mode')
    private static final Artifact artifact = new Artifact('org.projectlombok', 'lombok', '1.18.42')

    private RepositoryManager manager

    void setup() {
        this.manager = RepositoryManager.newManager(log).addRepositories(
                Repository.builder().id('main').url(TestUtils.MAVEN_CENTRAL_URL).build()
        )
    }

    def 'test that PomResolverMode of MEMORY does not store on file system'() {
        given:
        def workDir = new File(baseDir, 'memory')
        if (workDir.exists()) workDir.deleteDir()
        workDir.mkdirs()

        and:
        def resolver = PomResolverMode.MEMORY.create(this.manager, workDir, log)

        when:
        def pom = resolver.resolve(artifact)

        then:
        pom != null
        pom.project == artifact

        and:
        !new File(workDir, artifact.getFullPath('pom')).exists()
    }

    def 'test that PomResolverMode of DISK stores on file system if not previously existing'() {
        given:
        def workDir = new File(baseDir, 'disk')
        if (workDir.exists()) workDir.deleteDir()
        workDir.mkdirs()

        and:
        def resolver = PomResolverMode.DISK.create(this.manager, workDir, log)

        when:
        def pom1 = resolver.resolve(artifact)

        then:
        pom1 != null
        pom1.project == artifact

        and:
        new File(workDir, artifact.getFullPath('pom')).exists()

        when:
        def pom2 = resolver.resolve(artifact)

        then:
        pom2 != null
        pom2.project == artifact
    }

    def 'test that PomResolverMode of CHECKSUM stores on file system if not previously existing'() {
        given:
        def workDir = new File(baseDir, 'checksum')
        if (workDir.exists()) workDir.deleteDir()
        workDir.mkdirs()

        and:
        def resolver = PomResolverMode.CHECKSUM.create(this.manager, workDir, log)

        when:
        def pom1 = resolver.resolve(artifact)

        then:
        pom1 != null
        pom1.project == artifact

        and:
        new File(workDir, artifact.getFullPath('pom')).exists()

        when:
        def pom2 = resolver.resolve(artifact)

        then:
        pom2 != null
        pom2.project == artifact
    }

    def 'test that PomResolverMode correctly creates resolver of mode #mode'() {
        when:
        def resolver = mode.create(Mock(RepositoryManager), baseDir, log)

        then:
        resolver != null

        where:
        mode << PomResolverMode.values()
    }

    def 'test that PomResolverMode, in the event of a rare FileNotFoundException, throws DownloadException'() {
        given:
        def resolver = mode.create(Mock(RepositoryManager), baseDir, log)

        and:
        def file = new File('not_existing')
        def downloader = Mock(Downloader)
        downloader.getResourceFile(_) >> file
        downloader.resolveToFile(_, _) >> file

        and:
        def field = resolver.class.getDeclaredField('downloader')
        field.accessible = true
        field.set(resolver, downloader)

        when:
        resolver.resolve(file.path, [])

        then:
        def e = thrown(DownloadException)

        and:
        def cause = e.cause
        (cause instanceof FileNotFoundException)

        where:
        mode << [PomResolverMode.DISK, PomResolverMode.CHECKSUM]
    }

}
