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

    public static void main(String[] args) {
        Yaml yaml = new Yaml();
        List<Integer> formats = null;
        try {
            formats = yaml.load(new FileInputStream("./pm.yml"));
        } catch (IOException e) {
            System.out.println("Config load from pm.yml failed");
            System.exit(1);
        }
        SRC.mkdirs();
        TAR.mkdirs();
        File[] files = SRC.listFiles();
        if (files == null) return;
        for (File file : files) make(
                null, null,
                    SRC, formats.stream().map(b -> new Duo<>(b, new File(TAR, b + "/"))).toList(),
                    new File(""), new File(""), new File(file.getName()));
    }

    private static void make(
            @Nullable Byte lower, @Nullable Byte upper,
            @NotNull File srcBase, @NotNull List<Duo<Integer, File>> packBase,
            @NotNull File srcFolder, @NotNull File packFolder,
            @NotNull File srcFile) {
        @NotNull String name = srcFile.getName();
        int start = name.indexOf('#'); // Start of config
        if (0 <= start) {
            int i = start + 1; // Skip '#'
            Duo<Byte, Integer> first = parseByte(name, i); // Parse first bound
            if (first != null) {
                i = first.b();
                byte bound = first.a();

                char sep = name.charAt(i);
                Duo<Byte, Integer> second = null;

                boolean asLower = true;
                boolean asUpper = false;

                if (sep == '+') i++;
                else if (sep == '-') {
                    i++;
                    second = parseByte(name, i); // Parse second bound

                    if (second == null) {
                        asLower = false;
                        asUpper = true;
                    } else {
                        byte alternate = second.a();
                        if (upper == null || alternate < upper) upper = alternate;
                        else System.out.println("Config warning: Upper bound " + alternate + " is redundant for " + srcFile);
                        i = second.b();
                    }
                } else {
                    asUpper = true;
                }

                if (asLower) {
                    if (lower == null || lower < bound) lower = bound;
                    else System.out.println("Config warning: Lower bound " + bound + " is redundant for " + srcFile);
                }

                if (asUpper) {
                    if (upper == null || bound < upper) upper = bound;
                    else System.out.println("Config warning: Upper bound " + bound + " is redundant for " + srcFile);
                }
            } else {
                System.out.println("Config warning: Expecting <byte> after \"#\" for " + srcFile);
            }

            name = name.substring(0, start) + name.substring(i); // Remove config from name
        }

        @Nullable Byte fLower = lower; // Filter which pack formats are still in range
        @Nullable Byte fUpper = upper;
        List<Duo<Integer, File>> selected = packBase.stream()
                .filter(d -> (fLower == null || fLower <= d.a()) && (fUpper == null || d.a() <= fUpper)).toList();
        if (selected.isEmpty()) return; // Nothing to do

        File src = new File(srcBase + "/" + srcFolder + "/" + srcFile);

        if (src.isDirectory()) { // Descend into directory

            File[] files = src.listFiles();
            if (files == null || files.length == 0) return;
            for (File f : files) make(
                    lower, upper,
                    srcBase, packBase,
                    new File(srcFolder + "/" + srcFile),
                    new File(packFolder + "/" + name), new File(f.getName()));
        } else { // Copy file to packs
            for (Duo<Integer, File> pack : selected) {
                File destFolder = new File(pack.b() + "/" + packFolder);
                File dest = new File(destFolder + "/" + name);
                if (dest.exists()) System.out.println(src + " is over defining " + dest);
                else try {
                    destFolder.mkdirs();
                    Files.copy(src.toPath(), dest.toPath());
                } catch (IOException e) {
                    System.out.println("Failed to copy " + src + " to " + dest);
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