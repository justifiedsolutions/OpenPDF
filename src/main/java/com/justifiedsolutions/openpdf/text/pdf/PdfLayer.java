/*
 * Copyright 2004 by Paulo Soares.
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

import java.util.ArrayList;

/**
 * An optional content group is a dictionary representing a collection of graphics
 * that can be made visible or invisible dynamically by users of viewer applications.
 * In iText they are referenced as layers.
 *
 * @author Paulo Soares (psoares@consiste.pt)
 */
public class PdfLayer extends PdfDictionary implements PdfOCG {
    protected PdfIndirectReference ref;
    protected ArrayList<PdfLayer> children;
    protected PdfLayer parent;
    protected String title;

    /**
     * Holds value of property on.
     */
    private boolean on = true;
    
    /**
     * Holds value of property onPanel.
     */
    private boolean onPanel = true;

    String getTitle() {
        return title;
    }


    /**
     * Gets the parent layer.
     * @return the parent layer or <CODE>null</CODE> if the layer has no parent
     */    
    public PdfLayer getParent() {
        return parent;
    }
    
    /**
     * Gets the children layers.
     * @return the children layers or <CODE>null</CODE> if the layer has no children
     */    
    public ArrayList<PdfLayer> getChildren() {
        return children;
    }
    
    /**
     * Gets the <CODE>PdfIndirectReference</CODE> that represents this layer.
     * @return the <CODE>PdfIndirectReference</CODE> that represents this layer
     */    
    public PdfIndirectReference getRef() {
        return ref;
    }

    /**
     * Sets the name of this layer.
     * @param name the name of this layer
     */    
    public void setName(String name) {
        put(PdfName.NAME, new PdfString(name, PdfObject.TEXT_UNICODE));
    }
    
    /**
     * Gets the dictionary representing the layer. It just returns <CODE>this</CODE>.
     * @return the dictionary representing the layer
     */    
    public PdfObject getPdfObject() {
        return this;
    }
    
    /**
     * Gets the initial visibility of the layer.
     * @return the initial visibility of the layer
     */
    public boolean isOn() {
        return this.on;
    }

    /**
     * Gets the layer visibility in Acrobat's layer panel
     * @return the layer visibility in Acrobat's layer panel
     */
    public boolean isOnPanel() {
        return this.onPanel;
    }

}
