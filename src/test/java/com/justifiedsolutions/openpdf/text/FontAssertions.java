/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.justifiedsolutions.openpdf.text.pdf.BaseFont;
import java.awt.Color;

public class FontAssertions {

    public static void assertUndefinedFont(Font font) {
        assertNotNull(font);
        assertEquals(Font.UNDEFINED, font.getFamily());
        assertEquals(Font.UNDEFINED, font.getStyle());
        assertEquals(Font.UNDEFINED, font.getSize());
        assertNull(font.getColor());
    }

    public static void assertDefaultFont(Font font) {
        BaseFont fontBaseFont = font.getBaseFont();
        assertEquals(BaseFont.HELVETICA, fontBaseFont.getPostscriptFontName());
        assertEquals(12, font.getSize());
        assertEquals(Color.BLACK, font.getColor());
    }
}
