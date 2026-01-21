package it.fulminazzo.conveyor.xml

import spock.lang.Specification

import javax.xml.stream.XMLInputFactory
import javax.xml.stream.XMLStreamConstants
import javax.xml.stream.XMLStreamException
import javax.xml.stream.XMLStreamReader

class XmlParserImplTest extends Specification {

    def 'test that initialize XmlParserImpl with XMLStreamException throws XmlParserException'() {
        given:
        SpyStatic(XMLInputFactory)

        and:
        def factory = Mock(XMLInputFactory)
        factory.createXMLStreamReader(_ as InputStream) >> {
            throw new XMLStreamException('test')
        }
        XMLInputFactory.newInstance() >> factory

        when:
        new XmlParserImpl(new ByteArrayInputStream(''.bytes))

        then:
        thrown(XmlParserException)
    }

    def 'test that children does not return grand children'() {
        given:
        def parser = newParser("""
        <project>
            <first>
                <first_grand></first_grand>
                <second_grand></second_grand>
                <third_grand></third_grand>
            </first>
            <second>Hello, world!</second>
            <third></third>
            <fourth/>
        </project>""")

        when:
        parser.next()

        and:
        def children = parser.children().toList()

        then:
        children == ['first', 'second', 'third', 'fourth']
    }

    def 'test that invalid getCurrentContent does not prevent reading of next element'() {
        given:
        def parser = newParser('<project><first>Hello, world</first></project>')

        expect:
        parser.hasNext()

        and:
        parser.next() == 'project'

        when:
        parser.currentContent

        then:
        thrown(XmlParserException)

        and:
        parser.hasNext()

        and:
        parser.next() == 'first'

        and:
        parser.currentContent == 'Hello, world'

        and:
        !parser.hasNext()

        and:
        !parser.hasNext()

        and:
        !parser.hasNext()

        cleanup:
        parser.close()
    }

    def 'test that getCurrentContent does not throw on ended data'() {
        given:
        def parser = newParser('')

        when:
        parser.currentContent

        then:
        thrown(XmlParserException)

        when:
        parser.currentContent

        then:
        thrown(XmlParserException)

        cleanup:
        parser.close()
    }

    def 'test that next does not throw after currentContent ended data'() {
        given:
        def parser = newParser('')

        when:
        parser.currentContent

        then:
        thrown(XmlParserException)

        when:
        parser.next()

        then:
        thrown(XmlParserException)

        cleanup:
        parser.close()
    }

    def 'test hasNext and next methods return correct values'() {
        given:
        def parser = newParser('<project><first></first><second></second><third></third></project>')

        expect:
        parser.hasNext()

        when:
        def tag = parser.next()

        then:
        tag == 'project'

        and:
        parser.hasNext()

        when:
        tag = parser.next()

        then:
        tag == 'first'

        and:
        parser.hasNext()

        when:
        tag = parser.next()

        then:
        tag == 'second'

        and:
        parser.hasNext()

        when:
        tag = parser.next()

        then:
        tag == 'third'

        and:
        !parser.hasNext()

        cleanup:
        parser.close()
    }

    def 'test hasNext throws XmlParserException on XMLStreamException'() {
        given:
        def parser = newParser('')

        when:
        parser.hasNext()

        then:
        thrown(XmlParserException)

        cleanup:
        parser.close()
    }

    def 'test children returns all the children'() {
        given:
        def parser = newParser("""
            <project>
                <parent>
                <first></first>
                <second></second>
                <third></third>
                </parent>
                <sibling>
                </sibling>
            </project>
            """)
        parser.next()

        when:
        parser.next()

        and:
        def children = parser.children().toList()

        then:
        children == ['first', 'second', 'third']

        and:
        parser.hasNext()

        cleanup:
        parser.close()
    }

    def 'test children iterator throws RuntimeXmlParserException on XmlParserException'() {
        given:
        def parser = newParser('')

        and:
        def children = parser.children().iterator()

        when:
        children.hasNext()

        then:
        def e = thrown(RuntimeXmlParserException)

        when:
        def cause = e.cause

        then:
        cause != null
        cause.class == XmlParserException

        when:
        children.next()

        then:
        e = thrown(RuntimeXmlParserException)

        when:
        cause = e.cause

        then:
        cause != null
        cause.class == XmlParserException

        cleanup:
        parser.close()
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

        cleanup:
        parser.close()
    }

    def 'test getCurrentTag throws on not present'() {
        given:
        def parser = newParser('')

        when:
        parser.currentTag

        then:
        thrown(XmlParserException)

        cleanup:
        parser.close()
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

        when:
        content = parser.currentContent

        then:
        content == 'Hello, world!'

        cleanup:
        parser.close()
    }

    def 'test getCurrentContent throws on non-text content'() {
        given:
        def parser = newParser(data)

        when:
        parser.next()

        and:
        parser.currentContent

        then:
        thrown(XmlParserException)

        cleanup:
        parser.close()

        where:
        data << [
                '<second><first>Hello, world!</first></second>',
                '<second>\n\t</second>'
        ]
    }

    def 'test getCurrentContent throws on not present'() {
        given:
        def parser = newParser(data)

        when:
        parser.currentContent

        then:
        thrown(XmlParserException)

        cleanup:
        parser.close()

        where:
        data << ['', '<project></project>']
    }

    def 'test getCurrentContent throws on already fully read'() {
        given:
        def parser = newParser('<project></project>')
        parser.reader.next()
        parser.reader.next()
        parser.reader.next()

        when:
        parser.currentContent

        then:
        thrown(XmlParserException)

        cleanup:
        parser.close()
    }

    def 'test that close with XMLStreamException does not throw'() {
        given:
        def parser = newParser('')

        and:
        def reader = Mock(XMLStreamReader)
        reader.close() >> {
            throw new XMLStreamException()
        }

        and:
        def field = XmlParserImpl.getDeclaredField('reader')
        field.accessible = true
        field.set(parser, reader)

        when:
        parser.close()

        then:
        noExceptionThrown()

        and:
        parser.reader == null
    }

    def 'test that close with IOException does not throw'() {
        given:
        def parser = newParser('')

        and:
        def inputStream = Mock(InputStream)
        inputStream.close() >> {
            throw new IOException()
        }

        and:
        def field = XmlParserImpl.getDeclaredField('inputStream')
        field.accessible = true
        field.set(parser, inputStream)

        when:
        parser.close()

        then:
        noExceptionThrown()

        and:
        parser.reader == null
    }

    def 'test that handleEvent throws if reader is null'() {
        given:
        def parser = newParser('')

        and:
        def field = XmlParserImpl.getDeclaredField('reader')
        field.accessible = true
        field.set(parser, null)

        when:
        parser.handleEvent(XMLStreamConstants.START_DOCUMENT)

        then:
        thrown(IllegalStateException)
    }

    private static XmlParserImpl newParser(final String rawData) {
        InputStream inputStream = new ByteArrayInputStream(rawData.getBytes())
        return new XmlParserImpl(inputStream)
    }

}
