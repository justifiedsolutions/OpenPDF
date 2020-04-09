/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.internal;

import com.justifiedsolutions.openpdf.pdf.Footer;
import com.justifiedsolutions.openpdf.pdf.Header;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import com.justifiedsolutions.openpdf.text.Element;
import com.justifiedsolutions.openpdf.text.pdf.ColumnText;
import com.justifiedsolutions.openpdf.text.pdf.PdfContentByte;
import com.justifiedsolutions.openpdf.text.pdf.PdfPageEventHelper;
import com.justifiedsolutions.openpdf.text.pdf.PdfWriter;

/**
 * Assists in adding {@link Header} and {@link Footer} to a PDF document.
 */
class HeaderFooterHelper extends PdfPageEventHelper {

    private static final int DISTANCE_FROM_MARGIN = 27;

    private final Header header;
    private final Footer footer;

    public HeaderFooterHelper(Header header, Footer footer) {
        this.header = header;
        this.footer = footer;
    }

    @Override
    public void onEndPage(PdfWriter writer,
            com.justifiedsolutions.openpdf.text.Document document) {

        int pageNumber = document.getPageNumber();

        if (header != null && header.isValidForPageNumber(pageNumber)) {
            writeHeaderFooter(writer, document, header.getParagraph(pageNumber),
                    document.top() + DISTANCE_FROM_MARGIN);
        }

        if (footer != null && footer.isValidForPageNumber(pageNumber)) {
            writeHeaderFooter(writer, document, footer.getParagraph(pageNumber),
                    document.bottom() - DISTANCE_FROM_MARGIN);
        }
    }

    private void writeHeaderFooter(PdfWriter writer, com.justifiedsolutions.openpdf.text.Document document,
            Paragraph modelParagraph, float y) {
        com.justifiedsolutions.openpdf.text.Paragraph paragraph = com.justifiedsolutions.openpdf.text.Paragraph
                .getInstance(modelParagraph);
        float x;
        int alignment = paragraph.getAlignment();
        if (alignment == Element.ALIGN_LEFT) {
            x = document.left();
        } else if (alignment == Element.ALIGN_RIGHT) {
            x = document.right();
        } else {
            x = document.getPageSize().getRight() / 2.0f;
            alignment = Element.ALIGN_CENTER;
        }
        final PdfContentByte cb = writer.getDirectContent();
        ColumnText.showTextAligned(cb, alignment, paragraph, x, y, 0);
    }
}
