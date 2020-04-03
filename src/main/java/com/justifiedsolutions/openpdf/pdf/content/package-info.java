/*
 * SPDX-License-Identifier: (LGPL-3.0-only OR MPL-2.0)
 *
 * Copyright (c) 2020 Justified Solutions. All rights reserved.
 */

/**
 * This package models the content of a PDF document. {@link com.justifiedsolutions.openpdf.pdf.Chapter}s
 * must be created via a {@link com.justifiedsolutions.openpdf.pdf.Document} and {@link
 * com.justifiedsolutions.openpdf.pdf.Section}s must be created via Chapters.
 * <p>
 * A {@link com.justifiedsolutions.openpdf.pdf.content.Cell} is created via a {@link
 * com.justifiedsolutions.openpdf.pdf.content.Table}. They fill the Table in a left->right and
 * top->bottom order.
 * <p>
 * {@link com.justifiedsolutions.openpdf.pdf.content.Chunk}s, {@link
 * com.justifiedsolutions.openpdf.pdf.content.Phrase}s, {@link com.justifiedsolutions.openpdf.pdf.content.Paragraph}s,
 * and {@link com.justifiedsolutions.openpdf.pdf.content.Table}s are all created via normal
 * constructors.
 */
package com.justifiedsolutions.openpdf.pdf.content;