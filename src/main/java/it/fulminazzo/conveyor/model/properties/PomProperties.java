package it.fulminazzo.conveyor.model.properties;

import it.fulminazzo.conveyor.model.pom.Pom;
import it.fulminazzo.conveyor.property.PropertyAccessorException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * An implementation of {@link Properties} that provides
 * all the properties of the given {@link Pom}.
 */
@RequiredArgsConstructor
final class PomProperties extends BaseProperties {
    private static final @NotNull List<String> prefixes = Arrays.asList("project.", "pom.");

    private final @NotNull Pom pom;
    private final @NotNull File workingDir;

    @Override
    public @Nullable String get(@NotNull String key) {
        if (key.equals("maven.multiModuleProjectDirectory"))
            return getPomDirectory().getAbsolutePath();
        for (String prefix : prefixes)
            if (key.startsWith(prefix)) {
                key = key.substring(prefix.length());
                break; // avoid invalid chaining of prefixes
            }
        try {
            // invalid prefix
            for (String prefix : prefixes)
                if (key.startsWith(prefix))
                    return null;
            if (key.equals("basedir"))
                return getPomDirectory().getAbsolutePath();
            else if (key.equals("baseUri"))
                return getPomDirectory().toURI().toString();
            else return this.pom.getProperty(key);
        } catch (PropertyAccessorException e) {
            return null;
        }
    }

    /**
     * Gets the directory where the {@link #pom} should be stored,
     * relative to the {@link #workingDir}.
     *
     * @return the pom directory
     */
    public @NotNull File getPomDirectory() {
        return new File(this.workingDir, this.pom.getProject().getFullPath("pom")).getParentFile();
    }

}
