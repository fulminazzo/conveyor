package it.fulminazzo.conveyor.model.pom;

import it.fulminazzo.conveyor.model.Properties;
import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.dependency.Dependency;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents the actual <b>pom.xml</b> file of a project,
 * with a list of all the required dependencies to make it work.
 */
@Value
@RequiredArgsConstructor
public class EffectivePom implements PomLike {
    @NotNull Artifact project;
    @NotNull String packaging;

    @NotNull Properties properties;
    @NotNull List<Dependency> dependencies;

}
