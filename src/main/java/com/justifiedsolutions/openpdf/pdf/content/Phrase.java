package com.justifiedsolutions.openpdf.pdf.content;

import com.justifiedsolutions.openpdf.pdf.font.Font;
import java.util.List;

/**
 * A Phrase is a series of {@link Chunk}s. The Phrase has an associated {@link
 * com.justifiedsolutions.openpdf.pdf.font.Font} and any Chunks added to the Phrase inherit the Font
 * of the Phrase unless they specify a Font themselves. A Phrase also specifies a leading value.
 *
 * @see <a href="https://techterms.com/definition/leading">Leading</a>
 */
public interface Phrase extends Content {

    /**
     * Get the leading for the Phrase. The default value is 16.0f.
     *
     * @return the leading
     */
    float getLeading();

    /**
     * Set the leading for the Phrase.
     *
     * @param leading the leading
     */
    void setLeading(float leading);

    /**
     * Get the {@link Font} for the Phrase.
     *
     * @return the font
     */
    Font getFont();

    /**
     * Set the {@link Font} for the Phrase.
     *
     * @param font the font
     */
    void setFont(Font font);

    /**
     * Get the list of {@link Chunk}s for the Phrase.
     *
     * @return the list of Chunks
     */
    List<Chunk> getChunks();

    /**
     * Adds a {@link Chunk} to the Phrase.
     *
     * @param chunk the chunk to add
     */
    void addChunk(Chunk chunk);

    /**
     * Adds the text to the Phrase. This is a shortcut for creating a {@link Chunk} then adding it
     * to the Phrase.
     *
     * @param text the text to add
     */
    void addText(String text);
}
