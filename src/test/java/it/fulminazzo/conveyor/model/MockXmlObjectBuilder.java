package it.fulminazzo.conveyor.model;

import it.fulminazzo.conveyor.xml.XmlParser;
import it.fulminazzo.conveyor.xml.XmlParserException;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

final class MockXmlObjectBuilder extends XmlObjectBuilder<Object> {

    public MockXmlObjectBuilder(final @NotNull XmlParser parser) {
        super(parser);
    }

    @Override
    public Object build() {
        throw new UnsupportedOperationException();
    }

    public static @NotNull XmlObjectBuilder<?> newBuilder(final @NotNull String rawData) throws BuilderException {
        try {
            InputStream inputStream = new ByteArrayInputStream(rawData.getBytes(StandardCharsets.UTF_8));
            XmlParser parser = XmlParser.newParser(inputStream);
            return new MockXmlObjectBuilder(parser);
        } catch (XmlParserException e) {
            throw new BuilderException(e);
        }
    }

}
