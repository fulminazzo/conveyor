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

    private final @NotNull Set<Profile> activeProfiles = new HashSet<>();

    /**
     * Loads all the profiles of the given {@link #startingPom}
     * whose conditions are met under the given context.
     *
     * @param context the context
     */
    void populateActiveProfiles(final @NotNull ActivationContext context) {
        this.activeProfiles.clear();
        this.startingPom.getProfiles().stream()
                .filter(p -> p.getActivation().isEnabled(context))
                .forEach(this.activeProfiles::add);
    }

}
