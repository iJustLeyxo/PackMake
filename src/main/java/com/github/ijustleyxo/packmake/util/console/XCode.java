package com.github.ijustleyxo.packmake.util.console;

import org.jetbrains.annotations.NotNull;

/**
 * ANSI escape codes, used to format text outputted to most modern terminal emulators
 */
public enum XCode {
    RESET("\u001B[0m"),
    BOLD("\u001B[1m"),
    WEIGHT_OFF("\u001B[22m"),
    GRAY("\u001B[90m"),
    RED("\u001B[91m"),
    GREEN("\u001B[92m"),
    YELLOW("\u001B[93m"),
    BLUE("\u001B[94m"),
    MAGENTA("\u001B[95m"),
    WHITE("\u001B[97m");

    private final @NotNull String code;

    XCode(@NotNull String code) {
        this.code = code;
    }

    @Override
    public @NotNull String toString() {
        return this.code;
    }
}
