package com.github.ijustleyxo.packmake;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static com.github.ijustleyxo.packmake.Util.*;

/**
 * Minecraft resource pack compiler
 */
public final class PackMake {
    private static final @NotNull File CONF = new File("./pm.yml");

    public static void main(String[] args) throws Config.ConfigLoadException {
        System.out.println("Loading configuration");
        Config config = Config.load(CONF, args);
        config.src().mkdirs();
        delete(config.target());

        System.out.println("Compiling selected formats");
        File[] files = config.src().listFiles();
        if (files == null) return;
        for (File file : files) make(
                config, config.formats(),
                null, null,
                config.src(), new File(""), new File(file.getName()),
                config.target(), new File(""));

        System.out.println("Including special files");
        for (File file : config.include()) {
            for (Integer format : config.formats()) try {
                Files.copy(file.toPath(), new File(config.target(), format + "/" + file.getName()).toPath());
            } catch (IOException e) {
                System.out.println("Failed to copy included file " + file + " to " + new File(config.target(), format + "/").getPath());
            }
        }

        System.out.println("Running some checks");
        for (Integer format : config.formats()) {
            if (!new File(config.target(), format + "/pack.mcmeta").exists())
                System.out.println("Format " + format + " has no pack.mcmeta so it can't be loaded in Minecraft");
        }

        System.out.println("Packaging selected formats");
        for (Integer format : config.formats()) {
            File folder = new File(config.target(), format + "/");
            zip(folder.listFiles(), new File(config.target(), config.name() + "-" + config.version() + "-" + format + ".zip"));
            delete(folder);
        }
    }

    private static void make(
            @NotNull Config config, @NotNull List<Integer> formats,
            @Nullable Byte lower, @Nullable Byte upper,
            @NotNull File srcBase, @NotNull File srcFolder, @NotNull File file,
            @NotNull File targetBase, @NotNull File targetFolder) {
        @NotNull String simpleName = file.getName();
        int start = simpleName.indexOf('#'); // Start of config
        if (0 <= start) {
            int i = start + 1; // Skip '#'
            Duo<Byte, Integer> first = parseByte(simpleName, i); // Parse first bound
            if (first != null) {
                i = first.b();
                byte bound = first.a();

                char sep = i < simpleName.length() ? simpleName.charAt(i) : '\0';
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
                        else System.out.println("Warning: Upper bound " + alternate + " is redundant for " + file);
                        i = second.b();
                    }
                } else {
                    asUpper = true;
                }

                if (asLower) {
                    if (lower == null || lower < bound) lower = bound;
                    else System.out.println("Warning: Lower bound " + bound + " is redundant for " + file);
                }

                if (asUpper) {
                    if (upper == null || bound < upper) upper = bound;
                    else System.out.println("Warning: Upper bound " + bound + " is redundant for " + file);
                }
            } else {
                System.out.println("Warning: Expecting <byte> after \"#\" for " + file);
            }

            simpleName = simpleName.substring(0, start) + simpleName.substring(i); // Remove config from name
        }
        
        if (simpleName.isEmpty()) {
            System.out.println("Warning: Empty file name for " + file);
            return;
        }

        @Nullable Byte fLower = lower; // Filter which pack formats are still in range
        @Nullable Byte fUpper = upper;
        List<Integer> selected = formats.stream()
                .filter(f -> (fLower == null || fLower <= f) && (fUpper == null || f <= fUpper)).toList();
        if (selected.isEmpty()) { // Nothing to do
            System.out.println("Warning: No formats in range for " + file);
            return;
        }

        File src = new File(srcBase + "/" + srcFolder + "/" + file);

        if (src.isDirectory()) { // Descend into directory
            File[] files = src.listFiles();

            if (files == null || files.length == 0) {
                System.out.println("Warning: Folder " + file + " is empty");
                return;
            }

            targetFolder = new File(targetFolder + "/" + simpleName);
            srcFolder = new File(srcFolder + "/" + file);

            for (File f : files) make(
                    config, selected,
                    lower, upper,
                    srcBase, srcFolder, new File(f.getName()),
                    targetBase, targetFolder);
        } else { // Copy file to packs
            compress(src); // Automatically ignores files that don't end in ".png".

            @NotNull String finalSimpleName = simpleName;
            if (config.extensions().stream().filter(finalSimpleName::endsWith).toList().isEmpty()) {
                System.out.println("Warning: File " + file + " does not end with a valid extension");
            }

            for (Integer format : selected) {
                File destFolder = new File(targetBase + "/" + format + "/" + targetFolder);
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