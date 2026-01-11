package it.fulminazzo.conveyor.function;

/**
 * A special type of {@link java.util.function.Consumer}
 * that might throw an exception upon execution.
 *
 * @param <T> the type of the parameter
 * @param <X> the type of the throwable
 */
@FunctionalInterface
public interface ConsumerException<T, X extends Throwable> {

    /**
     * Applies the current function to the given parameter.
     *
     * @param parameter the parameter
     * @throws X in case of errors
     */
    void accept(final T parameter) throws X;

}
