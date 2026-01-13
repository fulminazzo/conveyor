package it.fulminazzo.conveyor.model.repository.update;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Represents the <code>interval</code> update policy.
 */
record IntervalUpdatePolicy(long interval) implements UpdatePolicy {

    @Override
    public boolean shouldUpdate(final long lastUpdate) {
        LocalDateTime time = Instant.ofEpochMilli(lastUpdate)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        return time.plusMinutes(this.interval).isBefore(now);
    }

}
