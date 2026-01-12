package it.fulminazzo.conveyor.profile.activation;

import it.fulminazzo.conveyor.profile.activation.context.ActivationContext;
import lombok.Value;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * An {@link Activation} based on the existence of files.
 * <br>
 * If {@link #exists} is present, will check for the existence of the file.
 * If not found, it will not enable.
 * <br>
 * If {@link #missing} is present, will check for the existence of the file.
 * If found, it will not enable.
 */
@Value
class FileActivation implements Activation {
    @Nullable String exists;
    @Nullable String missing;

    @Override
    public boolean isEnabled(final @NotNull ActivationContext context) {
        File workDir = context.getCurrentDir();
        if (this.exists != null) {
            File first = new File(workDir, context.applyProperties(this.exists));
            if (!first.exists()) return false;
        }

        if (this.missing != null) {
            File second = new File(workDir, context.applyProperties(this.missing));
            if (second.exists()) return false;
        }

        return true;
    }

}
