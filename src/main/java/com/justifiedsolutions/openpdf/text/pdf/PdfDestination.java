/*
 * $Id: PdfDestination.java 4094 2009-11-12 14:29:35Z blowagie $
 *
 * Copyright 1999, 2000, 2001, 2002 Bruno Lowagie
 *
 * The contents of this file are subject to the Mozilla Public License Version 1.1
 * (the "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the License.
 *
 * The Original Code is 'iText, a free JAVA-PDF library'.
 *
 * The Initial Developer of the Original Code is Bruno Lowagie. Portions created by
 * the Initial Developer are Copyright (C) 1999, 2000, 2001, 2002 by Bruno Lowagie.
 * All Rights Reserved.
 * Co-Developer of the code is Paulo Soares. Portions created by the Co-Developer
 * are Copyright (C) 2000, 2001, 2002 by Paulo Soares. All Rights Reserved.
 *
 * Contributor(s): all the names of the contributors are added in the source code
 * where applicable.
 *
 * Alternatively, the contents of this file may be used under the terms of the
 * LGPL license (the "GNU LIBRARY GENERAL PUBLIC LICENSE"), in which case the
 * provisions of LGPL are applicable instead of those above.  If you wish to
 * allow use of your version of this file only under the terms of the LGPL
 * License and not to allow others to use your version of this file under
 * the MPL, indicate your decision by deleting the provisions above and
 * replace them with the notice and other provisions required by the LGPL.
 * If you do not delete the provisions above, a recipient may use your version
 * of this file under either the MPL or the GNU LIBRARY GENERAL PUBLIC LICENSE.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the MPL as stated above or under the terms of the GNU
 * Library General Public License as published by the Free Software Foundation;
 * either version 2 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Library general Public License for more
 * details.
 *
 * If you didn't download this code from the following link, you should check if
 * you aren't using an obsolete version:
 * https://github.com/LibrePDF/OpenPDF
 */

package com.justifiedsolutions.openpdf.text.pdf;

public class PdfDestination extends PdfArray {

    /**
     * This is a possible destination type
     */
    public static final int FITH = 2;

    /**
     * This is a possible destination type
     */
    public static final int FITV = 3;

    /**
     * This is a possible destination type
     */
    public static final int FITBH = 6;

    /**
     * This is a possible destination type
     */
    public static final int FITBV = 7;

    /**
     * Is the indirect reference to a page already added?
     */
    private boolean status = false;

    /**
     * Constructs a new <CODE>PdfDestination</CODE>.
     * <p>
     * If <VAR>type</VAR> equals <VAR>FITBH</VAR> / <VAR>FITBV</VAR>, the width / height of the
     * bounding box of a page will fit the window of the Reader. The parameter will specify the y /
     * x coordinate of the top / left edge of the window. If the <VAR>type</VAR> equals
     * <VAR>FITH</VAR> or <VAR>FITV</VAR> the width / height of the entire page will fit the window
     * and the parameter will specify the y / x coordinate of the top / left edge. In all other
     * cases the type will be set to <VAR>FITH</VAR>.
     *
     * @param type      the destination type
     * @param parameter a parameter to combined with the destination type
     */
    public PdfDestination(int type, float parameter) {
        super(new PdfNumber(parameter));
        switch (type) {
            default:
                addFirst(PdfName.FITH);
                break;
            case FITV:
                addFirst(PdfName.FITV);
                break;
            case FITBH:
                addFirst(PdfName.FITBH);
                break;
            case FITBV:
                addFirst(PdfName.FITBV);
        }
    }

    /**
     * Checks if an indirect reference to a page has been added.
     *
     * @return <CODE>true</CODE> or <CODE>false</CODE>
     */
    public boolean hasPage() {
        return status;
    }

    /**
     * Adds the indirect reference of the destination page.
     *
     * @param page an indirect reference
     * @return true if the page reference was added
     */
    public boolean addPage(PdfIndirectReference page) {
        if (!status) {
            addFirst(page);
            status = true;
            return true;
        }
        return false;
    }
}