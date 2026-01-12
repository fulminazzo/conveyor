package it.fulminazzo.conveyor.profile.activation.context;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Represents the current context where an {@link it.fulminazzo.conveyor.profile.activation.Activation} is checked.
 */
public interface ActivationContext {

    /**
     * Applies all the properties of the current context to the given string.
     * <br>
     * Supports three types:
     * <ol>
     *     <li>system properties (provided by the <b>JVM</b>), like <code>${user.home}</code>;</li>
     *     <li>environment properties with the prefix <code>env.</code>, like <code>${env.JAVA_HOME}</code>;</li>
     *     <li>three maven special properties:
     *          <ul>
     *              <li><code>${basedir}</code></li>
     *              <li><code>${project.basedir}</code></li>
     *              <li><code>${maven.multiModuleProjectDirectory}</code></li>
     *          </ul>
     *     </li>
     * </ol>
     *
     * @param string the string
     * @return the string
     */
    @NotNull String applyProperties(final @NotNull String string);

    /**
     * Gets the value of a property.
     * <br>
     * Supports three types:
     * <ol>
     *     <li>system properties (provided by the <b>JVM</b>), like <code>${user.home}</code>;</li>
     *     <li>environment properties with the prefix <code>env.</code>, like <code>${env.JAVA_HOME}</code>;</li>
     *     <li>three maven special properties:
     *          <ul>
     *              <li><code>${basedir}</code></li>
     *              <li><code>${project.basedir}</code></li>
     *              <li><code>${maven.multiModuleProjectDirectory}</code></li>
     *          </ul>
     *     </li>
     * </ol>
     *
     * @param name the name of the property
     * @return the value, <code>null</code> if not defined, <code>true</code> if defined with no value
     */
    @Nullable String getProperty(final @NotNull String name);

    /**
     * Gets the packaging defined in the associated <b>pom.xml</b> file.
     *
     * @return the packaging
     */
    @NotNull String getPackaging();

    /**
     * Gets the base directory of the context.
     *
     * @return the current dir
     */
    @NotNull File getCurrentDir();

}
