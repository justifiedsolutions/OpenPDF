/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.content;

import com.justifiedsolutions.openpdf.pdf.HorizontalAlignment;
import com.justifiedsolutions.openpdf.pdf.font.Font;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * A Paragraph is a series of {@link Chunk}s and {@link Phrase}s. The Paragraph has an associated
 * {@link com.justifiedsolutions.openpdf.pdf.font.Font} and any Chunks or Phrases added to the
 * Paragraph inherit the Font of the Paragraph unless they specify a Font themselves.
 */
public class Paragraph implements TextContent {

    private float leading = 16f;
    private Font font;
    private float leftIndent = 0f;
    private float rightIndent = 0f;
    private float firstLineIndent = 0f;
    private float spacingBefore = 0f;
    private float spacingAfter = 0f;
    private boolean keepTogether = false;
    private HorizontalAlignment alignment;
    private final List<Content> content = new ArrayList<>();

    /**
     * Creates an empty Paragraph.
     */
    public Paragraph() {
        // do nothing
    }

    /**
     * Creates a new Paragraph adding the specified content.
     *
     * @param content the content
     * @throws NullPointerException     if content is null
     * @throws IllegalArgumentException if content is not a Chunk or Phrase
     */
    public Paragraph(Content content) {
        add(content);
    }

    /**
     * Get the leading for the Paragraph. The default value is 16.0f.
     *
     * @return the leading
     */
    public float getLeading() {
        return leading;
    }

    /**
     * Set the leading for the Paragraph.
     *
     * @param leading the leading
     */
    public void setLeading(float leading) {
        this.leading = leading;
    }

    @Override
    public Font getFont() {
        return font;
    }

    @Override
    public void setFont(Font font) {
        this.font = font;
    }

    /**
     * Gets the left indent of the Paragraph. The default is <code>0</code>.
     *
     * @return left indent
     */
    public float getLeftIndent() {
        return leftIndent;
    }

    /**
     * Sets the left indent of the Paragraph.
     *
     * @param leftIndent left indent
     */
    public void setLeftIndent(float leftIndent) {
        this.leftIndent = leftIndent;
    }

    /**
     * Gets the right indent of the Paragraph. The default is <code>0</code>    .
     *
     * @return right indent
     */
    public float getRightIndent() {
        return rightIndent;
    }

    /**
     * Sets the right indent of the Paragraph.
     *
     * @param rightIndent right indent
     */
    public void setRightIndent(float rightIndent) {
        this.rightIndent = rightIndent;
    }

    /**
     * Specifies the amount of the first line indentation. The default is <code>0</code>.
     *
     * @return the amount of indentation
     */
    public float getFirstLineIndent() {
        return firstLineIndent;
    }

    /**
     * Specifies the amount of the first line indentation.
     *
     * @param firstLineIndent the amount of indentation
     */
    public void setFirstLineIndent(float firstLineIndent) {
        this.firstLineIndent = firstLineIndent;
    }

    /**
     * Gets the amount of vertical spacing before the Paragraph. The default is <code>0</code>.
     *
     * @return the amount of vertical spacing before the Paragraph
     */
    public float getSpacingBefore() {
        return spacingBefore;
    }

    /**
     * Sets the amount of vertical spacing before the Paragraph.
     *
     * @param spacingBefore vertical spacing before
     */
    public void setSpacingBefore(float spacingBefore) {
        this.spacingBefore = spacingBefore;
    }

    /**
     * Gets the amount of vertical spacing after the Paragraph. The default is <code>0</code>.
     *
     * @return the amount of vertical spacing after the Paragraph
     */
    public float getSpacingAfter() {
        return spacingAfter;
    }

    /**
     * Sets the amount of vertical spacing after the Paragraph.
     *
     * @param spacingAfter vertical spacing after
     */
    public void setSpacingAfter(float spacingAfter) {
        this.spacingAfter = spacingAfter;
    }

    /**
     * Specifies if the Paragraph should be broken between pages. The default is
     * <code>false</code>.
     *
     * @return true if the paragraph should be kept together
     */
    public boolean isKeepTogether() {
        return keepTogether;
    }

    /**
     * Specifies if the Paragraph should be broken between pages.
     *
     * @param keepTogether true if the paragraph should be kept together
     */
    public void setKeepTogether(boolean keepTogether) {
        this.keepTogether = keepTogether;
    }

    /**
     * Gets the {@link HorizontalAlignment} of the Paragraph. If not specified, it will default to
     * HorizontalAlignment of the {@link com.justifiedsolutions.openpdf.pdf.Document}.
     *
     * @return the alignment of the paragraph or <code>null</code> if it isn't specified
     */
    public HorizontalAlignment getAlignment() {
        return alignment;
    }

    /**
     * Sets the {@link HorizontalAlignment} of the Paragraph.
     *
     * @param alignment the alignment or <code>null</code> if it isn't specified
     */
    public void setAlignment(HorizontalAlignment alignment) {
        this.alignment = alignment;
    }

    /**
     * Get an {@link Collections#unmodifiableList(List)} of {@link Content}s for the Paragraph.
     *
     * @return the list of Content
     */
    public List<Content> getContent() {
        return Collections.unmodifiableList(content);
    }

    /**
     * Adds the specified content to the Paragraph.
     *
     * @param content the content
     * @throws NullPointerException     if content is null
     * @throws IllegalArgumentException if content is not a Chunk or Phrase
     */
    public void add(Content content) {
        Objects.requireNonNull(content);
        if ((content instanceof Chunk) || (content instanceof Phrase)) {
            this.content.add(content);
        } else {
            throw new IllegalArgumentException("Invalid content type: " + content.getClass());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Paragraph paragraph = (Paragraph) o;
        return Float.compare(paragraph.leading, leading) == 0 &&
                Float.compare(paragraph.leftIndent, leftIndent) == 0 &&
                Float.compare(paragraph.rightIndent, rightIndent) == 0 &&
                firstLineIndent == paragraph.firstLineIndent &&
                Float.compare(paragraph.spacingBefore, spacingBefore) == 0 &&
                Float.compare(paragraph.spacingAfter, spacingAfter) == 0 &&
                keepTogether == paragraph.keepTogether &&
                Objects.equals(font, paragraph.font) &&
                alignment == paragraph.alignment &&
                content.equals(paragraph.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(leading, font, leftIndent, rightIndent, firstLineIndent, spacingBefore,
                spacingAfter, keepTogether, alignment, content);
    }
}
