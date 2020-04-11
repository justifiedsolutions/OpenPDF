/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

/**
 * Represents common page sizes.
 */
public enum PageSize {
    /**
     * US Letter
     */
    LETTER(612, 792),
    /**
     * US Legal
     */
    LEGAL(612, 1008),
    /**
     * A0
     */
    A0(2384, 3370),
    /**
     * A1
     */
    A1(1684, 2384),
    /**
     * A2
     */
    A2(1191, 1684),
    /**
     * A3
     */
    A3(842, 1191),
    /**
     * A4
     */
    A4(595, 842),
    /**
     * A5
     */
    A5(420, 595),
    /**
     * A6
     */
    A6(297, 420),
    /**
     * A7
     */
    A7(210, 297),
    /**
     * A8
     */
    A8(148, 210);

    private final float width;
    private final float height;

    PageSize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public float width() {
        return width;
    }

    public float height() {
        return height;
    }
}
