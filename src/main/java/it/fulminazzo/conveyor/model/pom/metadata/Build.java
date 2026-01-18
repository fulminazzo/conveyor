package it.fulminazzo.conveyor.model.pom.metadata;

import it.fulminazzo.conveyor.model.artifact.Artifact;
import it.fulminazzo.conveyor.model.profile.metadata.BuildBase;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder
public final class Build extends BuildBase {

    @Builder.Default
    @NotNull String sourceDirectory = "src/main/java";

    @Builder.Default
    @NotNull String scriptSourceDirectory = "src/main/scripts";

    @Builder.Default
    @NotNull String testSourceDirectory = "src/test/java";

    @Builder.Default
    @NotNull String outputDirectory = "target/classes";

    @Builder.Default
    @NotNull String testOutputDirectory = "target/test-classes";

    @Builder.Default
    @NotNull List<Artifact> extensions = new LinkedList<>();

}
