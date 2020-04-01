/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.content;

import com.justifiedsolutions.openpdf.pdf.font.Font;

/**
 * A factory class for creating the various content types.
 */
public class ContentFactory {

    /**
     * A {@link Chunk} that represents a line break.
     */
    public static final Chunk NEWLINE = createChunk("\n");

    /**
     * Prevent {@link ContentFactory} from being instantiated.
     */
    private ContentFactory() {
    }

    /**
     * Creates a new, empty {@link Chunk} with no specified {@link Font}.
     *
     * @return a new Chunk
     */
    public static Chunk createChunk() {
        //TODO
        return null;
    }

    /**
     * Creates a new {@link Chunk} with the specified text and no {@link Font}.
     *
     * @param text the text in the Chunk
     * @return a new Chunk
     */
    public static Chunk createChunk(String text) {
        //TODO
        return null;
    }

    /**
     * Creates a new {@link Chunk} with the specified text and {@link Font}.
     *
     * @param text the text in the Chunk
     * @param font the Font of the Chunk
     * @return a new Chunk
     */
    public static Chunk createChunk(String text, Font font) {
        //TODO
        return null;
    }

    /**
     * Creates a new {@link Paragraph} with no content or specified {@link Font}.
     *
     * @return a new Paragraph
     */
    public static Paragraph createParagraph() {
        //TODO
        return null;
    }

    /**
     * Creates a new {@link Paragraph} with the specified text and no specified {@link Font}.
     *
     * @param text the text of the Paragraph
     * @return a new Paragraph
     */
    public static Paragraph createParagraph(String text) {
        //TODO
        return null;
    }

    /**
     * Creates a new {@link Paragraph} with the specified text and {@link Font}.
     *
     * @param text the text of the Paragraph
     * @param font the font of the Paragraph
     * @return a new Paragraph
     */
    public static Paragraph createParagraph(String text, Font font) {
        //TODO
        return null;
    }

    /**
     * Creates a new {@link Phrase} with no specified text or {@link Font}.
     *
     * @return a new Phrase
     */
    public static Phrase createPhrase() {
        //TODO
        return null;
    }

    /**
     * Creates a new {@link Phrase} with the specified text and no specified {@link Font}.
     *
     * @param text the text of the Phrase
     * @return a new Phrase
     */
    public static Phrase createPhrase(String text) {
        //TODO
        return null;
    }

    /**
     * Creates a new {@link Phrase} with the specified text and {@link Font}.
     *
     * @param text the text of the Phrase
     * @param font the font of the Phrase
     * @return a new Phrase
     */
    public static Phrase createPhrase(String text, Font font) {
        //TODO
        return null;
    }
}
