/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf.content;

import java.util.List;

/**
 * A Section displays it's content as follows:
 * <pre>
 *     [SectionNumber] [Title]
 *     [Content]
 *     [SectionNumber.SubsSectionNumber] [SubSection Title]
 *     [SubSection Content]
 *     ...
 * </pre>
 */
public interface Section extends Content {

    /**
     * Gets the Section number.
     *
     * @return the section number
     */
    int getSectionNumber();

    /**
     * Returns the title of the section.
     *
     * @return the title
     */
    Paragraph getTitle();

    /**
     * Specifies if the Section should start a new page. The default value is <code>false</code>.
     *
     * @return true if the section should start a new page
     */
    boolean isStartsNewPage();

    /**
     * Specifies if the Section should start a new page. The default value is <code>false</code>.
     *
     * @param startsNewPage true if the section should start a new page
     */
    void setStartsNewPage(boolean startsNewPage);

    /**
     * Specifies if the section number should be displayed. The default is <code>true</code>.
     *
     * @return true if the section number should be displayed
     */
    boolean isDisplaySectionNumber();

    /**
     * Specifies if the section number should be displayed. The default is <code>true</code>.
     *
     * @param displaySectionNumber true if the section number should be displayed
     */
    void setDisplaySectionNumber(boolean displaySectionNumber);

    /**
     * Add {@link Content} to the Section.
     *
     * @param content the content to add
     */
    void addContent(Content content);

    /**
     * Gets the {@link List} of {@link Content} in the Section.
     *
     * @return the content in the section
     */
    List<Content> getContent();

    /**
     * Creates a Section and adds it as a subsection of this one.
     *
     * @param title the title of the subsection
     * @return the Section
     */
    Section addSection(Paragraph title);

    /**
     * Gets a {@link List} of all of the subsections of this Section.
     *
     * @return the list of subsections
     */
    List<Section> getSections();
}
