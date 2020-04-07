/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.text;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.justifiedsolutions.openpdf.pdf.font.PDFFont;
import com.justifiedsolutions.openpdf.pdf.font.PDFFont.FontName;
import com.justifiedsolutions.openpdf.text.pdf.BaseFont;
import java.awt.Color;
import org.junit.jupiter.api.Test;

public class FontFactoryTest {

    @Test
    public void getFontCourier14Blue() {
        PDFFont input = new PDFFont(FontName.COURIER, 14, Color.BLUE);
        Font actual = FontFactory.getFont(input);
        BaseFont actualBaseFont = actual.getBaseFont();
        assertEquals(BaseFont.COURIER, actualBaseFont.getPostscriptFontName());
        assertEquals(14, actual.getSize());
        assertEquals(Color.BLUE, actual.getColor());
    }

    @Test
    public void getFontDefault() {
        PDFFont input = new PDFFont();
        Font actual = FontFactory.getFont(input);
        FontAssertions.assertDefaultFont(actual);
    }

    @Test
    public void getFontHelveticaBold() {
        PDFFont input = new PDFFont(FontName.HELVETICA_BOLD);
        Font actual = FontFactory.getFont(input);
        BaseFont actualBaseFont = actual.getBaseFont();
        assertEquals(BaseFont.HELVETICA_BOLD, actualBaseFont.getPostscriptFontName());
        assertEquals(12, actual.getSize());
        assertEquals(Color.BLACK, actual.getColor());
    }

    @Test
    public void getFontHelveticaBoldOblique() {
        PDFFont input = new PDFFont(FontName.HELVETICA_BOLD_OBLIQUE);
        Font actual = FontFactory.getFont(input);
        BaseFont actualBaseFont = actual.getBaseFont();
        assertEquals(BaseFont.HELVETICA_BOLDOBLIQUE, actualBaseFont.getPostscriptFontName());
        assertEquals(12, actual.getSize());
        assertEquals(Color.BLACK, actual.getColor());
    }
}