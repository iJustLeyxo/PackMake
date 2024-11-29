package com.github.ijustleyxo.packmake;

import com.github.ijustleyxo.packmake.util.console.Console;
import com.github.ijustleyxo.packmake.util.console.Type;

/**
 * Minecraft resource pack compiler
 */
public final class PackMake {
    public static void main(String[] args) {
        // TODO: Resource pack creation logic
        PackMake.exit();
    }

    public static void exit() {
        Console.log(Type.DEBUG, "Exiting\n");
        Console.sep();
        System.exit(0);
    }
}