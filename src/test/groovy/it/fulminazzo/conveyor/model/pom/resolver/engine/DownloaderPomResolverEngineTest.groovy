package it.fulminazzo.conveyor.model.pom.resolver.engine

import it.fulminazzo.conveyor.downloader.DownloadException
import it.fulminazzo.conveyor.downloader.Downloader
import it.fulminazzo.conveyor.model.artifact.Artifact
import it.fulminazzo.conveyor.model.pom.resolver.PomResolverException
import spock.lang.Specification

class DownloaderPomResolverEngineTest extends Specification {

    def 'test that resolve throws correctly wrapped exception'() {
        given:
        def resolver = Spy(DownloaderPomResolverEngine, constructorArgs: [Mock(Downloader)])

        and:
        def expected = new DownloadException('Hello, world', new IllegalArgumentException('Invalid argument'))

        and:
        resolver.resolve(_ as String) >> {
            throw expected
        }

        when:
        resolver.resolve(new Artifact('it.fulminazzo', 'conveyor', '1.0'))

        then:
        def e = thrown(PomResolverException)
        e.message == expected.message
        e.cause == expected.cause
    }

}
