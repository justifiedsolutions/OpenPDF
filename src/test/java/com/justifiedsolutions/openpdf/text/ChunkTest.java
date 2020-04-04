/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.justifiedsolutions.openpdf.pdf.font.PDFFont;
import org.junit.jupiter.api.Test;

public class ChunkTest {

    @Test
    public void getInstanceNull() {
        assertThrows(NullPointerException.class,
                () -> com.justifiedsolutions.openpdf.text.Chunk.getInstance(null));
    }

    @Test
    public void getInstanceStringFont() {
        com.justifiedsolutions.openpdf.pdf.content.Chunk input = new com.justifiedsolutions.openpdf.pdf.content.Chunk(
                "string", new PDFFont());
        com.justifiedsolutions.openpdf.text.Chunk actual = com.justifiedsolutions.openpdf.text.Chunk
                .getInstance(input);
        assertEquals("string", actual.getContent());
        FontAssertions.assertDefaultFont(actual.getFont());
    }

    @Test
    public void getInstanceEmpty() {
        com.justifiedsolutions.openpdf.text.Chunk actual = com.justifiedsolutions.openpdf.text.Chunk
                .getInstance(new com.justifiedsolutions.openpdf.pdf.content.Chunk());
        assertEquals("", actual.getContent());
        FontAssertions.assertUndefinedFont(actual.getFont());
    }
}