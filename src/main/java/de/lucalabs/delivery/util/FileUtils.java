package de.lucalabs.delivery.util;

import de.lucalabs.delivery.SameDayDelivery;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Optional;

public final class FileUtils {

    private static final String dataFileName = "items";

    private static final FilePath[] windowsPaths = {
            new EnvVar("APPDATA"),
            new Relative("AppData\\Roaming")
    };

    private static final FilePath[] linuxPaths = {
            new EnvVar("XDG_DATA_HOME"),
            new Relative(".local/share")
    };

    private static final FilePath[] macPaths = {
            new Relative("Library/Application Support")
    };

    private FileUtils() {
    }

    public static @NotNull File getItemFile() throws IOException {
        FilePath[] osPaths;

        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            osPaths = windowsPaths;
        } else if (os.contains("mac")) {
            osPaths = macPaths;
        } else { // Assume Linux or Unix
            osPaths = linuxPaths;
        }

        Optional<String> path = Arrays.stream(osPaths)
                .map(FilePath::toAbsolutePath)
                .filter(p -> p != null && !p.isEmpty()).findFirst();

        if (path.isPresent()) {
            return getFileForPath(path.get());
        }

        throw new IOException("failed to create data file");
    }

    private static @NotNull File getFileForPath(String path) throws IOException {

        File appDir = new File(path, SameDayDelivery.MOD_ID);
        if (!appDir.exists()) {
            Files.createDirectories(appDir.toPath());
        }

        // Create the file inside the application directory
        return new File(appDir, dataFileName);
    }

    private interface FilePath {
        String toAbsolutePath();
    }

    private record EnvVar(String var) implements FilePath {
        @Override
        public String toAbsolutePath() {
            return System.getenv("APPDATA");
        }
    }

    private record Relative(String path) implements FilePath {
        @Override
        public String toAbsolutePath() {
            return System.getProperty("user.home") + "/" + path;
        }
    }
}
