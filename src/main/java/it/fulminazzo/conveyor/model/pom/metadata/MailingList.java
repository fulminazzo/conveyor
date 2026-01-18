package it.fulminazzo.conveyor.model.pom.metadata;

import lombok.Builder;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedList;
import java.util.List;

@Value
@Builder
public class MailingList {

    @Nullable String name;

    @Nullable String subscribe;

    @Nullable String unsubscribe;

    @Nullable String post;

    @Nullable String archive;

    @Builder.Default
    @NotNull List<String> otherArchives = new LinkedList<>();

}
