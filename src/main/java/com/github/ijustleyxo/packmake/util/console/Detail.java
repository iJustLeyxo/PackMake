package com.github.ijustleyxo.packmake.util.console;

/**
 * Console output detail level
 */
public enum Detail {
    /**
     * Override detail management and force console output
     */
    OVERRIDE(Integer.MIN_VALUE),
    MIN(-2),
    LOW(-1),
    STD(0),
    HIGH(1);

    public final int val;

    Detail(int val) {
        this.val = val;
    }
}
