package it.fulminazzo.conveyor.model.pom;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * Responsible for creating a {@link EffectivePom} object.
 */
@RequiredArgsConstructor
final class EffectivePomBuilder {
    private final @NotNull Pom startingPom;
    private final @NotNull PomResolver pomResolver;

}
