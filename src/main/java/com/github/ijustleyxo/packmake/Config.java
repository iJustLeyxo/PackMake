package com.github.ijustleyxo.packmake;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import static com.github.ijustleyxo.packmake.Util.gitTag;

public record Config (
        @NotNull String name, // Project name
        @Nullable String version,// Project version (optional, uses console argument or most recent tag on current git branch instead)
        @NotNull File src, // Source directory (optional, defaults to src/)
        @NotNull File target, // Target directory (optional, defaults to target/)
        @NotNull List<Integer> formats, // Formats to compile
        @NotNull List<File> include, // Files to include in every package
        @NotNull List<String> extensions // Allowed file extensions
) {
    public static @NotNull Config load(@NotNull File file, @NotNull String[] args) throws ConfigLoadException {
        Object yml;

        try {
            Yaml loader = new Yaml();
            yml = loader.load(new FileInputStream(file));
        } catch (IOException e) {
            throw new ConfigLoadException(file, "Exception while reading file", e);
        }

        try {
            HashMap<String, Object> fields = (HashMap<String, Object>) yml;

            String name = null;
            String version = null;

            String srcStr = null;
            String targetStr = null;

            List<Integer> formats = null;
            List<String> include = null;
            List<String> extensions = null;

            if (fields != null) {
                name = (String) fields.get("name");
                version = (String) fields.get("version");

                srcStr = (String) fields.get("src");
                targetStr = (String) fields.get("target");

                formats = (List<Integer>) fields.get("formats");
                include = (List<String>) fields.get("include");
                extensions = (List<String>) fields.get("extensions");
            }

            if (name == null) name = "NoName";

            if (args.length > 0) version = args[0];
            else if (version == null) {
                String git = gitTag();
                if (git != null) version = git;
            }

            if (srcStr == null) srcStr = "./src/";
            File src = new File(srcStr);
            if (targetStr == null) targetStr = "./target/";
            File target = new File(targetStr);

            if (formats == null) formats = new LinkedList<>();
            if (include == null) include = new LinkedList<>();
            if (extensions == null) extensions = new LinkedList<>();

            return new Config(name, version, src, target, formats, include.stream().map(File::new).toList(), extensions);
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
