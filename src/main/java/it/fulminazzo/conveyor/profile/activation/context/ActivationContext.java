package it.fulminazzo.conveyor.profile.activation.context;

import org.jetbrains.annotations.NotNull;

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
     * Gets the base directory of the context.
     *
     * @return the current dir
     */
    @NotNull File getCurrentDir();

}
