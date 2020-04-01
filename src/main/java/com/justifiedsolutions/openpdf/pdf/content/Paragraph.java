/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.content;

import com.justifiedsolutions.openpdf.pdf.HorizontalAlignment;

/**
 * A Paragraph is a series of {@link Chunk}s and {@link Phrase}s. A Paragraph extends {@link Phrase}
 * and adds indentation and alignment.
 */
public interface Paragraph extends Phrase {

    /**
     * Gets the left indent of the Paragraph. The default is <code>0</code>.
     *
     * @return left indent
     */
    float getLeftIndent();

    /**
     * Sets the left indent of the Paragraph.
     *
     * @param leftIndent left indent
     */
    void setLeftIndent(float leftIndent);

    /**
     * Gets the right indent of the Paragraph. The default is <code>0</code>    .
     *
     * @return right indent
     */
    float getRightIndent();

    /**
     * Sets the right indent of the Paragraph.
     *
     * @param rightIndent right indent
     */
    void setRightIndent(float rightIndent);

    /**
     * Specifies if the first line is indented. The default is <code>false</code>.
     *
     * @return true if the first line is indented
     */
    boolean isFirstLineIndent();

    /**
     * Specifies whether the first line is indented.
     *
     * @param firstLineIndent true if the first line should be indented
     */
    void setFirstLineIndent(boolean firstLineIndent);

    /**
     * Gets the amount of vertical spacing before the Paragraph. The default is <code>0</code>.
     *
     * @return the amount of vertical spacing before the Paragraph
     */
    float getSpacingBefore();

    /**
     * Sets the amount of vertical spacing before the Paragraph.
     *
     * @param spacingBefore vertical spacing before
     */
    void setSpacingBefore(float spacingBefore);

    /**
     * Gets the amount of vertical spacing after the Paragraph. The default is <code>0</code>.
     *
     * @return the amount of vertical spacing after the Paragraph
     */
    float getSpacingAfter();

    /**
     * Sets the amount of vertical spacing after the Paragraph.
     *
     * @param spacingAfter vertical spacing after
     */
    void setSpacingAfter(float spacingAfter);

    /**
     * Specifies if the Paragraph should be broken between pages. The default is
     * <code>false</code>.
     *
     * @return true if the paragraph should be kept together
     */
    boolean isKeepTogether();

    /**
     * Specifies if the Paragraph should be broken between pages.
     *
     * @param keepTogether true if the paragraph should be kept together
     */
    void setKeepTogether(boolean keepTogether);

    /**
     * Gets the {@link HorizontalAlignment} of the Paragraph. If not specified, it will default to
     * HorizontalAlignment of the {@link com.justifiedsolutions.openpdf.pdf.Document}.
     *
     * @return the alignment of the paragraph or <code>null</code> if it isn't specified
     */
    HorizontalAlignment getAlignment();

    /**
     * Sets the {@link HorizontalAlignment} of the Paragraph.
     *
     * @param alignment the alignment or <code>null</code> if it isn't specified
     */
    void setAlignment(HorizontalAlignment alignment);
}
