package it.fulminazzo.conveyor.model.repository.update;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Represents the <code>interval</code> update policy.
 */
@ToString
@EqualsAndHashCode
@RequiredArgsConstructor
final class IntervalUpdatePolicy implements UpdatePolicy {
    private final long interval;

    @Override
    public boolean shouldUpdate(final long lastUpdate) {
        LocalDateTime time = Instant.ofEpochMilli(lastUpdate)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        return time.plusMinutes(this.interval).isBefore(now);
    }

}
