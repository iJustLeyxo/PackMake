package com.github.ijustleyxo.packmake;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Util {
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
}
