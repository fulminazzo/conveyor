package it.fulminazzo.conveyor.model.properties.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A collection of utilities to work with Linux based operating systems.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LinuxUtils {
    private static final @NotNull Collection<String> releaseFiles = Arrays.asList("/etc/os-release", "/usr/lib/os-release");
    private static final @NotNull String redhatReleaseFile = "/etc/redhat-release";

    private static final @NotNull String idPrefix = "ID=";
    private static final @NotNull String idLikePrefix = "ID_LIKE=";
    private static final @NotNull String versionIdPrefix = "VERSION_ID=";

    private static final @NotNull Pattern redhatMajorVersionRegex = Pattern.compile("(\\d+)");
    private static final @NotNull Collection<String> defaultRedhatVariants = Arrays.asList("rhel", "fedora");

    /**
     * Attempts to return the release of the current version
     * of Linux of the running operating system.
     *
     * @return the current release (if on Linux)
     */
    public static @NotNull Optional<Release> getCurrentRelease() {
        for (String file : releaseFiles) {
            Release release = parseReleaseFile(file);
            if (release != null) return Optional.of(release);
        }
        return Optional.ofNullable(parseRedhatReleaseFile(redhatReleaseFile));
    }

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
     * Parses a file in the format of <code>/etc/redhat-release</code> and
     * returns the corresponding {@link Release}, with data fetched
     * from <code>ID</code> and <code>VERSION_ID</code> entries.
     * Like are taken from {@link #defaultRedhatVariants}
     */
    static @Nullable Release parseRedhatReleaseFile(final @NotNull String fileName) {
        try (InputStreamReader streamReader = new FileReader(fileName, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(streamReader)) {
            String line = reader.readLine();
            if (line != null) {
                line = line.toLowerCase();

                final String id;
                if (line.contains("centos")) id = "centos";
                else if (line.contains("fedora")) id = "fedora";
                else if (line.contains("red hat enterprise linux")) id = "rhel";
                else return null;

                final String version;
                final Matcher matcher = redhatMajorVersionRegex.matcher(line);
                if (matcher.find()) version = matcher.group(1);
                else version = null;

                return new Release(id, version, Set.copyOf(defaultRedhatVariants));
            }
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
