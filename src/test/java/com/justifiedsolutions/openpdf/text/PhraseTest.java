/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.justifiedsolutions.openpdf.pdf.font.PDFFont;
import org.junit.jupiter.api.Test;

public class PhraseTest {

    @Test
    public void getInstanceNull() {
        assertThrows(NullPointerException.class, () -> Phrase.getInstance(
                (com.justifiedsolutions.openpdf.pdf.content.Phrase) null));
    }

    @Test
    public void getInstanceDefault() {
        Phrase actual = Phrase.getInstance(new com.justifiedsolutions.openpdf.pdf.content.Phrase());
        assertEquals(16, actual.getLeading());
        FontAssertions.assertUndefinedFont(actual.getFont());
        assertTrue(actual.getChunks().isEmpty());
    }

    @Test
    public void getInstanceDefaultChangeLeading() {
        com.justifiedsolutions.openpdf.pdf.content.Phrase input = new com.justifiedsolutions.openpdf.pdf.content.Phrase();
        input.setLeading(8);
        Phrase actual = Phrase.getInstance(input);
        assertEquals(8, actual.getLeading());
        FontAssertions.assertUndefinedFont(actual.getFont());
        assertTrue(actual.getChunks().isEmpty());
    }

    @Test
    public void getInstanceString() {
        Phrase actual = Phrase
                .getInstance(new com.justifiedsolutions.openpdf.pdf.content.Phrase("string"));
        assertEquals(16, actual.getLeading());
        FontAssertions.assertUndefinedFont(actual.getFont());
        assertEquals(1, actual.getChunks().size());
        assertEquals("string", actual.getChunks().get(0).getContent());
    }

    @Test
    public void getInstanceStringFont() {
        Phrase actual = Phrase.getInstance(
                new com.justifiedsolutions.openpdf.pdf.content.Phrase("string", new PDFFont()));
        assertEquals(16, actual.getLeading());
        FontAssertions.assertDefaultFont(actual.getFont());
        assertEquals(1, actual.getChunks().size());
        assertEquals("string", actual.getChunks().get(0).getContent());
    }

}