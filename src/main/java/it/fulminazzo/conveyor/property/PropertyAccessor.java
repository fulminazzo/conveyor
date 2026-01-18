package it.fulminazzo.conveyor.property;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class to access fields and methods of a given object.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PropertyAccessor {

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
     * @param object the object to search
     * @param key    the key associated with the property
     * @return the property
     */
    public static @Nullable String getProperty(final @NotNull Object object,
                                               final @NotNull String key) {
        throw new UnsupportedOperationException();
    }

}
