/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

import java.util.Objects;

/**
 * An immutable rectangle.
 */
public final class Rectangle {

    private final float width;
    private final float height;

    /**
     * Creates a rectangle with the specified width and height.
     *
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     */
    public Rectangle(float width, float height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the width of the rectangle
     *
     * @return the width
     */
    public float getWidth() {
        return width;
    }

    /**
     * Gets the height of the rectangle
     *
     * @return the height
     */
    public float getHeight() {
        return height;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Rectangle rectangle = (Rectangle) o;
        return Float.compare(rectangle.width, width) == 0 &&
                Float.compare(rectangle.height, height) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(width, height);
    }
}
