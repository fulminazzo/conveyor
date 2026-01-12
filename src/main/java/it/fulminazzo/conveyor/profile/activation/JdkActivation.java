package it.fulminazzo.conveyor.profile.activation;

import it.fulminazzo.conveyor.profile.activation.context.ActivationContext;
import org.jetbrains.annotations.NotNull;

/**
 * An {@link Activation} that will check the context JDK version.
 *
 * @param jdk the jdk
 */
record JdkActivation(@NotNull String jdk) implements Activation {

    @Override
    public boolean isEnabled(final @NotNull ActivationContext context) {
        final @NotNull String current = context.getJdkVersion();
        String jdk = this.jdk;
        boolean negated = jdk.startsWith(NEGATION);
        if (negated) jdk = jdk.substring(1);

        if (jdk.startsWith("[") || jdk.startsWith("("))
            throw new UnsupportedOperationException("Ranges");

        if (current.startsWith(jdk)) return !negated;
        else return negated;
    }

}
