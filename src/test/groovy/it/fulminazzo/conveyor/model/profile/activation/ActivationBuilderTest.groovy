package it.fulminazzo.conveyor.model.profile.activation

import it.fulminazzo.conveyor.model.XmlObjectBuilderUtils
import it.fulminazzo.conveyor.xml.XmlParser
import spock.lang.Specification

class ActivationBuilderTest extends Specification {

    def 'test parseFile of #data returns #expected'() {
        given:
        def builder = newBuilder(data)
        XmlObjectBuilderUtils.getParser(builder).next()

        when:
        def actual = builder.parseFile()

        then:
        actual == expected

        where:
        expected                                                             || data
        FileActivation.builder().build()                                     || '<file></file>'
        FileActivation.builder().missing('missing').build()                  || '<file><missing>missing</missing></file>'
        FileActivation.builder().exists('exists').build()                    || '<file><exists>exists</exists></file>'
        FileActivation.builder().missing('missing').exists('exists').build() || '<file><missing>missing</missing><exists>exists</exists></file>'
    }

    private static ActivationBuilder newBuilder(final String data) {
        def stream = new ByteArrayInputStream(data.bytes)
        def parser = XmlParser.newParser(stream)
        return new ActivationBuilder(parser)
    }

}
