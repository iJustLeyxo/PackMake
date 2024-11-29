package com.github.ijustleyxo.packmake.util.console;

import org.jetbrains.annotations.NotNull;

/**
 * Console output type, used to combine commonly used combinations of a style and detail level
 */
public enum Type {
    PROMPT(Detail.OVERRIDE, Style.PROMPT),
    REQUESTED(Detail.OVERRIDE, Style.INFO),
    DEBUG(Detail.HIGH, Style.DEBUG),
    INFO(Detail.STD, Style.INFO),
    WARN(Detail.LOW, Style.WARN),
    ERR(Detail.MIN, Style.ERR);

    public final @NotNull Detail detail;
    public final @NotNull Style style;

    Type(@NotNull Detail detail, @NotNull Style style) {
        this.detail = detail;
        this.style = style;
    }
}
