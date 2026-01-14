package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.profile.Profile;
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Responsible for creating a {@link EffectivePom} object.
 */
@RequiredArgsConstructor
final class EffectivePomBuilder {
    private final @NotNull Pom startingPom;
    private final @NotNull PomResolver pomResolver;
    private final @NotNull ActivationContext context;

    private final @NotNull Set<Profile> activeProfiles = new HashSet<>();

    /**
     * Loads all the profiles of the given {@link #startingPom}
     * whose conditions are met under the given context.
     */
    void populateActiveProfiles() {
        this.activeProfiles.clear();
        this.startingPom.getProfiles().stream()
                .filter(p -> p.getActivation().isEnabled(this.context))
                .forEach(this.activeProfiles::add);
    }

}
