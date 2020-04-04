/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text.pdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.justifiedsolutions.openpdf.pdf.HorizontalAlignment;
import com.justifiedsolutions.openpdf.pdf.VerticalAlignment;
import com.justifiedsolutions.openpdf.pdf.content.Cell;
import com.justifiedsolutions.openpdf.pdf.content.Cell.Border;
import com.justifiedsolutions.openpdf.pdf.content.Phrase;
import com.justifiedsolutions.openpdf.pdf.content.Table;
import com.justifiedsolutions.openpdf.text.Element;
import com.justifiedsolutions.openpdf.text.Rectangle;
import org.junit.jupiter.api.Test;

public class PdfPCellTest {

    @Test
    public void getInstanceNull() {
        assertThrows(NullPointerException.class, () -> PdfPCell.getInstance(null));
    }

    @Test
    public void getInstanceDefault() {
        Table table = new Table(1);
        Cell input = table.createCell();
        PdfPCell actual = PdfPCell.getInstance(input);

        assertEquals(Rectangle.BOX, actual.getBorder());
        assertNull(actual.getPhrase());
        assertEquals(1, actual.getRowspan());
        assertEquals(1, actual.getColspan());
        assertEquals(Element.ALIGN_LEFT, actual.getHorizontalAlignment());
        assertEquals(Element.ALIGN_TOP, actual.getVerticalAlignment());
        assertEquals(0, actual.getMinimumHeight());
        assertEquals(2, actual.getPaddingTop());
        assertEquals(2, actual.getPaddingBottom());
        assertEquals(2, actual.getPaddingLeft());
        assertEquals(2, actual.getPaddingRight());
        assertEquals(1, actual.getGrayFill());
    }

    @Test
    public void getInstanceLoadedNoContent() {
        Table table = new Table(2);
        Cell input = table.createCell();
        input.setBorders(Border.BOTTOM);
        input.setRowSpan(2);
        input.setColumnSpan(2);
        input.setHorizontalAlignment(HorizontalAlignment.CENTER);
        input.setVerticalAlignment(VerticalAlignment.MIDDLE);
        input.setMinimumHeight(10);
        input.setPadding(4);
        input.setGreyFill(.75f);

        PdfPCell actual = PdfPCell.getInstance(input);

        assertEquals(Rectangle.BOTTOM, actual.getBorder());
        assertNull(actual.getPhrase());
        assertEquals(2, actual.getRowspan());
        assertEquals(2, actual.getColspan());
        assertEquals(Element.ALIGN_CENTER, actual.getHorizontalAlignment());
        assertEquals(Element.ALIGN_MIDDLE, actual.getVerticalAlignment());
        assertEquals(10, actual.getMinimumHeight());
        assertEquals(4, actual.getPaddingTop());
        assertEquals(4, actual.getPaddingBottom());
        assertEquals(4, actual.getPaddingLeft());
        assertEquals(4, actual.getPaddingRight());
        assertEquals(.75f, actual.getGrayFill());
    }

    @Test
    public void getInstanceComplicatedBorder() {
        Table table = new Table(2);
        Cell input = table.createCell();
        input.setBorders(Border.TOP, Border.LEFT, Border.RIGHT);

        PdfPCell actual = PdfPCell.getInstance(input);

        assertEquals(Rectangle.TOP | Rectangle.LEFT | Rectangle.RIGHT, actual.getBorder());
    }

    @Test
    public void getInstanceContent() {
        String expectedText = "text";
        Phrase phrase = new Phrase(expectedText);
        Table table = new Table(2);
        Cell input = table.createCell(phrase);

        PdfPCell actual = PdfPCell.getInstance(input);
        assertNotNull(actual.getPhrase());
        assertNotNull(actual.getPhrase().getChunks());
        assertEquals(1, actual.getPhrase().getChunks().size());
        assertNotNull(actual.getPhrase().getChunks().get(0));
        assertEquals(expectedText, actual.getPhrase().getChunks().get(0).getContent());
    }

}