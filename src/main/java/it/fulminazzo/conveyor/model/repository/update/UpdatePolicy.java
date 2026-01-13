package it.fulminazzo.conveyor.model.repository.update;

import org.jetbrains.annotations.NotNull;

/**
 * Represents the update policy of a repository.
 */
public interface UpdatePolicy {

    /**
     * Checks if the current policy allows updating, given the last update time.
     *
     * @param lastUpdate the last update
     * @return true if it does
     */
    boolean shouldUpdate(final long lastUpdate);

    /**
     * Gets the corresponding update policy of the given raw policy.
     *
     * @param rawPolicy the raw policy
     * @return the update policy
     */
    static @NotNull UpdatePolicy of(final @NotNull String rawPolicy) {
        for (UpdatePolicyEnum updatePolicy : UpdatePolicyEnum.values())
            if (updatePolicy.name().toLowerCase().equals(rawPolicy))
                return updatePolicy;
        if (rawPolicy.startsWith("interval")) {
            String time = rawPolicy.substring("interval".length());
            if (time.startsWith(":")) {
                time = time.substring(1);
                try {
                    long actualTime = Long.parseLong(time);
                    return new IntervalUpdatePolicy(actualTime);
                } catch (NumberFormatException ignored) {
                }
            }
            throw new IllegalArgumentException(
                    String.format("Could not determine time interval from update policy '%s'. ", rawPolicy) +
                    "Format: interval:<minutes>"
            );
        }
        throw new IllegalArgumentException(String.format("Could not find matching update policy of '%s'", rawPolicy));
    }

}
