package it.fulminazzo.conveyor.model

import spock.lang.Specification

class XmlObjectBuilderTest extends Specification {

    def 'test that buildObject returns expected'() {
        given:
        def builder = MockXmlObjectBuilder.newBuilder('<project>')

        when:
        def actual = builder.buildObject('test', t -> 'Hello, world!')

        then:
        actual == 'Hello, world!'
    }

    def 'test that buildObject throws BuilderException on Exception'() {
        given:
        def builder = MockXmlObjectBuilder.newBuilder('<project>')

        and:
        def exception = new Exception('Test exception')

        when:
        builder.buildObject('test', t -> {
            throw exception
        })

        then:
        def e = thrown(BuilderException)
        e.message == 'Could not build test'
        e.cause == exception
    }

    def 'test that getCurrentTag returns expected'() {
        given:
        def builder = MockXmlObjectBuilder.newBuilder('<project>Hello, world!</project>')
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def actual = builder.currentTag

        then:
        actual == 'project'
    }

    def 'test that getCurrentTag throws BuilderException on XmlParserException'() {
        given:
        def builder = MockXmlObjectBuilder.newBuilder('')

        when:
        builder.currentTag

        then:
        thrown(BuilderException)
    }

    def 'test that getCurrentTextContent returns expected'() {
        given:
        def builder = MockXmlObjectBuilder.newBuilder('<project>Hello, world!</project>')
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def actual = builder.currentTextContent

        then:
        actual == 'Hello, world!'
    }

    def 'test that getCurrentTextContent throws BuilderException on XmlParserException'() {
        given:
        def builder = MockXmlObjectBuilder.newBuilder('')

        when:
        builder.currentTextContent

        then:
        thrown(BuilderException)
    }


}
