package it.fulminazzo.conveyor.profile.activation;

import it.fulminazzo.conveyor.profile.activation.context.ActivationContext;
import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Activation} with a boolean to check for enabled.
 * 
 * @param enabled the return of {@link #isEnabled(ActivationContext)}
 */
record BooleanActivation(boolean enabled) implements Activation {

    @Override
    public boolean isEnabled(final @NotNull ActivationContext context) {
        return this.enabled;
    }

}
