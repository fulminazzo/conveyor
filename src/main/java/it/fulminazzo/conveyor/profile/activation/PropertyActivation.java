package it.fulminazzo.conveyor.profile.activation;

import it.fulminazzo.conveyor.profile.activation.context.ActivationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link Activation} based on the existence of a property and its value.
 *
 * @param name  the name of the property
 * @param value the value of the property (if <code>null</code>, any value)
 */
record PropertyActivation(@NotNull String name, @Nullable String value) implements Activation {

    @Override
    public boolean isEnabled(final @NotNull ActivationContext context) {
        String actualName = this.name;
        boolean matchName = true;
        if (actualName.startsWith(NEGATION)) {
            actualName = actualName.substring(1);
            matchName = false;
        }

        @Nullable String actualValue = this.value;
        boolean matchValue = true;
        if (actualValue != null) {
            if (actualName.startsWith(NEGATION)) {
                actualValue = actualValue.substring(1);
                matchValue = false;
            }
        }

        String propertyValue = context.getProperty(actualName);
        if (propertyValue == null) return !matchName;
        if (actualValue == null) return true;

        boolean matches = actualValue.equals(propertyValue);
        if (matchValue) return matches;
        else return !matches;
    }

}
