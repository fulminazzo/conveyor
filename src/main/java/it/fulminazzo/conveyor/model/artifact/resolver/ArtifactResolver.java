package it.fulminazzo.conveyor.model.artifact.resolver;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public interface ArtifactResolver {

    @NotNull File resolve(final @NotNull Artifact artifact);

}
