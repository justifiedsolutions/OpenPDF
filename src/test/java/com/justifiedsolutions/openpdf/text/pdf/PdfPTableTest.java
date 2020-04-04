/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text.pdf;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.justifiedsolutions.openpdf.pdf.content.Phrase;
import com.justifiedsolutions.openpdf.pdf.content.Table;
import org.junit.jupiter.api.Test;

public class PdfPTableTest {

    @Test
    public void getInstanceNull() {
        assertThrows(NullPointerException.class, () -> PdfPTable.getInstance(null));
    }

    @Test
    public void getInstanceWidthConstructor() {
        float[] expected = {.25f, .25f, .25f, .1f, .15f};
        Table input = new Table(expected);
        PdfPTable actual = PdfPTable.getInstance(input);
        assertEquals(expected.length, actual.getNumberOfColumns());
        assertArrayEquals(expected, actual.relativeWidths);
    }

    @Test
    public void getInstanceNumColConstructor() {
        float[] expected = {1f, 1f, 1f, 1f};
        Table input = new Table(expected.length);
        PdfPTable actual = PdfPTable.getInstance(input);
        assertEquals(expected.length, actual.getNumberOfColumns());
        assertArrayEquals(expected, actual.relativeWidths);
    }

    @Test
    public void getInstanceAttributes() {
        Table input = new Table(2);
        input.setKeepTogether(true);
        input.setWidthPercentage(100);
        input.setSpacingBefore(10);
        input.setSpacingAfter(11);

        PdfPTable actual = PdfPTable.getInstance(input);

        assertTrue(actual.getKeepTogether());
        assertEquals(100, actual.getWidthPercentage());
        assertEquals(10, actual.spacingBefore());
        assertEquals(11, actual.spacingAfter());
    }

    @Test
    public void getInstanceCells() {
        Table input = new Table(2);
        Phrase p1 = new Phrase("p1");
        input.createCell(p1);
        Phrase p2 = new Phrase("p2");
        input.createCell(p2);

        PdfPTable actual = PdfPTable.getInstance(input);

        assertNotNull(actual.getRows());
        assertEquals(1, actual.getRows().size());
        PdfPRow row = actual.getRow(0);
        assertNotNull(row.getCells());
        assertEquals(2, row.getCells().length);
        PdfPCell c0 = row.getCells()[0];
        assertEquals("p1", c0.getPhrase().getContent());
        PdfPCell c1 = row.getCells()[1];
        assertEquals("p2", c1.getPhrase().getContent());
    }


}