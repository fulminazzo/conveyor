package it.fulminazzo.conveyor.model.repository.update;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Enum implementation of {@link UpdatePolicy}.
 */
enum UpdatePolicyEnum implements UpdatePolicy {
    /**
     * Always updates the libraries.
     */
    ALWAYS,
    /**
     * Updates the libraries once a day.
     */
    DAILY,
    /**
     * Never updates the libraries.
     */
    NEVER;

    @Override
    public boolean shouldUpdate(final long lastUpdate) {
        return switch (this) {
            case ALWAYS -> true;
            case NEVER -> false;
            default -> {
                LocalDate date = Instant.ofEpochMilli(lastUpdate)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate();
                LocalDate now = LocalDate.now();
                yield !date.equals(now);
            }
        };
    }

}
