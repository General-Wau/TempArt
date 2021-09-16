package com.necromyd.tempart;

import android.graphics.Path;

public class Brush {

    public int color;
    public int strokeWidth;
    public Path path;

    public Brush(int color, int strokeWidth, Path path) {

        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;

    }
}
