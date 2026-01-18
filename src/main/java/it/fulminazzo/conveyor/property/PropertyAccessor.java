package it.fulminazzo.conveyor.property;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.*;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A utility class to access fields and methods of a given object.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PropertyAccessor {
    private static final Pattern propertyPattern = Pattern.compile("^([a-zA-Z_].*)\\.([a-zA-Z_][^.]*)");
    private static final Pattern indexPattern = Pattern.compile("^([a-zA-Z_][^.]*)\\[(\\d+)]");

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
        Object property = getObject(object, key);
        return property == null ? null : property.toString();
    }

    private static @Nullable Object getObject(final @NotNull Object object,
                                              @NotNull String name) {
        Matcher matcher = propertyPattern.matcher(name);
        if (matcher.find()) {
            name = matcher.group(1);
            return getSubProperty(object, name, matcher.group(2));
        }
        matcher = indexPattern.matcher(name);
        if (matcher.find()) {
            name = matcher.group(1);
            return getIndexed(object, name, matcher.group(2));
        }
        Object obj = getField(object, name);
        if (obj == null) obj = invokeMethod(object, name);
        return obj;
    }

    /**
     * Attempts to get a property with the given name.
     * Then, it will get the property with the other name
     * from the first result.
     *
     * @param object          the object
     * @param name            the name
     * @param subPropertyName the sub property name
     * @return the value
     */
    static @Nullable Object getSubProperty(final @NotNull Object object,
                                           final @NotNull String name,
                                           final @NotNull String subPropertyName) {
        Object obj = getObject(object, name);
        if (obj == null)
            throw new NullPointerException(String.format("Could not get '%s' from %s.%s", subPropertyName, object, name));
        return getObject(obj, subPropertyName);
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
        if (o != null && o.getClass().isArray()) return Array.get(o, index);
        if (o instanceof Collection<?> collection) return collection.stream().toList().get(index);
        throw new IllegalArgumentException(String.format("Property %s.%s = %s is not an indexable object", object, name, o));
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
            Field field = getDeclaredField(object.getClass(), name);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            List<Field> fields = getDeclaredFields(object.getClass()).stream()
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    .filter(f -> !f.getName().equals(name)) // ignore previously looked up field
                    .filter(f -> f.isAnnotationPresent(DelegateProperties.class))
                    .toList();
            for (Field field : fields) {
                try {
                    Object raw = getSubProperty(object, field.getName(), name);
                    if (raw != null) return raw;
                } catch (NullPointerException ignored) {
                }
            }
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

    private static @NotNull Field getDeclaredField(final @NotNull Class<?> clazz,
                                                   final @NotNull String name) throws NoSuchFieldException {
        if (clazz.equals(Object.class))
            throw new NoSuchFieldException(String.format("Could not find field '%s'", name));
        try {
            return clazz.getDeclaredField(name);
        } catch (NoSuchFieldException e) {
            try {
                return getDeclaredField(clazz.getSuperclass(), name);
            } catch (NoSuchFieldException ignored) {
            }
            throw e;
        }
    }

    private static @NotNull Collection<Field> getDeclaredFields(final @NotNull Class<?> clazz) {
        List<Field> fields = new LinkedList<>();
        if (clazz.equals(Object.class)) return fields;
        fields.addAll(getDeclaredFields(clazz.getSuperclass()));
        fields.addAll(List.of(clazz.getDeclaredFields()));
        return fields;
    }

}
