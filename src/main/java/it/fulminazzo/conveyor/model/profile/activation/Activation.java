package it.fulminazzo.conveyor.model.profile.activation;

import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
import org.jetbrains.annotations.NotNull;

/**
 * A condition to <b>enable</b> or <b>disable</b> a {@link it.fulminazzo.conveyor.profile.Profile}.
 */
public interface Activation {
    @NotNull String NEGATION = "!";

    /**
     * Checks if the expected condition is met.
     *
     * @param context the context of activation
     * @return true if it is
     */
    boolean isEnabled(final @NotNull ActivationContext context);

}
