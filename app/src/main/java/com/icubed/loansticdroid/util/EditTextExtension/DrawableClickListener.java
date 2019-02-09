package com.icubed.loansticdroid.util.EditTextExtension;

public interface DrawableClickListener {

    public static enum DrawablePosition { TOP, BOTTOM, LEFT, RIGHT };
    public void onClick(DrawablePosition target);
}