/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.content;

import com.justifiedsolutions.openpdf.pdf.font.Font;

/**
 * TextContent is an interface for Content that has a {@link Font} and can be added to a {@link
 * com.justifiedsolutions.openpdf.pdf.Document}.
 */
public interface TextContent extends Content {

    /**
     * Get the {@link Font} for the Content.
     *
     * @return the font
     */
    Font getFont();

    /**
     * Set the {@link Font} for the Content.
     *
     * @param font the font
     */
    void setFont(Font font);

}
