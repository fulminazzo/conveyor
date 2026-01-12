package it.fulminazzo.conveyor.function;

/**
 * A special type of {@link java.util.function.Supplier}
 * that might throw an exception upon execution.
 *
 * @param <T> the type of the returned object
 * @param <X> the type of the throwable
 */
@FunctionalInterface
public interface SupplierException<T, X extends Throwable> {

    /**
     * Gets the object.
     *
     * @return the returned object
     * @throws X in case of errors
     */
    T get() throws X;

}
