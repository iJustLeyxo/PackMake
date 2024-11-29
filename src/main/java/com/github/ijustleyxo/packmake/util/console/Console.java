package com.github.ijustleyxo.packmake.util.console;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Console manager, used to format console outputs
 */
public final class Console {
    public static @NotNull Detail detail = Detail.STD;
    private static @Nullable Type type = null;
    private static boolean empty = true;

    /**
     * Logs a message to console
     * @param type Type of message to log
     * @param style Style to override the default style of the specified type
     * @param msg Message to log
     * @return {@code true} if the message was logged and not held back due to verbosity
     */
    public static boolean log(@NotNull Type type, @NotNull Style style, @NotNull String msg) {
        if (!Console.logs(type.detail)) return false;
        Console.sep(type);
        System.out.print(XCode.RESET + style.toString() + msg);
        Console.empty = false;
        return true;
    }

    /**
     * Logs a formatted message to console
     * @param type Type of message to log
     * @param style Style to override the default style of the specified type
     * @param format Format to use for the message
     * @param params Params to format
     * @return {@code true} if the message was logged and not held back due to verbosity
     */
    public static boolean logF(@NotNull Type type, @NotNull Style style, @NotNull String format, @NotNull String... params) {
        if (!Console.logs(type.detail)) return false;
        Console.sep(type);
        System.out.printf(XCode.RESET + style.toString() + format, (Object[]) params);
        Console.empty = false;
        return true;
    }

    /**
     * Logs a list to console
     * @param type Type of message to log
     * @param style Style to override the default style of the specified type
     * @param header List header
     * @param cols Columns of the list
     * @param colSize Size of the columns of the list
     * @return {@code true} if the message was logged and not held back due to verbosity
     */
    public static boolean logL(@NotNull Type type, @NotNull Style style, @NotNull String header,
                               int cols, int colSize, @NotNull Object... objects) {
        if (!Console.logs(type.detail)) return false;
        int dashes = (cols * colSize + cols - 1) - header.length() - 2;
        StringBuilder b = new StringBuilder(XCode.BOLD + "-".repeat(dashes / 2) + " " + header + " "
                + "-".repeat(dashes / 2 + dashes % 2) + "\n" + XCode.WEIGHT_OFF);
        Arrays.sort(objects);
        int i = 1;
        for (Object o : objects) {
            b.append(o).append(" ".repeat(Math.max(0, colSize - o.toString().length())));
            if (i >= cols) {
                b.append("\n");
                i = 1;
            } else {
                b.append(" ");
                i++;
            }
        }
        String s = b.toString();
        if (!s.endsWith("\n")) s += "\n";
        Console.log(type, style, s);
        return true;
    }

    /**
     * Logs a style to console
     * @param type Type of style to log
     * @param style Style to override the default style of the specified type
     * @return {@code true} if the style was logged and not held back due to verbosity
     */
    public static boolean log(@NotNull Type type, @NotNull Style style) {
        return Console.log(type, style, "");
    }

    /**
     * Logs a message to console
     * @param type Type of message to log using the default style of the type
     * @return {@code true} if the style was logged and not held back due to verbosity
     */
    public static boolean log(@NotNull Type type, @NotNull String msg) {
        return Console.log(type, type.style, msg);
    }

    /**
     * @param detail The detail to test for logging
     * @return {@code true} if the detail level gets logged and not held back due to verbosity
     */
    public static boolean logs(@NotNull Detail detail) {
        return Console.detail.val >= detail.val;
    }

    public static boolean logs(@NotNull Type type) {
        return Console.logs(type.detail);
    }

    /**
     * Test for separation between different types of logged output
     * @param type The type to test for separation, {@code type = null} will always separate
     */
    public static void sep(@Nullable Type type) {
        if (Console.type != type || null == type) {
            if (!Console.empty) System.out.println();
            Console.type = type;
            Console.empty = true;
        }
    }

    /**
     * Force separation between logged outputs
     */
    public static void sep() {
        Console.sep(null);
    }

    /**
     * Get new input arguments from console
     * @return user input argument
     */
    public static @NotNull String @NotNull [] in() {
        Console.sep();
        Console.log(Type.PROMPT, "> ");
        Console.log(Type.PROMPT, Style.INPUT);
        return System.console().readLine().split(" ");
    }

    /**
     * Get an input from console
     * @param prompt Prompt to output
     * @return user's response to the prompt
     */
    public static @NotNull String in(@NotNull String prompt) {
        Console.sep();
        Console.log(Type.PROMPT, prompt + " ");
        Console.log(Type.PROMPT, Style.INPUT);
        return System.console().readLine();
    }

    public static boolean confirm(@NotNull String string) {
        return Console.in(string + " (Y/n)?").equalsIgnoreCase("y");
    }
}
