package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.profile.Profile;
import it.fulminazzo.conveyor.model.profile.activation.context.ActivationContext;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    private @Nullable EffectivePomBuilder parentEffectivePomBuilder;

    /**
     * Populates some of the fields of this builder.
     * Used by the builder itself to fetch information
     * from other artifacts.
     *
     * @return this builder
     */
    @NotNull EffectivePomBuilder buildIncomplete() {
        return populateActiveProfiles()
                .resolveParentEffectivePom();
    }

    /**
     * If the {@link #startingPom} contains a parent {@link Artifact},
     * populates the {@link #parentEffectivePomBuilder} with a new builder
     * with incomplete information (but enough to complete the building
     * of the current builder).
     *
     * @return this builder
     */
    @NotNull EffectivePomBuilder resolveParentEffectivePom() {
        this.parentEffectivePomBuilder = null;
        Artifact parent = this.startingPom.getParent();
        if (parent != null) {
            Pom parentPom = this.pomResolver.resolve(parent);
            this.parentEffectivePomBuilder = newBuilder(parentPom).buildIncomplete();
        }
        return this;
    }

    /**
     * Loads all the profiles of the given {@link #startingPom}
     * whose conditions are met under the given context.
     *
     * @return this builder
     */
    @NotNull EffectivePomBuilder populateActiveProfiles() {
        this.activeProfiles.clear();
        this.startingPom.getProfiles().stream()
                .filter(p -> p.getActivation().isEnabled(this.context))
                .forEach(this.activeProfiles::add);
        return this;
    }

    private @NotNull EffectivePomBuilder newBuilder(final Pom pom) {
        return new EffectivePomBuilder(pom, this.pomResolver, this.context);
    }

}
