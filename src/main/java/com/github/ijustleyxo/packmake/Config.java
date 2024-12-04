package com.github.ijustleyxo.packmake;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public record Config (
    @NotNull List<Integer> formats, // Formats to compile
    @NotNull List<String> extensions // Allowed file extensions
) {
    public static @NotNull Config load(@NotNull File file) throws ConfigLoadException {
        Object yml;

        try {
            Yaml loader = new Yaml();
            yml = loader.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new ConfigLoadException(file, "Exception while reading file", e);
        }

        try {
            HashMap<String, Object> fields = (HashMap<String, Object>) yml;
            if (fields == null) return new Config(new LinkedList<>(), new LinkedList<>());
            List<Integer> formats = (List<Integer>) fields.get("formats");
            if (formats == null) formats = new LinkedList<>();
            List<String> extensions = (List<String>) fields.get("extensions");
            if (extensions == null) extensions = new LinkedList<>();
            return new Config(formats, extensions);
        } catch (ClassCastException e) {
            throw new ConfigLoadException(file, "Invalid config format", e);
        }
    }

    public static final class ConfigLoadException extends Exception {
        public ConfigLoadException(@NotNull File file, @NotNull String message, @NotNull Throwable cause) {
            super("Failed to load config from " + file + ": " + message, cause);
        }
    }
}
