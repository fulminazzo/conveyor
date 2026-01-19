package it.fulminazzo.conveyor.model;

import it.fulminazzo.conveyor.function.ConsumerException;
import it.fulminazzo.conveyor.function.SupplierException;
import it.fulminazzo.conveyor.xml.XmlParser;
import it.fulminazzo.conveyor.xml.XmlParserException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A special type of builder that uses <b>XML</b> documents to create objects.
 *
 * @param <O> the type of the built object
 */
@RequiredArgsConstructor
public abstract class XmlObjectBuilder<O> {
    @Getter(AccessLevel.PROTECTED)
    private final @NotNull XmlParser parser;

    /**
     * Builds the object.
     *
     * @return the object
     * @throws BuilderException in case of building errors
     */
    public abstract O build() throws BuilderException;

    /**
     * Wrapper for building new objects from builders.
     * Replaces any building errors with a {@link BuilderException}.
     *
     * @param <T>           the type of the built object
     * @param name          the name of the building object
     * @param buildFunction the build function
     * @return the built object
     * @throws BuilderException in case of building errors
     */
    protected <T> @NotNull T buildObject(final @NotNull String name,
                                         final @NotNull SupplierException<T, Exception> buildFunction
    ) throws BuilderException {
        try {
            return buildFunction.get();
        } catch (Exception e) {
            throw new BuilderException(String.format("Could not build %s", name), e);
        }
    }

    /**
     * Assuming the parser has just entered the tags of an XML element,
     * will call the given function for all the children elements.
     *
     * @param then the function to execute (provides the tag of the child as argument)
     * @throws BuilderException in case of any errors
     */
    protected void onChildElements(final @NotNull ConsumerException<String, BuilderException> then) throws BuilderException {
        for (String element : this.parser.children()) {
            then.accept(element);
        }
    }

    /**
     * Gets the latest tag read from the parser.
     *
     * @return the current tag
     * @throws BuilderException in case of any errors
     */
    protected @NotNull String getCurrentTag() throws BuilderException {
        try {
            return this.parser.getCurrentTag();
        } catch (XmlParserException e) {
            throw new BuilderException(e);
        }
    }

    /**
     * Gets the latest text content read from the parser.
     * If not available, returns <code>null</code>.
     *
     * @return the current text content
     * @throws BuilderException in case of any errors
     */
    protected @Nullable String getCurrentTextContent() throws BuilderException {
        try {
            return this.parser.getCurrentContent();
        } catch (XmlParserException e) {
            return null;
        }
    }

}
