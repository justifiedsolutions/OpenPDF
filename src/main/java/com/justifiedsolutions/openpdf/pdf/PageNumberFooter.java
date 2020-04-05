/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

import com.justifiedsolutions.openpdf.pdf.content.Chunk;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import com.justifiedsolutions.openpdf.pdf.font.Font;

/**
 * A convenience class that places "Page N" at the bottom right of a page (where N is the page
 * number).
 */
public class PageNumberFooter implements Footer {

    private final boolean validForFirstPage;
    private final Font font;

    /**
     * Creates a new PageNumberFooter.
     *
     * @param validForFirstPage true if the page number should be on the first page
     * @param font              the font to use for the text
     */
    public PageNumberFooter(boolean validForFirstPage, Font font) {
        this.validForFirstPage = validForFirstPage;
        this.font = font;
    }

    /**
     * Specifies if the page number should be on the first page.
     *
     * @return true if the page number will be on the first page
     */
    public boolean isValidForFirstPage() {
        return validForFirstPage;
    }

    /**
     * Gets the {@link Font} for the {@link Footer} text
     *
     * @return the font
     */
    public Font getFont() {
        return font;
    }

    @Override
    public boolean isValidForPageNumber(int pageNumber) {
        boolean result = true;
        if ((pageNumber == 1) && !isValidForFirstPage()) {
            result = false;
        }
        return result;
    }

    @Override
    public Paragraph getParagraph(int pageNumber) {
        Paragraph result = new Paragraph(new Chunk("Page "));
        result.add(new Chunk(String.valueOf(pageNumber)));
        result.setAlignment(HorizontalAlignment.RIGHT);
        result.setFont(getFont());
        return result;
    }
}
