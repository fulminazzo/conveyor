package it.fulminazzo.conveyor.model

import it.fulminazzo.conveyor.xml.XmlParser
import lombok.AccessLevel
import lombok.NoArgsConstructor
import org.jetbrains.annotations.NotNull

@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class XmlObjectBuilderUtils {

    static XmlParser getParser(final @NotNull XmlObjectBuilder<?> builder) {
        def field = XmlObjectBuilder.getDeclaredField('parser')
        field.accessible = true
        return field.get(builder)
    }

}
