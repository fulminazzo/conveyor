package it.fulminazzo.conveyor.model.metadata;

import it.fulminazzo.conveyor.model.repository.RawRepository;
import lombok.*;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@Getter
@EqualsAndHashCode
@ToString
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@SuperBuilder
public class MavenModelMetadata {

    @Builder.Default
    @NotNull List<String> modules = new LinkedList<>();

    @Nullable DistributionManagement distributionManagement;

    @Builder.Default
    @NotNull Set<RawRepository> pluginRepositories = new HashSet<>();

    @Builder.Default
    @NotNull Reporting reporting = Reporting.builder().build();

}
