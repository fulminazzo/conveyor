package it.fulminazzo.conveyor.profile.activation;

import it.fulminazzo.conveyor.profile.activation.context.ActivationContext;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An {@link Activation} that will check the context JDK version.
 *
 * @param jdk the jdk
 */
record JdkActivation(@NotNull String jdk) implements Activation {

    @Override
    public boolean isEnabled(final @NotNull ActivationContext context) {
        final @NotNull String current = context.getJdkVersion();
        String jdk = this.jdk;
        boolean negated = jdk.startsWith(NEGATION);
        if (negated) jdk = jdk.substring(1);

        if (jdk.startsWith("[") || jdk.startsWith("(")) {
            if (verifyRange(current, jdk)) return !negated;
            else return negated;
        }

        if (current.startsWith(jdk)) return !negated;
        else return negated;
    }

    private boolean verifyRange(final @NotNull String current,
                                final @NotNull String jdk) {
        Matcher matcher = Pattern.compile("([\\[(])([^,]*),([^])]*)([])])").matcher(jdk);
        if (matcher.find()) {
            boolean leftInclusive = matcher.group(1).equals("[");
            String leftVersion = matcher.group(2);
            int left = leftVersion.isEmpty() ? 1 : compareVersion(current, leftVersion);

            if (left < 0 || (!leftInclusive && left == 0)) return false;

            boolean rightInclusive = matcher.group(4).equals("]");
            String rightVersion = matcher.group(3);
            int right = rightVersion.isEmpty() ? -1 : compareVersion(current, rightVersion);

            return right <= 0 && (rightInclusive || right != 0);
        } else throw new IllegalArgumentException(String.format("Invalid range '%s'", this.jdk));
    }

    private int compareVersion(final @NotNull String version1,
                               final @NotNull String version2) {
        final String regex = "[._\\-+]";
        String[] split1 = version1.split(regex);
        String[] split2 = version2.split(regex);

        int length = Math.max(split1.length, split2.length);
        for (int i = 0; i < length; i++) {
            int n1 = i < split1.length ? parseToken(split1[i]) : 0;
            int n2 = i < split2.length ? parseToken(split2[i]) : 0;
            if (n1 != n2) return Integer.compare(n1, n2);
        }
        return 0;
    }

    private int parseToken(final @NotNull String token) {
        try {
            return Integer.parseInt(token.replaceAll("\\D", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

}
