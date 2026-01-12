package it.fulminazzo.conveyor.profile.activation;

import it.fulminazzo.conveyor.profile.activation.context.ActivationContext;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Activation} with a boolean to check for enabled.
 */
@Value
class BooleanActivation implements Activation {
    boolean enabled;

    @Override
    public boolean isEnabled(final @NotNull ActivationContext context) {
        return this.enabled;
    }

}
