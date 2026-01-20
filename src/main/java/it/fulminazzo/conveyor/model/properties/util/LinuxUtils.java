package it.fulminazzo.conveyor.model.properties.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A collection of utilities to work with Linux based operating systems.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LinuxUtils {
    private static final String idPrefix = "ID=";
    private static final String idLikePrefix = "ID_LIKE=";
    private static final String versionIdPrefix = "VERSION_ID=";

    /**
     * Parses a file in the format of <code>/etc/os-release</code> and
     * returns the corresponding {@link Release}, with data fetched
     * from <code>ID</code>, <code>ID_LIKE</code> and <code>VERSION_ID</code>
     * entries.
     *
     * @param fileName the release file name
     * @return the release (<code>null</code> if it was not possible
     * to determine the release information)
     */
    static @Nullable Release parseReleaseFile(final @NotNull String fileName) {
        try (InputStreamReader streamReader = new FileReader(fileName, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {

            String id = null;
            String version = null;
            final Set<String> likeSet = new LinkedHashSet<>();

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith(idPrefix)) {
                    line = line.substring(idPrefix.length());
                    line = removeQuotes(line);
                    id = line;
                    likeSet.add(id);
                    continue;
                }

                if (line.startsWith(versionIdPrefix)) {
                    line = line.substring(versionIdPrefix.length());
                    line = removeQuotes(line);
                    version = line;
                    continue;
                }

                if (line.startsWith(idLikePrefix)) {
                    line = line.substring(idLikePrefix.length());
                    line = removeQuotes(line);
                    likeSet.addAll(List.of(line.split("\\s+")));
                }
            }

            if (id != null)
                return new Release(id, version, likeSet);
        } catch (IOException ignored) {
        }
        return null;
    }

    /**
     * Removes any quotes from the given string.
     *
     * @param string the string
     * @return the string without quotes
     */
    static @NotNull String removeQuotes(final @NotNull String string) {
        return string.trim().replace("\"", "");
    }

    /**
     * Represents Linux release data.
     *
     * @param id      the id
     * @param version the version
     * @param like    the like
     */
    public record Release(@NotNull String id,
                          @Nullable String version,
                          @NotNull Collection<String> like) {

    }

}
