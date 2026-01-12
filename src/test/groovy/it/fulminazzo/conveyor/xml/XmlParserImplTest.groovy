package it.fulminazzo.conveyor.xml

import spock.lang.Specification

import javax.xml.stream.XMLInputFactory

class XmlParserImplTest extends Specification {

    def 'test hasNext and next methods return correct values'() {
        given:
        def parser = newParser('<first></first><second></second><third></third>')

        when:
        def next = parser.hasNext()

        then:
        next

        when:
        def tag = parser.next()

        then:
        tag == 'first'

        when:
        next = parser.hasNext()

        then:
        next

        when:
        tag = parser.next()

        then:
        tag == 'second'

        when:
        next = parser.hasNext()

        then:
        next

        when:
        tag = parser.next()

        then:
        tag == 'third'

        when:
        next = parser.hasNext()

        then:
        !next
    }

    def 'test children returns all the children'() {
        given:
        def parser = newParser("""
            <parent>
                <first></first>
                <second></second>
                <third></third>
            </parent>
            <sibling>
            </sibling>
            """)

        when:
        parser.next()

        and:
        def children = parser.children().toList()

        then:
        children == ['first', 'second', 'third']

        and:
        parser.hasNext()
    }

    def 'test getCurrentTag returns correct value'() {
        given:
        def parser = newParser('<first></first>')

        when:
        parser.next()

        and:
        def tag = parser.currentTag

        then:
        tag == 'first'
    }

    def 'test getCurrentTag throws on not present'() {
        given:
        def parser = newParser('')

        when:
        parser.currentTag

        then:
        thrown(XmlParserException)
    }

    def 'test getCurrentContent returns correct value'() {
        given:
        def parser = newParser('<first>Hello, world!</first>')

        when:
        parser.next()

        and:
        def content = parser.currentContent

        then:
        content == 'Hello, world!'
    }

    def 'test getCurrentContent throws on non-text content'() {
        given:
        def parser = newParser('<second><first>Hello, world!</first></second>')

        when:
        parser.next()

        and:
        parser.currentContent

        then:
        thrown(XmlParserException)
    }

    def 'test getCurrentContent throws on not present'() {
        given:
        def parser = newParser('')

        when:
        parser.currentContent

        then:
        thrown(XmlParserException)
    }

    private XmlParserImpl newParser(final String rawData) {
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes());
        XMLInputFactory factory = XMLInputFactory.newInstance();
        return new XmlParserImpl(factory.createXMLStreamReader(inputStream));
    }

}
