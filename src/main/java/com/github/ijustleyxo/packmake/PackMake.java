package com.github.ijustleyxo.packmake;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

/**
 * Minecraft resource pack compiler
 */
public final class PackMake {
    private static final @NotNull File SRC = new File("./src/");
    private static final @NotNull File TAR = new File("./target/");
    private static final @NotNull File CONF = new File("./pm.yml");

    public static void main(String[] args) throws Config.ConfigLoadException {
        Config config = Config.load(CONF);
        SRC.mkdirs();
        TAR.mkdirs();
        File[] files = SRC.listFiles();
        if (files == null) return;
        for (File file : files) make(
                config, config.formats(),
                null, null,
                TAR, SRC,
                new File(""), new File(""),
                new File(file.getName()));
    }

    private static void make(
            @NotNull Config config, @NotNull List<Integer> formats,
            @Nullable Byte lower, @Nullable Byte upper,
            @NotNull File packBase, @NotNull File packFolder,
            @NotNull File srcBase, @NotNull File srcFolder,
            @NotNull File srcFile) {
        @NotNull String simpleName = srcFile.getName();
        int start = simpleName.indexOf('#'); // Start of config
        if (0 <= start) {
            int i = start + 1; // Skip '#'
            Duo<Byte, Integer> first = parseByte(simpleName, i); // Parse first bound
            if (first != null) {
                i = first.b();
                byte bound = first.a();

                char sep = simpleName.charAt(i);
                Duo<Byte, Integer> second;

                boolean asLower = true;
                boolean asUpper = false;

                if (sep == '+') i++;
                else if (sep == '-') {
                    i++;
                    second = parseByte(simpleName, i); // Parse second bound

                    if (second == null) {
                        asLower = false;
                        asUpper = true;
                    } else {
                        byte alternate = second.a();
                        if (upper == null || alternate < upper) upper = alternate;
                        else System.out.println("Warning: Upper bound " + alternate + " is redundant for " + srcFile);
                        i = second.b();
                    }
                } else {
                    asUpper = true;
                }

                if (asLower) {
                    if (lower == null || lower < bound) lower = bound;
                    else System.out.println("Warning: Lower bound " + bound + " is redundant for " + srcFile);
                }

                if (asUpper) {
                    if (upper == null || bound < upper) upper = bound;
                    else System.out.println("Warning: Upper bound " + bound + " is redundant for " + srcFile);
                }
            } else {
                System.out.println("Warning: Expecting <byte> after \"#\" for " + srcFile);
            }

            simpleName = simpleName.substring(0, start) + simpleName.substring(i); // Remove config from name
        }
        
        if (simpleName.isEmpty()) {
            System.out.println("Warning: Empty file name for " + srcFile);
            return;
        }

        @Nullable Byte fLower = lower; // Filter which pack formats are still in range
        @Nullable Byte fUpper = upper;
        List<Integer> selected = config.formats().stream()
                .filter(f -> (fLower == null || fLower <= f) && (fUpper == null || f <= fUpper)).toList();
        if (selected.isEmpty()) { // Nothing to do
            System.out.println("Warning: No formats in range for " + srcFile);
            return;
        }

        File src = new File(srcBase + "/" + srcFolder + "/" + srcFile);

        if (src.isDirectory()) { // Descend into directory
            File[] files = src.listFiles();

            if (files == null || files.length == 0) {
                System.out.println("Warning: Folder " + srcFile + " is empty");
                return;
            }

            packFolder = new File(packFolder + "/" + simpleName);
            srcFolder = new File(srcFolder + "/" + srcFile);

            for (File f : files) make(
                    config, selected,
                    lower, upper,
                    packBase, packFolder,
                    srcBase, srcFolder,
                    new File(f.getName()));
        } else { // Copy file to packs
            @NotNull String finalSimpleName = simpleName;
            if (config.extensions().stream().filter(finalSimpleName::endsWith).toList().isEmpty()) {
                System.out.println("Warning: File " + srcFile + " does not end with a valid extension");
            }

            for (Integer format : selected) {
                File destFolder = new File(packBase + "/" + format + "/" + packFolder);
                File dest = new File(destFolder + "/" + simpleName);
                if (dest.exists()) System.out.println("Warning: " + src + " is overdefining " + dest);
                else try {
                    destFolder.mkdirs();
                    Files.copy(src.toPath(), dest.toPath());
                } catch (IOException e) {
                    System.out.println("Error: Failed to copy " + src + " to " + dest);
                }
            }
        }
    }

    private static @Nullable Duo<Byte, Integer> parseByte(@NotNull String string, int i) {
        Byte result = null;
        int k = i + 1;
        while (k <= string.length()) try {
            result = Byte.parseByte(string.substring(i, k));
            k++;
        } catch (NumberFormatException e) {
            break;
        }
        if (result == null) return null;
        else return new Duo<>(result, k - 1);
    }
}