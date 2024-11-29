package com.github.ijustleyxo.packmake.util.console;

import org.jetbrains.annotations.NotNull;

/**
 * Console output styles, used to manage color, weight, etc. of console outputs
 */
public enum Style {
    PROMPT(XCode.BOLD, XCode.BLUE),
    INPUT(XCode.BOLD, XCode.WHITE),
    DEBUG(XCode.GRAY),
    SELECT(XCode.BLUE),
    INSTALL(XCode.GREEN),
    UPDATE(XCode.GREEN),
    UNINSTALL(XCode.RED),
    LINK(XCode.MAGENTA),
    SUPERFLUOUS(XCode.YELLOW),
    MISSING(XCode.RED),
    UNKNOWN(XCode.GRAY),
    PLUGIN(XCode.BLUE),
    CATEGORY(XCode.GREEN),
    SERVER(XCode.MAGENTA),
    SOFTWARE(XCode.YELLOW),
    INFO(XCode.WHITE),
    DONE(XCode.GREEN),
    HELP(XCode.GREEN),
    WARN(XCode.BOLD, XCode.YELLOW),
    ERR(XCode.BOLD, XCode.RED);

    public final @NotNull String xCodes;

    Style(@NotNull XCode @NotNull ... codes) {
        StringBuilder s = new StringBuilder();
        for (XCode c : codes) {
            s.append(c);
        }
        this.xCodes = s.toString();
    }

    @Override
    public @NotNull String toString() { return this.xCodes; }
}
