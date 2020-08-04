package com.codegames.simplelist.util

import android.graphics.Rect

fun Boolean.toInt(): Int {
    return if (this) 1 else 0
}

fun Rect.set(space: Int) = set(space, space, space, space)