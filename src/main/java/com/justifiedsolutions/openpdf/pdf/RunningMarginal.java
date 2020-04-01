/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

import com.justifiedsolutions.openpdf.pdf.content.Paragraph;

/**
 * Represents either a header or footer for a {@link Document}.
 *
 * @see Header
 * @see Footer
 */
public interface RunningMarginal {

    /**
     * Verifies that this RunningMarginal is valid for the specified page.
     *
     * @param pageNumber the page number to validate
     * @return true if this running marginal should be applied to the specified page
     */
    boolean isValidForPageNumber(int pageNumber);

    /**
     * Gets the {@link Paragraph} that should be applied to the {@link Document}.
     *
     * @param pageNumber the page number the {@link Paragraph} will be applied to
     * @return the Paragraph to apply
     */
    Paragraph getParagraph(int pageNumber);
}
