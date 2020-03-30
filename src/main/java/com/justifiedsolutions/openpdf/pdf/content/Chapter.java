package com.justifiedsolutions.openpdf.pdf.content;

/**
 * A Chapter is a specialization of a {@link Section} that can only be created by a {@link
 * com.justifiedsolutions.openpdf.pdf.Document}. If a Document has Chapters it cannot have any other
 * type of {@link Content} directly added to it. A new Chapter always starts a new page in the
 * Document.
 *
 * @see com.justifiedsolutions.openpdf.pdf.Document
 * @see Section
 */
public interface Chapter extends Section {

}
