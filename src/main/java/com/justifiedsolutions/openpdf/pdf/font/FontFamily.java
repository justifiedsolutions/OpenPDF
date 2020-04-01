/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.font;

/**
 * Represents the various font families natively supported in a PDF document.
 */
public enum FontFamily {
    /**
     * Courier fixed width font.
     */
    COURIER,

    /**
     * Helvetica font.
     */
    HELVETICA,

    /**
     * Times Roman font.
     */
    TIMES_ROMAN,

    /**
     * Symbols
     *
     * @see <a href="https://en.wikipedia.org/wiki/Symbol_(typeface)">Wikipedia</a>
     */
    SYMBOL,

    /**
     * Zapfdingbats
     *
     * @see <a href="https://en.wikipedia.org/wiki/Zapf_Dingbats">Wikipedia</a>
     */
    ZAPFDINGBATS
}
