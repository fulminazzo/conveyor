package it.fulminazzo.conveyor.property;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Denotes a special object that supports access to
 * its data through properties notation.
 */
public interface PropertyAccessible {

    /**
     * Attempts to retrieve the property associated with the given key.
     * The rules are the following:
     * <ul>
     *     <li>if <code>key</code> is alphabetical, a <b>field</b> with that name is searched.
     *     If no such field is found, a <b>method</b> with that name and no parameters is searched.
     *     If no such method is found, <code>null</code> will be returned;</li>
     *     <li>if <code>key</code> is indexed (<code>[i]</code>), will try the previously described
     *     lookup, and then will access the requested item of the collection;</li>
     *     <li>if <code>key</code> contains a dot ("."), the previously described lookups are executed,
     *     then the same function is executed for the result.</li>
     * </ul>
     *
     * @param key the key associated with the property
     * @return the property
     */
    default @Nullable String getProperty(final @NotNull String key) {
        return PropertyAccessor.getProperty(this, key);
    }

}
