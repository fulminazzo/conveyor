package it.fulminazzo.conveyor.property;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

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

    /**
     * Attempts to get a property with the given name.
     * Then, if it is a {@link Collection}, it will return the object at the given index.
     * If it is not a {@link Collection}, an exception will be returned.
     *
     * @param object   the object
     * @param name     the name
     * @param rawIndex the raw index
     * @return the value
     */
    static @Nullable Object getIndexed(final @NotNull Object object,
                                       final @NotNull String name,
                                       final @NotNull String rawIndex) {
        return getIndexed(object, name, Integer.parseInt(rawIndex));
    }

    /**
     * Attempts to get a property with the given name.
     * Then, if it is a {@link Collection}, it will return the object at the given index.
     * If it is not a {@link Collection}, an exception will be returned.
     *
     * @param object the object
     * @param name   the name
     * @param index  the index
     * @return the value
     */
    static @Nullable Object getIndexed(final @NotNull Object object,
                                       final @NotNull String name,
                                       final int index) {
        Object o = getObject(object, name);
        if (o instanceof Collection<?> collection) {
            List<?> list = collection.stream().toList();
            return list.get(index);
        }
        throw new IllegalArgumentException(String.format("Property %s.%s = %s is not an indexable object", object, name, o));
    }

    /**
     * Attempts to get a field with the given name from the object.
     * If it fails, it will try to invoke a method with the given name
     * and no parameters.
     *
     * @param object the object
     * @param name   the name
     * @return the result object
     */
    static @Nullable Object getObject(final @NotNull Object object,
                                      final @NotNull String name) {
        Object obj = getField(object, name);
        if (obj == null) obj = invokeMethod(object, name);
        return obj;
    }

    /**
     * Attempts to get a field with the given name from the object.
     * If its value is <code>null</code> or the field is not found,
     * then <code>null</code> is returned.
     *
     * @param object the object
     * @param name   the name
     * @return the field value
     */
    static @Nullable Object getField(final @NotNull Object object,
                                     final @NotNull String name) {
        try {
            Field field = object.getClass().getDeclaredField(name);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Attempts to invoke a method with the given name and no parameters from the object.
     * If its value is <code>null</code> or the method is not found,
     * then <code>null</code> is returned.
     *
     * @param object the object
     * @param name   the name
     * @return the method return
     */
    static @Nullable Object invokeMethod(final @NotNull Object object,
                                         final @NotNull String name) {
        try {
            Method method = object.getClass().getDeclaredMethod(name);
            method.setAccessible(true);
            return method.invoke(object);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            return null;
        }
    }

}
