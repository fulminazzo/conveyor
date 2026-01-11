package it.fulminazzo.conveyor.pom;

import org.jetbrains.annotations.NotNull;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class MockMavenModelBuilder extends MavenModelBuilder {

    private MockMavenModelBuilder(final @NotNull XMLStreamReader reader) {
        super(reader);
    }

    public static @NotNull MockMavenModelBuilder newBuilder(final @NotNull String rawData) throws ParserException {
        try {
            InputStream inputStream = new ByteArrayInputStream(rawData.getBytes());
            XMLInputFactory factory = XMLInputFactory.newInstance();
            return new MockMavenModelBuilder(factory.createXMLStreamReader(inputStream));
        } catch (XMLStreamException e) {
            throw ParserException.of(
                    String.format("Error while creating %s", MockMavenModelBuilder.class.getSimpleName()), e
            );
        }
    }

}
