package com.github.ijustleyxo.packmake;

import com.googlecode.pngtastic.core.PngImage;
import com.googlecode.pngtastic.core.PngOptimizer;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public final class Util {
    /**
     * Compress a png file with configured settings. Ignores files that do not ent with ".png".
     * @param file The file to compress
     */
    public static void compress(@NotNull File file) {
        if (!file.getName().toLowerCase().endsWith(".png")) return;

        try {
            new PngOptimizer()
                    .optimize(new PngImage(Files.newInputStream(file.toPath())))
                    .writeDataOutputStream(Files.newOutputStream(file.toPath()));
            System.out.println("Compressed " + file);
        } catch (IOException e) {
            System.out.println("Failed to compress " + file);
        }
    }

    public static void zip(@NotNull File[] files, @NotNull File target) {
        try {
            ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(target.toPath()));
            if (files != null) for (File f : files) addEntry(zip, f, "/");
            zip.close();
        } catch (IOException e) {
            System.out.println("Failed to zip " + Arrays.toString(files));
        }
    }

    private static void addEntry(@NotNull ZipOutputStream zip, @NotNull File file, @NotNull String path) throws IOException {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) return;
            for (File f : files) addEntry(zip, f, path + file.getName() + "/");
            return;
        }

        zip.putNextEntry(new ZipEntry(path + file.getName())); // Add file meta

        FileInputStream input = new FileInputStream(file); // Copy file
        byte[] bytes = new byte[1024];
        int length;
        while (true) {
            length = input.read(bytes);
            if (length <= 0) break;
            zip.write(bytes, 0, length);
        }
        input.close();
    }

    /**
     * Recursively deletes files
     * @param file The file / folder to delete
     */
    public static void delete(@NotNull File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) for (File f : files) delete(f);
        }

        file.delete();
    }
}
