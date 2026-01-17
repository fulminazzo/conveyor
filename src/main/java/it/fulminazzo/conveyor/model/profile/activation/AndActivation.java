package it.fulminazzo.conveyor.model.profile.activation;

import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * A special type of {@link Activation} that will check for all
 * the given {@link Activation}s in its {@link #isEnabled(ActivationContext)} method.
 */
@EqualsAndHashCode
@ToString(includeFieldNames = false)
final class AndActivation implements Activation {
    private final @NotNull Map<String, Activation> activations = new HashMap<>();

    @Override
    public boolean isEnabled(final @NotNull ActivationContext context) {
        return !this.activations.isEmpty() && this.activations.values().stream().allMatch(a -> a.isEnabled(context));
    }

    /**
     * Adds a new {@link Activation}.
     *
     * @param id         the identifier of the activation. Must be unique (or will be overridden)
     * @param activation the activation
     */
    public void addActivation(final @NotNull String id, final @NotNull Activation activation) {
        this.activations.put(id, activation);
    }

}
