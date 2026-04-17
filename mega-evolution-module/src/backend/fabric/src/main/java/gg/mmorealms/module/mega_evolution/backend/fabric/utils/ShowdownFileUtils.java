package gg.mmorealms.module.mega_evolution.backend.fabric.utils;

import com.raduvoinea.utils.logger.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;

public final class ShowdownFileUtils {
    private static final String SHOWDOWN_RESOURCES = "/data/mmorealms/showdown/";

    private ShowdownFileUtils() {}

    public static void loadCustomFiles() {
        Path showdownDir = Path.of("./showdown");
        Path showdownDataDir = showdownDir.resolve("data");
        Path cobblemonModDir = showdownDataDir.resolve("mods/cobblemon");

        appendResourceFile("index.js", showdownDir.resolve("index.js"));
        replaceResourceFile("data/mods/cobblemon/items.js", cobblemonModDir.resolve("items.js"));
        replaceResourceFile("data/mods/cobblemon/abilities.js", cobblemonModDir.resolve("abilities.js"));
    }

    private static void replaceResourceFile(String resourcePath, Path targetPath) {
        try (InputStream inputStream = ShowdownFileUtils.class.getResourceAsStream(SHOWDOWN_RESOURCES + resourcePath)) {
            if (inputStream == null) {
                Logger.info("Resource file not found: " + resourcePath);
                return;
            }

            boolean targetFileExists = Files.exists(targetPath);
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);

            if (targetFileExists) {
                Logger.info("Replaced target file " + targetPath + " with " + resourcePath);
            } else {
                Logger.info("Copied resource file " + resourcePath + " to " + targetPath);
            }

        } catch (IOException e) {
            Logger.info("Failed to copy resource " + resourcePath + ": " + e.getMessage());
        }
    }

    private static void appendResourceFile(String resourcePath, Path targetPath) {
        try (InputStream inputStream = ShowdownFileUtils.class.getResourceAsStream(SHOWDOWN_RESOURCES + resourcePath)) {
            if (inputStream == null) {
                Logger.info("Resource file not found: " + resourcePath);
                return;
            }

            if (!Files.exists(targetPath)) {
                Logger.info("Target file not found: " + targetPath);
            }

            String existingContent = Files.readString(targetPath);
            String newContent = new String(inputStream.readAllBytes());

            if (existingContent.endsWith(newContent)) {
                Logger.info("Skipped appending resource file: " + resourcePath + " to target file " + targetPath);
                return;
            }

            String contentToAppend = (existingContent.endsWith("\n") ? "" : "\n") + newContent;
            Files.writeString(targetPath, contentToAppend, StandardOpenOption.APPEND);

            Logger.info("Appended resource file " + resourcePath + " to " + targetPath);
        } catch (IOException e) {
            Logger.info("Failed to append resource " + resourcePath + ": " + e.getMessage());
        }
    }
}