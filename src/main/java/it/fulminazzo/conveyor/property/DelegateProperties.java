package it.fulminazzo.conveyor.property;

import java.lang.annotation.*;

/**
 * Any annotated field will be considered as part
 * of the main object during the research for properties
 * in {@link PropertyAccessor#getProperty(Object, String)}.
 * <br>
 * Consider this example:
 * <pre>
 *     class Person {
 *         String username;
 *         String lastname;
 *         int age;
 *         &#64;DelegateProperties
 *         House houseAddress;
 *     }
 *
 *     class House {
 *         String street;
 *         int civic;
 *     }
 * </pre>
 * When looking for the properties <code>street</code> or <code>civic</code>
 * from <b>Person</b>, they will be immediately found, without requiring
 * to access <code>houseAddress</code>.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DelegateProperties {

}
