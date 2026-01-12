package it.fulminazzo.conveyor.profile.activation;

import it.fulminazzo.conveyor.profile.activation.context.ActivationContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * An {@link Activation} based on the existence of files.
 *
 * @param exists if present, will check for the existence of the file.
 *               If not found, it will not enable.
 * @param missing if present, will check for the existence of the file.
 *               If found, it will not enable.
 */
record FileActivation(@Nullable String exists, @Nullable String missing) implements Activation {

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
