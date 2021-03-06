/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

import com.justifiedsolutions.openpdf.pdf.content.Content;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;

/**
 * A Chapter is a specialization of a {@link Section} that can only be created by a {@link
 * com.justifiedsolutions.openpdf.pdf.Document}. If a Document has Chapters it cannot have any other type of {@link
 * Content} directly added to it. A new Chapter always starts a new page in the Document.
 *
 * @see com.justifiedsolutions.openpdf.pdf.Document
 * @see Section
 */
public class Chapter extends Section {

    Chapter(int sectionNumber, Paragraph title) {
        super(sectionNumber, title);
    }

    /**
     * A Chapter always starts a new page.
     *
     * @return <code>true</code>
     */
    @Override
    public boolean isStartsNewPage() {
        return true;
    }

    /**
     * If anything other than <code>true</code> is passed, an exception is thrown.
     *
     * @param startsNewPage true if the section should start a new page
     * @throws IllegalArgumentException if <code>false</code> is passed as an argument
     */
    @Override
    public void setStartsNewPage(boolean startsNewPage) {
        if (!startsNewPage) {
            throw new IllegalArgumentException("A Chapter always starts a new page.");
        }
    }
}
