package it.fulminazzo.conveyor.model.pom.resolver.engine;

import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Represents the type of available {@link PomResolverEngine}s.
 */
@RequiredArgsConstructor
public enum PomResolveEngineType {
    /**
     * A {@link PomResolverEngine} that always downloads the pom data for each request.
     */
    MEMORY(MemoryDownloaderPomResolverEngine.class),
    /**
     * A {@link PomResolverEngine} that checks if the pom exists on disk
     * and returns it before attempting to download it again.
     */
    DISK(DiskDownloaderPomResolverEngine.class),
    /**
     * A {@link PomResolverEngine} that verifies the stored pom version
     * with a checksum before attempting to download it again.
     */
    CHECKSUM(ChecksumDownloaderPomResolverEngine.class)
    ;

    private final @NotNull Class<? extends PomResolverEngine> type;

    /**
     * Creates a new pom resolver engine of the current type.
     *
     * @param workingDir the working dir
     * @param logger     the logger
     * @return the pom resolver engine
     */
    public @NotNull PomResolverEngine create(final @NotNull File workingDir,
                                             final @NotNull Logger logger) {
        try {
            Constructor<?> constructor = this.type.getDeclaredConstructor(workingDir.getClass(), logger.getClass());
            constructor.setAccessible(true);
            return (PomResolverEngine) constructor.newInstance(workingDir, logger);
        } catch (InvocationTargetException | IllegalAccessException | InstantiationException | NoSuchMethodException e) {
            // should be impossible
            throw new RuntimeException(e);
        }
    }

}
