package it.fulminazzo.conveyor.property;

import lombok.AccessLevel;
import lombok.experimental.StandardException;

/**
 * An exception thrown by {@link PropertyAccessor}.
 */
@StandardException(access = AccessLevel.PACKAGE)
public final class PropertyAccessorException extends RuntimeException {

}
