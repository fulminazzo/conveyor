package it.fulminazzo.conveyor.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

/**
 * A collection of utilities to work with strings
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

    /**
     * Attempts to find the common suffix of the given strings.
     *
     * @param target the target string
     * @param suffix the string to lookup for suffix
     * @return the index of the start of the suffix in the given target string
     */
    public static int findCommonSuffix(final @NotNull String target,
                                       final @NotNull String suffix) {
        int i = target.length() - 1, j = suffix.length() - 1;
        int count = 0;

        while (i >= 0 && j >= 0 && target.charAt(i) == suffix.charAt(j)) {
            i--;
            j--;
            count++;
        }

        int result = target.length() - count;
        if (result == 0 && suffix.length() > target.length()) result = -1;
        return result;
    }

}
