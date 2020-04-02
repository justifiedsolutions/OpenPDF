/*
 * Copyright 2003, 2004 by Paulo Soares.
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

import com.justifiedsolutions.openpdf.text.DocumentException;
import com.justifiedsolutions.openpdf.text.pdf.interfaces.PdfViewerPreferences;
import com.justifiedsolutions.openpdf.text.xml.xmp.XmpWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/** Applies extra content to the pages of a PDF document.
 * This extra content can be all the objects allowed in PdfContentByte
 * including pages from other Pdfs. The original PDF will keep
 * all the interactive elements including bookmarks, links and form fields.
 * <p>
 * It is also possible to change the field values and to
 * flatten them. New fields can be added but not flattened.
 * @author Paulo Soares (psoares@consiste.pt)
 */
public class PdfStamper
    implements PdfViewerPreferences {
    /**
     * The writer
     */    
    protected PdfStamperImp stamper;
    private Map<String, String> moreInfo;
    private boolean cleanMetadata = false;

    /** Starts the process of adding extra content to an existing PDF
     * document.
     * @param reader the original document. It cannot be reused
     * @param os the output stream
     * @throws DocumentException on error
     * @throws IOException on error
     */
    public PdfStamper(PdfReader reader, OutputStream os) throws DocumentException, IOException {
        stamper = new PdfStamperImp(reader, os, '\0', false);
    }

    /**
     * Starts the process of adding extra content to an existing PDF
     * document.
     * @param reader the original document. It cannot be reused
     * @param os the output stream
     * @param pdfVersion the new pdf version or '\0' to keep the same version as the original
     * document
     * @throws DocumentException on error
     * @throws IOException on error
     */
    public PdfStamper(PdfReader reader, OutputStream os, char pdfVersion) throws DocumentException, IOException {
        stamper = new PdfStamperImp(reader, os, pdfVersion, false);
    }

    /**
     * Starts the process of adding extra content to an existing PDF
     * document, possibly as a new revision.
     * @param reader the original document. It cannot be reused
     * @param os the output stream
     * @param pdfVersion the new pdf version or '\0' to keep the same version as the original
     * document
     * @param append if <CODE>true</CODE> appends the document changes as a new revision. This is
     * only useful for multiple signatures as nothing is gained in speed or memory
     * @throws DocumentException on error
     * @throws IOException on error
     */
    public PdfStamper(PdfReader reader, OutputStream os, char pdfVersion, boolean append) throws DocumentException, IOException {
        stamper = new PdfStamperImp(reader, os, pdfVersion, append);
    }

    /** Gets the optional <CODE>String</CODE> map to add or change values in
     * the info dictionary.
     * @return the map or <CODE>null</CODE>
     *
     */
    public Map<String, String> getInfoDictionary() {
        return moreInfo;
    }

    /**
     * An option to make this stamper to clean metadata in the generated file. You must call this method before closing the stamper.
     */
    public void cleanMetadata() {
      Map<String, String> meta = new HashMap<>();
      meta.put("Title", null);
      meta.put("Author", null);
      meta.put("Subject", null);
      meta.put("Producer", null);
      meta.put("Keywords", null);
      meta.put("Creator", null);
      meta.put("CreationDate", null);
      meta.put("ModDate",null);
      setInfoDictionary(meta);
      this.cleanMetadata = true;
    }

    /** An optional <CODE>String</CODE> map to add or change values in
     * the info dictionary. Entries with <CODE>null</CODE>
     * values delete the key in the original info dictionary
     * @param moreInfo additional entries to the info dictionary
     *
     */
    public void setInfoDictionary(Map<String, String> moreInfo) {
        this.moreInfo = moreInfo;
    }


    /**
     * Closes the document. No more content can be written after the
     * document is closed.
     * <p>
     * If closing a signed document with an external signature the closing must be done
     * in the <CODE>PdfSignatureAppearance</CODE> instance.
     * @throws DocumentException on error
     * @throws IOException on error
     */
    public void close() throws DocumentException, IOException {
        if (cleanMetadata && stamper.xmpMetadata == null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                XmpWriter writer = new XmpWriter(baos, moreInfo);
                writer.close();
                stamper.setXmpMetadata(baos.toByteArray());
            } catch (IOException ignore) {
                // ignore exception
            }
        }
        stamper.close(moreInfo);
    }

    /** Gets a <CODE>PdfContentByte</CODE> to write over the page of
     * the original document.
     * @param pageNum the page number where the extra content is written
     * @return a <CODE>PdfContentByte</CODE> to write over the page of
     * the original document
     */
    public PdfContentByte getOverContent(int pageNum) {
        return stamper.getOverContent(pageNum);
    }

    /** Gets the underlying PdfWriter.
     * @return the underlying PdfWriter
     */
    public PdfWriter getWriter() {
        return stamper;
    }

    /** Gets the underlying PdfReader.
     * @return the underlying PdfReader
     */
    public PdfReader getReader() {
        return stamper.reader;
    }


    /**
     * Sets the viewer preferences.
     * @param preferences the viewer preferences
     * @see PdfViewerPreferences#setViewerPreferences(int)
     */
    public void setViewerPreferences(int preferences) {
        stamper.setViewerPreferences(preferences);
    }
    
    /** Adds a viewer preference
     * @param key a key for a viewer preference
     * @param value the value for the viewer preference
     * @see PdfViewerPreferences#addViewerPreference
     */
    
    public void addViewerPreference(PdfName key, PdfObject value) {
        stamper.addViewerPreference(key, value);
    }

    /**
     * Sets the XMP metadata.
     * @param xmp
     * @see PdfWriter#setXmpMetadata(byte[])
     */
    public void setXmpMetadata(byte[] xmp) {
        stamper.setXmpMetadata(xmp);
    }


}
