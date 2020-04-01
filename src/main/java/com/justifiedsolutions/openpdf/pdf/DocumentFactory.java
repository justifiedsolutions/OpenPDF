/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

package com.justifiedsolutions.openpdf.pdf;

import java.util.Objects;

/**
 * Factory for creating {@link Document}s.
 */
public final class DocumentFactory {

    /**
     * Prevent the DocumentFactory from being instantiated.
     */
    private DocumentFactory(){}

    /**
     * Creates a {@link Document} of the specified {@link PageSize}.
     * @param size the size of the page
     * @param margin the document margin
     * @return a document of the specified size
     */
    public static Document createDocument(PageSize size, Margin margin) {
        Objects.requireNonNull(size);
        Objects.requireNonNull(margin);
        //TODO
        return null;
    }
}
