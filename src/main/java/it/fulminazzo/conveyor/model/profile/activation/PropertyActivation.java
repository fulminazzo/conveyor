package it.fulminazzo.conveyor.model.profile.activation;

import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
import lombok.Builder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link Activation} based on the existence of a property and its value.
 *
 * @param name  the name of the property
 * @param value the value of the property (if <code>null</code>, any value)
 */
@Builder
record PropertyActivation(@NotNull String name, @Nullable String value) implements Activation {
    private static final @NotNull String packaging = "packaging";

    @Override
    public boolean isEnabled(final @NotNull ActivationContext context) {
        String actualName = this.name;
        boolean negatedName = actualName.startsWith(NEGATION);
        if (negatedName) actualName = actualName.substring(1);

        @Nullable String actualValue = this.value;
        final boolean negatedValue;
        if (actualValue != null) {
            negatedValue = actualValue.startsWith(NEGATION);
            if (negatedValue) actualValue = actualValue.substring(1);
        } else negatedValue = false;

        String propertyValue = actualName.equals(packaging) ? context.getPackaging() : context.getProperty(actualName);
        if (negatedName) return propertyValue == null;
        if (actualValue == null) return true;

        boolean matches = actualValue.equals(propertyValue);
        if (negatedValue) return !matches;
        else return matches;
    }

}
