/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.justifiedsolutions.openpdf.pdf.HorizontalAlignment;
import com.justifiedsolutions.openpdf.pdf.content.Chunk;
import com.justifiedsolutions.openpdf.pdf.content.Phrase;
import com.justifiedsolutions.openpdf.pdf.font.PDFFont;
import org.junit.jupiter.api.Test;

public class ParagraphTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void getInstanceNull() {
        assertThrows(NullPointerException.class, () -> Paragraph.getInstance(
                (com.justifiedsolutions.openpdf.pdf.content.Paragraph) null));
    }

    @Test
    public void getInstanceDefault() {
        Paragraph actual = Paragraph
                .getInstance(new com.justifiedsolutions.openpdf.pdf.content.Paragraph());
        assertEquals(16, actual.getLeading());
        FontAssertions.assertUndefinedFont(actual.getFont());
        assertTrue(actual.getChunks().isEmpty());
    }

    @Test
    public void getInstanceDefaultChangeLeading() {
        com.justifiedsolutions.openpdf.pdf.content.Paragraph input = new com.justifiedsolutions.openpdf.pdf.content.Paragraph();
        input.setLeading(8);
        Paragraph actual = Paragraph.getInstance(input);
        assertEquals(8, actual.getLeading());
        FontAssertions.assertUndefinedFont(actual.getFont());
        assertTrue(actual.getChunks().isEmpty());
    }

    @Test
    public void getInstanceString() {
        Chunk chunk = new Chunk("string");
        Paragraph actual = Paragraph
                .getInstance(new com.justifiedsolutions.openpdf.pdf.content.Paragraph(chunk));
        assertEquals(16, actual.getLeading());
        FontAssertions.assertUndefinedFont(actual.getFont());
        assertEquals(1, actual.getChunks().size());
        assertEquals("string", actual.getChunks().get(0).getContent());
    }

    @Test
    public void getInstanceStringFont() {
        Chunk chunk = new Chunk("string");
        com.justifiedsolutions.openpdf.pdf.content.Paragraph input =
                new com.justifiedsolutions.openpdf.pdf.content.Paragraph(chunk);
        input.setFont(new PDFFont());
        Paragraph actual = Paragraph.getInstance(input);
        assertEquals(16, actual.getLeading());
        FontAssertions.assertDefaultFont(actual.getFont());
        assertEquals(1, actual.getChunks().size());
        assertEquals("string", actual.getChunks().get(0).getContent());
    }

    @Test
    public void getInstanceLoaded() {
        Chunk chunk = new Chunk("string");
        Chunk chunk2 = new Chunk("string2");
        float value = 10f;
        com.justifiedsolutions.openpdf.pdf.content.Paragraph input =
                new com.justifiedsolutions.openpdf.pdf.content.Paragraph(chunk);
        input.setFont(new PDFFont());
        input.addContent(chunk2);
        input.setLeftIndent(value);
        input.setRightIndent(value);
        input.setFirstLineIndent(value);
        input.setSpacingBefore(value);
        input.setSpacingAfter(value);
        input.setKeepTogether(true);
        input.setAlignment(HorizontalAlignment.JUSTIFIED);

        Paragraph actual = Paragraph.getInstance(input);

        assertEquals(16, actual.getLeading());
        FontAssertions.assertDefaultFont(actual.getFont());
        assertEquals(value,actual.getIndentationLeft());
        assertEquals(value,actual.getIndentationRight());
        assertEquals(value,actual.getFirstLineIndent());
        assertEquals(value,actual.getSpacingBefore());
        assertEquals(value,actual.getSpacingAfter());
        assertTrue(actual.getKeepTogether());
        assertEquals(Element.ALIGN_JUSTIFIED, actual.getAlignment());
        assertEquals(1, actual.getChunks().size());
        assertEquals("stringstring2", actual.getChunks().get(0).getContent());
    }

    @Test
    public void getInstanceChunkPhrase() {
        Chunk chunk = new Chunk("string");
        Phrase phrase = new Phrase("string2");
        com.justifiedsolutions.openpdf.pdf.content.Paragraph input =
                new com.justifiedsolutions.openpdf.pdf.content.Paragraph(chunk);
        input.addContent(phrase);

        Paragraph actual = Paragraph.getInstance(input);

        assertEquals(1, actual.getChunks().size());
        assertEquals("stringstring2", actual.getChunks().get(0).getContent());
    }

}