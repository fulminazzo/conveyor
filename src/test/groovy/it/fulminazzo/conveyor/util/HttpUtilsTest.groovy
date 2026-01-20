package it.fulminazzo.conveyor.util

import spock.lang.Specification

class HttpUtilsTest extends Specification {

    def 'test that openConnection of #resourcePath does not throw'() {
        when:
        def data = HttpUtils.openConnection(website, resourcePath)

        then:
        noExceptionThrown()

        and:
        data.available() > 0

        cleanup:
        data.close()

        where:
        resourcePath              | website
        TestUtils.LOMBOK_PATH     | "https://$TestUtils.MAVEN_CENTRAL_URL"
        "/$TestUtils.LOMBOK_PATH" | "https://$TestUtils.MAVEN_CENTRAL_URL"
        TestUtils.LOMBOK_PATH     | "https://$TestUtils.MAVEN_CENTRAL_URL/"
        "/$TestUtils.LOMBOK_PATH" | "https://$TestUtils.MAVEN_CENTRAL_URL/"
    }

    def 'test that openConnection throws IOException on not found'() {
        when:
        HttpUtils.openConnection("https://$TestUtils.MAVEN_CENTRAL_URL/", 'path')

        then:
        thrown(IOException)
    }

}
