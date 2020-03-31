package com.justifiedsolutions.openpdf.pdf.content;

/**
 * A Table is a type of {@link Content} that can be added to a PDF {@link
 * com.justifiedsolutions.openpdf.pdf.Document}.
 */
public interface Table extends Content {

    /**
     * Gets the number of columns in the Table.
     *
     * @return the number of columns
     */
    int getNumberOfColumns();

    /**
     * Specifies whether the entire Table should be kept together on the same page. The default
     * value is <code>false</code>.
     *
     * @return true if the table should be kept together on the same page
     */
    boolean isKeepTogether();

    /**
     * Specifies whether the entire Table should be kept together on the same page.
     *
     * @param keepTogether true if the table should be kept together
     */
    void setKeepTogether(boolean keepTogether);

    /**
     * Gets the percentage of the page width that the table should occupy. A value of 100 would go
     * from left margin to right margin. The default value is <code>80</code>.
     *
     * @return the width percentage
     */
    float getWidthPercentage();

    /**
     * Sets the percentage of the page width that the table should occupy. A value of 100 would go
     * from left margin to right margin.
     *
     * @param percentage the width percentage
     */
    void setWidthPercentage(float percentage);

    /**
     * Gets the amount of empty space above the table. The default value is <code>0</code>.
     *
     * @return the spacing before the table
     */
    float getSpacingBefore();

    /**
     * Sets the amount of empty space above the table.
     *
     * @param spacing the spacing before the table
     */
    void setSpacingBefore(float spacing);

    /**
     * Gets the amount of empty space below the table. The default value is <code>0</code>.
     *
     * @return the spacing after the table
     */
    float getSpacingAfter();

    /**
     * Sets the amount of empty space below the table.
     *
     * @param spacing the spacing after the table
     */
    void setSpacingAfter(float spacing);

    /**
     * Creates a new empty {@link Cell} and adds it to the Table.
     *
     * @return a new cell
     */
    Cell createCell();

    /**
     * Creates a new {@link Cell} with the specified {@link Phrase} and adds it to the Table.
     *
     * @param phrase the content for the cell
     * @return a new cell
     */
    Cell createCell(Phrase phrase);
}
