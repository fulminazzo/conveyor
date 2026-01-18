package it.fulminazzo.conveyor.model.pom.metadata;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Value
@Builder
public class Notifier {

    @Builder.Default
    @NotNull String type = "mail";

    @Builder.Default
    @NotNull String sendOnError = Boolean.TRUE.toString();

    @Builder.Default
    @NotNull String sendOnFailure = Boolean.TRUE.toString();

    @Builder.Default
    @NotNull String sendOnSuccess = Boolean.TRUE.toString();

    @Builder.Default
    @NotNull String sendOnWarning = Boolean.TRUE.toString();

    @Nullable String address;

    @Builder.Default
    @NotNull Map<String, String> configuration = new HashMap<>();

}
