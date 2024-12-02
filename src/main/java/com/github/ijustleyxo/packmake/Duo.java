package com.github.ijustleyxo.packmake;

import org.jetbrains.annotations.NotNull;

public final class Duo<A, B> {
    private @NotNull A a;
    private @NotNull B b;

    public Duo(@NotNull A a, @NotNull B b) {
        this.a = a;
        this.b = b;
    }

    public @NotNull A a() {
        return this.a;
    }

    public @NotNull B b() {
        return this.b;
    }

    public void a(@NotNull A a) {
        this.a = a;
    }

    public void b(@NotNull B b) {
        this.b = b;
    }
}
