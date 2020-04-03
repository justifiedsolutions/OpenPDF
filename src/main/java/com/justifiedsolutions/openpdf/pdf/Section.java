/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

import com.justifiedsolutions.openpdf.pdf.content.Chunk;
import com.justifiedsolutions.openpdf.pdf.content.Content;
import com.justifiedsolutions.openpdf.pdf.content.Paragraph;
import com.justifiedsolutions.openpdf.pdf.content.Phrase;
import com.justifiedsolutions.openpdf.pdf.content.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
public class Section {

    private final int sectionNumber;
    private final Paragraph title;
    private final List<Content> content = new ArrayList<>();
    private final List<Section> sections = new ArrayList<>();
    private boolean startsNewPage = false;
    private boolean displaySectionNumber = true;

    protected Section(int sectionNumber, Paragraph title) {
        this.sectionNumber = sectionNumber;
        this.title = title;
    }

    /**
     * Gets the Section number.
     *
     * @return the section number
     */
    public int getSectionNumber() {
        return sectionNumber;
    }

    /**
     * Returns the title of the section.
     *
     * @return the title
     */
    public Paragraph getTitle() {
        return title;
    }

    /**
     * Specifies if the Section should start a new page. The default value is <code>false</code>.
     *
     * @return true if the section should start a new page
     */
    public boolean isStartsNewPage() {
        return startsNewPage;
    }

    /**
     * Specifies if the Section should start a new page. The default value is <code>false</code>.
     *
     * @param startsNewPage true if the section should start a new page
     */
    public void setStartsNewPage(boolean startsNewPage) {
        this.startsNewPage = startsNewPage;
    }

    /**
     * Specifies if the section number should be displayed. The default is <code>true</code>.
     *
     * @return true if the section number should be displayed
     */
    public boolean isDisplaySectionNumber() {
        return displaySectionNumber;
    }

    /**
     * Specifies if the section number should be displayed. The default is <code>true</code>.
     *
     * @param displaySectionNumber true if the section number should be displayed
     */
    public void setDisplaySectionNumber(boolean displaySectionNumber) {
        this.displaySectionNumber = displaySectionNumber;
    }

    /**
     * Add {@link Content} to the Section.
     *
     * @param content the content to add
     * @throws NullPointerException     if content is null
     * @throws IllegalArgumentException if content is not a Chunk, Phrase, Paragraph, or Table
     */
    public void addContent(Content content) {
        Objects.requireNonNull(content);
        if ((content instanceof Chunk) || (content instanceof Phrase)
                || (content instanceof Paragraph) || (content instanceof Table)) {
            this.content.add(content);
        } else {
            throw new IllegalArgumentException("Invalid content type: " + content.getClass());
        }
    }

    /**
     * Gets the {@link java.util.Collections#unmodifiableList(List)} of {@link Content} in the
     * Section.
     *
     * @return the content in the section
     */
    public List<Content> getContent() {
        return Collections.unmodifiableList(content);
    }

    /**
     * Creates a Section and adds it as a subsection of this one.
     *
     * @param title the title of the subsection
     * @return the Section
     * @throws NullPointerException if title is <code>null</code>
     */
    public Section addSection(Paragraph title) {
        Objects.requireNonNull(title);
        int num = sections.size() + 1;
        Section section = new Section(num, title);
        sections.add(section);
        return section;
    }

    /**
     * Gets a {@link java.util.Collections#unmodifiableList(List)} of all of the subsections of this
     * Section.
     *
     * @return the list of subsections
     */
    public List<Section> getSections() {
        return Collections.unmodifiableList(sections);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Section section = (Section) o;
        return sectionNumber == section.sectionNumber &&
                startsNewPage == section.startsNewPage &&
                displaySectionNumber == section.displaySectionNumber &&
                title.equals(section.title) &&
                content.equals(section.content) &&
                sections.equals(section.sections);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sectionNumber, title, content, sections, startsNewPage,
                displaySectionNumber);
    }
}
