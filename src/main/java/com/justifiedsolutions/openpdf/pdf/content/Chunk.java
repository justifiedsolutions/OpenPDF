/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.content;


import com.justifiedsolutions.openpdf.pdf.font.Font;

/**
 * Smallest part of text that can be added to a {@link com.justifiedsolutions.openpdf.pdf.Document}.
 */
public interface Chunk extends Content {

    /**
     * Append the specified text to existing text in this Chunk.
     *
     * @param text the text to append
     */
    void append(String text);

    /**
     * The text in the Chunk
     *
     * @return the text
     */
    String getText();

    /**
     * Sets the text of the Chunk.
     *
     * @param text the new text for the Chunk
     */
    void setText(String text);

    /**
     * Gets the font associated with this Chunk.
     *
     * @return the font
     */
    Font getFont();

    /**
     * Sets the font associated with this Chunk
     *
     * @param font the new font
     */
    void setFont(Font font);
}
