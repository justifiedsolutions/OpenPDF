/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

import static org.junit.jupiter.api.Assertions.*;

import com.justifiedsolutions.openpdf.pdf.content.Chunk;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import com.justifiedsolutions.openpdf.pdf.font.Font;
import com.justifiedsolutions.openpdf.pdf.font.PDFFont;
import org.junit.jupiter.api.Test;

class PageNumberFooterTest {

    @Test
    void isValidForPageNumberTrue() {
        PageNumberFooter footer = new PageNumberFooter(true, new PDFFont());
        assertTrue(footer.isValidForPageNumber(1));
        assertTrue(footer.isValidForPageNumber(2));
    }
    @Test
    void isValidForPageNumberFalse() {
        PageNumberFooter footer = new PageNumberFooter(false, new PDFFont());
        assertFalse(footer.isValidForPageNumber(1));
        assertTrue(footer.isValidForPageNumber(2));
    }

    @Test
    void getParagraph() {
        Font expectedFont = new PDFFont();
        PageNumberFooter footer = new PageNumberFooter(true, expectedFont);
        Paragraph actual = footer.getParagraph(1);
        assertEquals(HorizontalAlignment.RIGHT, actual.getAlignment());
        assertEquals(expectedFont, actual.getFont());
        assertEquals(2, actual.getContent().size());
        Chunk chunk1 = (Chunk) actual.getContent().get(0);
        Chunk chunk2 = (Chunk) actual.getContent().get(1);
        assertEquals("Page ", chunk1.getText());
        assertEquals("1",chunk2.getText());
    }
}