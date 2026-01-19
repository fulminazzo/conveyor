package it.fulminazzo.conveyor.model.profile.activation.context;

import it.fulminazzo.conveyor.model.profile.activation.Activation;
import it.fulminazzo.conveyor.model.properties.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Represents the current context where an {@link Activation} is checked.
 */
public interface ActivationContext {

    /**
     * Gets the directory of the current project.
     *
     * @return the current dir
     */
    @NotNull File getProjectDir();

    /**
     * Gets the context JDK version.
     *
     * @return the jdk version
     */
    @NotNull String getJdkVersion();

    /**
     * Gets the context Operating System name.
     *
     * @return the os name
     */
    @NotNull String getOsName();

    /**
     * Gets the context Operating System arch.
     *
     * @return the os arch
     */
    @NotNull String getOsArch();

    /**
     * Gets the context Operating System version.
     *
     * @return the os version
     */
    @NotNull String getOsVersion();

    /**
     * Gets the packaging defined in the associated <b>pom.xml</b> file.
     *
     * @return the packaging
     */
    @NotNull String getPackaging();

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
     * Gets a new activation context with the current environment variables and properties.
     *
     * @param properties the properties
     * @return the activation context
     */
    static @NotNull ActivationContext current(final @NotNull MavenProjectProperties properties) {
        return current(properties.toImmutable());
    }

    /**
     * Gets a new activation context with the current environment variables and properties.
     *
     * @param properties the properties
     * @return the activation context
     */
    static @NotNull ActivationContext current(final @NotNull Properties properties) {
        return new CurrentActivationContext(properties);
    }

}
