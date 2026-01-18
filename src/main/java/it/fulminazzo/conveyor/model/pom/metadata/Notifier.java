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
    boolean sendOnerror = true;

    @Builder.Default
    boolean sendOnFailure = true;

    @Builder.Default
    boolean sendOnSuccess = true;

    @Builder.Default
    boolean sendOnWarning = true;

    @Nullable String address;

    @Builder.Default
    @NotNull Map<String, String> configuration = new HashMap<>();

}
