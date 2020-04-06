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

public class PageNumberFooterTest {

    @Test
    public void isValidForPageNumberTrue() {
        PageNumberFooter footer = new PageNumberFooter(true, HorizontalAlignment.CENTER, new PDFFont());
        assertTrue(footer.isValidForPageNumber(1));
        assertTrue(footer.isValidForPageNumber(2));
    }
    @Test
    public void isValidForPageNumberFalse() {
        PageNumberFooter footer = new PageNumberFooter(false, HorizontalAlignment.CENTER, new PDFFont());
        assertFalse(footer.isValidForPageNumber(1));
        assertTrue(footer.isValidForPageNumber(2));
    }

    @Test
    public void getParagraph() {
        Font expectedFont = new PDFFont();
        HorizontalAlignment expectedAlignment = HorizontalAlignment.CENTER;
        PageNumberFooter footer = new PageNumberFooter(true, expectedAlignment, expectedFont);
        Paragraph actual = footer.getParagraph(1);
        assertEquals(expectedAlignment, actual.getAlignment());
        assertEquals(expectedFont, actual.getFont());
        assertEquals(1, actual.getContent().size());
        Chunk chunk = (Chunk) actual.getContent().get(0);
        assertEquals("Page 1", chunk.getText());
    }
}