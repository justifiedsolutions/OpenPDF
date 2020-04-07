/*
 * $Id: PdfWriter.java 4095 2009-11-12 14:40:41Z blowagie $
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

import com.justifiedsolutions.openpdf.text.DocWriter;
import com.justifiedsolutions.openpdf.text.Document;
import com.justifiedsolutions.openpdf.text.DocumentException;
import com.justifiedsolutions.openpdf.text.ExceptionConverter;
import com.justifiedsolutions.openpdf.text.Image;
import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import com.justifiedsolutions.openpdf.text.pdf.collection.PdfCollection;
import com.justifiedsolutions.openpdf.text.pdf.events.PdfPageEventForwarder;
import com.justifiedsolutions.openpdf.text.pdf.interfaces.PdfVersion;
import com.justifiedsolutions.openpdf.text.pdf.interfaces.PdfXConformance;
import com.justifiedsolutions.openpdf.text.pdf.internal.PdfVersionImp;
import com.justifiedsolutions.openpdf.text.pdf.internal.PdfXConformanceImp;
import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * A <CODE>DocWriter</CODE> class for PDF.
 * <P>
 * When this <CODE>PdfWriter</CODE> is added
 * to a certain <CODE>PdfDocument</CODE>, the PDF representation of every Element
 * added to this Document will be written to the outputstream.</P>
 */

public class PdfWriter extends DocWriter implements
    PdfVersion,
    PdfXConformance {

    /**
     * The highest generation number possible.
     * @since    iText 2.1.6
     */
    public static final int GENERATION_MAX = 65535;
    
// INNER CLASSES

    /**
     * This class generates the structure of a PDF document.
     * <P>
     * This class covers the third section of Chapter 5 in the 'Portable Document Format
     * Reference Manual version 1.3' (page 55-60). It contains the body of a PDF document
     * (section 5.14) and it can also generate a Cross-reference Table (section 5.15).
     *
     * @see        PdfWriter
     * @see        PdfObject
     * @see        PdfIndirectObject
     */

    public static class PdfBody {

        // inner classes

        /**
         * <CODE>PdfCrossReference</CODE> is an entry in the PDF Cross-Reference table.
         */

        public static class PdfCrossReference implements Comparable<PdfCrossReference> {

          /**
           * String template for cross-reference entry PDF representation.
           *
           * @see Formatter
           */
          private static final String CROSS_REFERENCE_ENTRY_FORMAT = "%010d %05d %c \n";

            // membervariables
            private int type;

            /**    Byte offset in the PDF file. */
            private final long offset;

            private int refnum;
            /**    generation of the object. */
            private int generation;

            // constructors
            /**
             * Constructs a cross-reference element for a PdfIndirectObject.
             * @param refnum
             * @param    offset        byte offset of the object
             * @param    generation    generation number of the object
             */

            public PdfCrossReference(int refnum, long offset, int generation) {
                type = 0;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = generation;
            }

            /**
             * Constructs a cross-reference element for a PdfIndirectObject.
             * @param refnum
             * @param    offset        byte offset of the object
             */

            public PdfCrossReference(int refnum, long offset) {
                type = 1;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = 0;
            }

            public PdfCrossReference(int type, int refnum, long offset, int generation) {
                this.type = type;
                this.offset = offset;
                this.refnum = refnum;
                this.generation = generation;
            }

            int getRefnum() {
                return refnum;
            }

            /**
             * Writes PDF representation of cross-reference entry to passed output stream.
             *
             * @param os Output stream this entry to write to
             * @throws IOException If any I/O error occurs
             */
            public void toPdf(OutputStream os) throws IOException {
              // TODO: are generation number and 'In use' keyword bound that way?
              final char inUse = generation == GENERATION_MAX ? 'f' : 'n';
              os.write(String.format(CROSS_REFERENCE_ENTRY_FORMAT, offset, generation, inUse).getBytes());
            }

            /**
             * Writes PDF syntax to the OutputStream
             * @param midSize
             * @param os
             * @throws IOException
             */
            public void toPdf(int midSize, OutputStream os) throws IOException {
                os.write((byte)type);
                while (--midSize >= 0)
                    os.write((byte)((offset >>> (8 * midSize)) & 0xff));
                os.write((byte)((generation >>> 8) & 0xff));
                os.write((byte)(generation & 0xff));
            }

            /**
             * Compares current {@link PdfCrossReference entry} with passed {@code reference} by PDF object number.
             */
            @Override
            public int compareTo(final PdfCrossReference reference) {
              return Integer.compare(refnum, reference.refnum);
            }

            /**
             * Checks if two entries are equal if their PDF object numbers are equal.
             *
             * @param obj Another cross-reference entry
             * @return If null, not of type {@link PdfCrossReference} or object numbers are not equal,
             * returns false; true otherwise
             */
            @Override
            public boolean equals(Object obj) {
              if (!(obj instanceof PdfCrossReference)) {
                return false;
              }

              final PdfCrossReference other = (PdfCrossReference)obj;
              return refnum == other.refnum;
            }

            @Override
            public int hashCode() {
              return refnum;
            }
        }

        private static final int OBJSINSTREAM = 200;

        // membervariables

        /** array containing the cross-reference table of the normal objects. */
        private TreeSet<PdfCrossReference> xrefs;
        private int refnum;
        /** the current byte position in the body. */
        private long position;
        private PdfWriter writer;
        private ByteBuffer index;
        private ByteBuffer streamObjects;
        private int currentObjNum;
        private int numObj = 0;

        // constructors

        /**
         * Constructs a new <CODE>PdfBody</CODE>.
         * @param writer
         */
        PdfBody(PdfWriter writer) {
            xrefs = new TreeSet<>();
            xrefs.add(new PdfCrossReference(0, 0, GENERATION_MAX));
            position = writer.getOs().getCounter();
            refnum = 1;
            this.writer = writer;
        }

        // methods

        private PdfWriter.PdfBody.PdfCrossReference addToObjStm(PdfObject obj, int nObj) throws IOException {
            if (numObj >= OBJSINSTREAM)
                flushObjStm();
            if (index == null) {
                index = new ByteBuffer();
                streamObjects = new ByteBuffer();
                currentObjNum = getIndirectReferenceNumber();
                numObj = 0;
            }
            int p = streamObjects.size();
            int idx = numObj++;
            obj.toPdf(writer, streamObjects);
            streamObjects.append(' ');
            index.append(nObj).append(' ').append(p).append(' ');
            return new PdfWriter.PdfBody.PdfCrossReference(2, nObj, currentObjNum, idx);
        }

        private void flushObjStm() throws IOException {
            if (numObj == 0)
                return;
            int first = index.size();
            index.append(streamObjects);
            PdfStream stream = new PdfStream(index.toByteArray());
            stream.flateCompress(writer.getCompressionLevel());
            stream.put(PdfName.TYPE, PdfName.OBJSTM);
            stream.put(PdfName.N, new PdfNumber(numObj));
            stream.put(PdfName.FIRST, new PdfNumber(first));
            add(stream, currentObjNum);
            index = null;
            streamObjects = null;
            numObj = 0;
        }

        /**
         * Adds a <CODE>PdfObject</CODE> to the body.
         * <P>
         * This methods creates a <CODE>PdfIndirectObject</CODE> with a
         * certain number, containing the given <CODE>PdfObject</CODE>.
         * It also adds a <CODE>PdfCrossReference</CODE> for this object
         * to an <CODE>ArrayList</CODE> that will be used to build the
         * Cross-reference Table.
         *
         * @param        object            a <CODE>PdfObject</CODE>
         * @return        a <CODE>PdfIndirectObject</CODE>
         * @throws IOException
         */

        PdfIndirectObject add(PdfObject object) throws IOException {
            return add(object, getIndirectReferenceNumber());
        }

        PdfIndirectObject add(PdfObject object, boolean inObjStm) throws IOException {
            return add(object, getIndirectReferenceNumber(), inObjStm);
        }

        /**
         * Gets a PdfIndirectReference for an object that will be created in the future.
         * @return a PdfIndirectReference
         */

        PdfIndirectReference getPdfIndirectReference() {
            return new PdfIndirectReference(0, getIndirectReferenceNumber());
        }

        int getIndirectReferenceNumber() {
            int n = refnum++;
            xrefs.add(new PdfCrossReference(n, 0, GENERATION_MAX));
            return n;
        }

        /**
         * Adds a <CODE>PdfObject</CODE> to the body given an already existing
         * PdfIndirectReference.
         * <P>
         * This methods creates a <CODE>PdfIndirectObject</CODE> with the number given by
         * <CODE>ref</CODE>, containing the given <CODE>PdfObject</CODE>.
         * It also adds a <CODE>PdfCrossReference</CODE> for this object
         * to an <CODE>ArrayList</CODE> that will be used to build the
         * Cross-reference Table.
         *
         * @param        object            a <CODE>PdfObject</CODE>
         * @param        ref                a <CODE>PdfIndirectReference</CODE>
         * @return        a <CODE>PdfIndirectObject</CODE>
         * @throws IOException
         */

        PdfIndirectObject add(PdfObject object, PdfIndirectReference ref) throws IOException {
            return add(object, ref.getNumber());
        }

        PdfIndirectObject add(PdfObject object, PdfIndirectReference ref, boolean inObjStm) throws IOException {
            return add(object, ref.getNumber(), inObjStm);
        }

        PdfIndirectObject add(PdfObject object, int refNumber) throws IOException {
            return add(object, refNumber, true); // to false
        }

        PdfIndirectObject add(PdfObject object, int refNumber, boolean inObjStm) throws IOException {
            if (inObjStm && object.canBeInObjStm() && writer.isFullCompression()) {
                PdfCrossReference pxref = addToObjStm(object, refNumber);
                PdfIndirectObject indirect = new PdfIndirectObject(refNumber, object, writer);
                if (!xrefs.add(pxref)) {
                    xrefs.remove(pxref);
                    xrefs.add(pxref);
                }
                return indirect;
            }
            else {
                PdfIndirectObject indirect = new PdfIndirectObject(refNumber, object, writer);
                PdfCrossReference pxref = new PdfCrossReference(refNumber, position);
                if (!xrefs.add(pxref)) {
                    xrefs.remove(pxref);
                    xrefs.add(pxref);
                }
                indirect.writeTo(writer.getOs());
                position = writer.getOs().getCounter();
                return indirect;
            }
        }

        /**
         * Returns the offset of the Cross-Reference table.
         *
         * @return        an offset
         */

        long offset() {
            return position;
        }

        /**
         * Returns the total number of objects contained in the CrossReferenceTable of this <CODE>Body</CODE>.
         *
         * @return    a number of objects
         */

        int size() {
            return Math.max((xrefs.last()).getRefnum() + 1, refnum);
        }

        /**
         * Returns the CrossReferenceTable of the <CODE>Body</CODE>.
         * @param os
         * @param root
         * @param info
         * @param encryption
         * @param fileID
         * @param prevxref
         * @throws IOException
         */

        void writeCrossReferenceTable(OutputStream os, PdfIndirectReference root, PdfIndirectReference info, PdfIndirectReference encryption, PdfObject fileID, int prevxref) throws IOException {
            int refNumber = 0;
            // Old-style xref tables limit object offsets to 10 digits
            boolean useNewXrefFormat = writer.isFullCompression() || position > 9_999_999_999L;
            if (useNewXrefFormat) {
                flushObjStm();
                refNumber = getIndirectReferenceNumber();
                xrefs.add(new PdfCrossReference(refNumber, position));
            }
            PdfCrossReference entry = xrefs.first();
            int first = entry.getRefnum();
            int len = 0;
            ArrayList<Integer> sections = new ArrayList<>();
            for (PdfCrossReference xref1 : xrefs) {
                entry = xref1;
                if (first + len == entry.getRefnum())
                    ++len;
                else {
                    sections.add(first);
                    sections.add(len);
                    first = entry.getRefnum();
                    len = 1;
                }
            }
            sections.add(first);
            sections.add(len);
            PdfTrailer trailer = new PdfTrailer(size(), root, info, encryption, fileID, prevxref);
            if (useNewXrefFormat) {
                int mid = 8 - (Long.numberOfLeadingZeros(position) >> 3);
                ByteBuffer buf = new ByteBuffer();

                for (PdfCrossReference xref : xrefs) {
                    entry = xref;
                    entry.toPdf(mid, buf);
                }
                PdfStream xr = new PdfStream(buf.toByteArray());
                buf = null;
                xr.flateCompress(writer.getCompressionLevel());
                xr.putAll(trailer);
                xr.put(PdfName.W, new PdfArray(new int[]{1, mid, 2}));
                xr.put(PdfName.TYPE, PdfName.XREF);
                PdfArray idx = new PdfArray();
                for (Integer section : sections) idx.add(new PdfNumber(section));
                xr.put(PdfName.INDEX, idx);
                PdfIndirectObject indirect = new PdfIndirectObject(refNumber, xr, writer);
                indirect.writeTo(writer.getOs());
            }
            else {
                os.write(getISOBytes("xref\n"));
                Iterator<PdfCrossReference> i = xrefs.iterator();
                for (int k = 0; k < sections.size(); k += 2) {
                    first = sections.get(k);
                    len = sections.get(k + 1);
                    os.write(getISOBytes(String.valueOf(first)));
                    os.write(getISOBytes(" "));
                    os.write(getISOBytes(String.valueOf(len)));
                    os.write('\n');
                    while (len-- > 0) {
                        entry = i.next();
                        entry.toPdf(os);
                    }
                }
                // make the trailer
                trailer.toPdf(writer, os);
            }
        }
    }

    /**
     * <CODE>PdfTrailer</CODE> is the PDF Trailer object.
     * <P>
     * This object is described in the 'Portable Document Format Reference Manual version 1.3'
     * section 5.16 (page 59-60).
     */

    static class PdfTrailer extends PdfDictionary {

        // constructors

        /**
         * Constructs a PDF-Trailer.
         *
         * @param        size        the number of entries in the <CODE>PdfCrossReferenceTable</CODE>
         * @param        root        an indirect reference to the root of the PDF document
         * @param        info        an indirect reference to the info object of the PDF document
         * @param encryption
         * @param fileID
         * @param prevxref
         */

        PdfTrailer(int size, PdfIndirectReference root, PdfIndirectReference info, PdfIndirectReference encryption, PdfObject fileID, int prevxref) {
            put(PdfName.SIZE, new PdfNumber(size));
            put(PdfName.ROOT, root);
            if (info != null) {
                put(PdfName.INFO, info);
            }
            if (encryption != null)
                put(PdfName.ENCRYPT, encryption);
            if (fileID != null)
                put(PdfName.ID, fileID);
            if (prevxref > 0)
                put(PdfName.PREV, new PdfNumber(prevxref));
        }

        /**
         * Returns the PDF representation of this <CODE>PdfObject</CODE>.
         * @param writer
         * @param os
         * @throws IOException
         */
        public void toPdf(PdfWriter writer, OutputStream os) throws IOException {
            os.write(getISOBytes("trailer\n"));
            super.toPdf(null, os);
            os.write('\n');
        }
    }

//    ESSENTIALS

//    Construct a PdfWriter instance

    /**
     * Constructs a <CODE>PdfWriter</CODE>.
     */
    protected PdfWriter() {
    }

    /**
     * Constructs a <CODE>PdfWriter</CODE>.
     * <P>
     * Remark: a PdfWriter can only be constructed by calling the method
     * <CODE>getInstance(Document document, OutputStream os)</CODE>.
     *
     * @param    document    The <CODE>PdfDocument</CODE> that has to be written
     * @param    os            The <CODE>OutputStream</CODE> the writer has to write to.
     */

    protected PdfWriter(PdfDocument document, OutputStream os) {
        super(document, os);
        pdf = document;
        directContent = new PdfContentByte(this);
        directContentUnder = new PdfContentByte(this);
    }

    /**
     * Use this method to get an instance of the <CODE>PdfWriter</CODE>.
     *
     * @param    document    The <CODE>Document</CODE> that has to be written
     * @param    os    The <CODE>OutputStream</CODE> the writer has to write to.
     * @return    a new <CODE>PdfWriter</CODE>
     *
     * @throws    DocumentException on error
     */

    public static PdfWriter getInstance(Document document, OutputStream os)
    throws DocumentException {
        PdfDocument pdf = new PdfDocument();
        document.addDocListener(pdf);
        PdfWriter writer = new PdfWriter(pdf, os);
        pdf.addWriter(writer);
        return writer;
    }

//    the PdfDocument instance

    /** the pdfdocument object. */
    protected PdfDocument pdf;

    /**
     * Gets the <CODE>PdfDocument</CODE> associated with this writer.
     * @return the <CODE>PdfDocument</CODE>
     */

    PdfDocument getPdfDocument() {
        return pdf;
    }

    /**
     * Use this method to get the info dictionary if you want to
     * change it directly (add keys and values to the info dictionary).
     * @return the info dictionary
     */
    public PdfDictionary getInfo() {
        return pdf.getInfo();
    }

    //    the PdfDirectContentByte instances

/*
 * You should see Direct Content as a canvas on which you can draw
 * graphics and text. One canvas goes on top of the page (getDirectContent),
 * the other goes underneath (getDirectContentUnder).
 * You can always the same object throughout your document,
 * even if you have moved to a new page. Whatever you add on
 * the canvas will be displayed on top or under the current page.
 */

    /** The direct content in this document. */
    protected PdfContentByte directContent;

    /** The direct content under in this document. */
    protected PdfContentByte directContentUnder;

    /**
     * Use this method to get the direct content for this document.
     * There is only one direct content, multiple calls to this method
     * will allways retrieve the same object.
     * @return the direct content
     */

    public PdfContentByte getDirectContent() {
        if (!open)
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        return directContent;
    }

    /**
     * Use this method to get the direct content under for this document.
     * There is only one direct content, multiple calls to this method
     * will always retrieve the same object.
     * @return the direct content
     */

    public PdfContentByte getDirectContentUnder() {
        if (!open)
            throw new RuntimeException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        return directContentUnder;
    }

    /**
     * Resets all the direct contents to empty.
     * This happens when a new page is started.
     */
    void resetContent() {
        directContent.reset();
        directContentUnder.reset();
    }

//    PDF body

/*
 * A PDF file has 4 parts: a header, a body, a cross-reference table, and a trailer.
 * The body contains all the PDF objects that make up the PDF document.
 * Each element gets a reference (a set of numbers) and the byte position of
 * every object is stored in the cross-reference table.
 * Use these methods only if you know what you're doing.
 */

    /** body of the PDF document */
    protected PdfBody body;

    /**
     * Adds the local destinations to the body of the document.
     * @param dest the <CODE>HashMap</CODE> containing the destinations
     * @throws IOException on error
     */

    void addLocalDestinations(TreeMap dest) throws IOException {
        for (Object o : dest.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String name = (String) entry.getKey();
            Object[] obj = (Object[]) entry.getValue();
            PdfDestination destination = (PdfDestination) obj[2];
            if (obj[1] == null)
                obj[1] = getPdfIndirectReference();
            if (destination == null)
                addToBody(new PdfString("invalid_" + name), (PdfIndirectReference) obj[1]);
            else
                addToBody(destination, (PdfIndirectReference) obj[1]);
        }
    }

    /**
     * Use this method to add a PDF object to the PDF body.
     * Use this method only if you know what you're doing!
     * @param object
     * @return a PdfIndirectObject
     * @throws IOException
     */
    public PdfIndirectObject addToBody(PdfObject object) throws IOException {
        PdfIndirectObject iobj = body.add(object);
        return iobj;
    }

    /**
     * Use this method to add a PDF object to the PDF body.
     * Use this method only if you know what you're doing!
     * @param object
     * @param inObjStm
     * @return a PdfIndirectObject
     * @throws IOException
     */
    public PdfIndirectObject addToBody(PdfObject object, boolean inObjStm) throws IOException {
        PdfIndirectObject iobj = body.add(object, inObjStm);
        return iobj;
    }

    /**
     * Use this method to add a PDF object to the PDF body.
     * Use this method only if you know what you're doing!
     * @param object
     * @param ref
     * @return a PdfIndirectObject
     * @throws IOException
     */
    public PdfIndirectObject addToBody(PdfObject object, PdfIndirectReference ref) throws IOException {
        PdfIndirectObject iobj = body.add(object, ref);
        return iobj;
    }

    /**
     * Use this method to add a PDF object to the PDF body.
     * Use this method only if you know what you're doing!
     * @param object
     * @param ref
     * @param inObjStm
     * @return a PdfIndirectObject
     * @throws IOException
     */
    public PdfIndirectObject addToBody(PdfObject object, PdfIndirectReference ref, boolean inObjStm) throws IOException {
        PdfIndirectObject iobj = body.add(object, ref, inObjStm);
        return iobj;
    }

    /**
     * Use this method to add a PDF object to the PDF body.
     * Use this method only if you know what you're doing!
     * @param object
     * @param refNumber
     * @return a PdfIndirectObject
     * @throws IOException
     */
    public PdfIndirectObject addToBody(PdfObject object, int refNumber) throws IOException {
        PdfIndirectObject iobj = body.add(object, refNumber);
        return iobj;
    }

    /**
     * Use this method to add a PDF object to the PDF body.
     * Use this method only if you know what you're doing!
     * @param object
     * @param refNumber
     * @param inObjStm
     * @return a PdfIndirectObject
     * @throws IOException
     */
    public PdfIndirectObject addToBody(PdfObject object, int refNumber, boolean inObjStm) throws IOException {
        PdfIndirectObject iobj = body.add(object, refNumber, inObjStm);
        return iobj;
    }

    /**
     * Use this to get an <CODE>PdfIndirectReference</CODE> for an object that
     * will be created in the future.
     * Use this method only if you know what you're doing!
     * @return the <CODE>PdfIndirectReference</CODE>
     */

    public PdfIndirectReference getPdfIndirectReference() {
        return body.getPdfIndirectReference();
    }

    int getIndirectReferenceNumber() {
        return body.getIndirectReferenceNumber();
    }

    /**
     * Returns the outputStreamCounter.
     * @return the outputStreamCounter
     */
    OutputStreamCounter getOs() {
        return os;
    }


//    PDF Catalog

/*
 * The Catalog is also called the root object of the document.
 * Whereas the Cross-Reference maps the objects number with the
 * byte offset so that the viewer can find the objects, the
 * Catalog tells the viewer the numbers of the objects needed
 * to render the document.
 */

    protected PdfDictionary getCatalog(PdfIndirectReference rootObj)
    {
        PdfDictionary catalog = pdf.getCatalog(rootObj);
        // [F12] tagged PDF
        if (tagged) {
            try {
                getStructureTreeRoot().buildTree();
            }
            catch (Exception e) {
                throw new ExceptionConverter(e);
            }
            catalog.put(PdfName.STRUCTTREEROOT, structureTreeRoot.getReference());
            PdfDictionary mi = new PdfDictionary();
            mi.put(PdfName.MARKED, PdfBoolean.PDFTRUE);
            catalog.put(PdfName.MARKINFO, mi);
        }
        // [F13] OCG
        if (!documentOCG.isEmpty()) {
            fillOCProperties(false);
            catalog.put(PdfName.OCPROPERTIES, OCProperties);
        }
        return catalog;
    }

    /** Holds value of property extraCatalog this is used for Output Intents. */
    protected PdfDictionary extraCatalog;

    /**
     * Sets extra keys to the catalog.
     * @return the catalog to change
     */
    public PdfDictionary getExtraCatalog() {
        if (extraCatalog == null)
            extraCatalog = new PdfDictionary();
        return this.extraCatalog;
    }

//    PdfPages

/*
 * The page root keeps the complete page tree of the document.
 * There's an entry in the Catalog that refers to the root
 * of the page tree, the page tree contains the references
 * to pages and other page trees.
 */

    /** The root of the page tree. */
    protected PdfPages root = new PdfPages(this);
    /** The PdfIndirectReference to the pages. */
    protected ArrayList<PdfIndirectReference> pageReferences = new ArrayList<>();
    /** The current page number. */
    protected int currentPageNumber = 1;
    /**
     * The value of the Tabs entry in the page dictionary.
     * @since    2.1.5
     */
    protected PdfName tabs = null;

    /**
     * Use this method to get a reference to a page existing or not.
     * If the page does not exist yet the reference will be created
     * in advance. If on closing the document, a page number greater
     * than the total number of pages was requested, an exception
     * is thrown.
     * @param page the page number. The first page is 1
     * @return the reference to the page
     */
    public PdfIndirectReference getPageReference(int page) {
        --page;
        if (page < 0)
            throw new IndexOutOfBoundsException(MessageLocalization.getComposedMessage("the.page.number.must.be.gt.eq.1"));
        PdfIndirectReference ref;
        if (page < pageReferences.size()) {
            ref = pageReferences.get(page);
            if (ref == null) {
                ref = body.getPdfIndirectReference();
                pageReferences.set(page, ref);
            }
        }
        else {
            int empty = page - pageReferences.size();
            for (int k = 0; k < empty; ++k)
                pageReferences.add(null);
            ref = body.getPdfIndirectReference();
            pageReferences.add(ref);
        }
        return ref;
    }

    /**
     * Gets the pagenumber of this document.
     * This number can be different from the real pagenumber,
     * if you have (re)set the page number previously.
     * @return a page number
     */

    public int getPageNumber() {
        return pdf.getPageNumber();
    }

    PdfIndirectReference getCurrentPage() {
        return getPageReference(currentPageNumber);
    }

    public int getCurrentPageNumber() {
        return currentPageNumber;
    }

    /**
     * Returns the value to be used for the Tabs entry in the page tree.
     * @since    2.1.5
     */
    public PdfName getTabs() {
        return tabs;
    }

    /**
     * Adds some <CODE>PdfContents</CODE> to this Writer.
     * <P>
     * The document has to be open before you can begin to add content
     * to the body of the document.
     *
     * @return a <CODE>PdfIndirectReference</CODE>
     * @param page the <CODE>PdfPage</CODE> to add
     * @param contents the <CODE>PdfContents</CODE> of the page
     * @throws PdfException on error
     */

    PdfIndirectReference add(PdfPage page, PdfContents contents) throws PdfException {
        if (!open) {
            throw new PdfException(MessageLocalization.getComposedMessage("the.document.is.not.open"));
        }
        PdfIndirectObject object;
        try {
            object = addToBody(contents);
        }
        catch(IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
        page.add(object.getIndirectReference());
        if (rgbTransparencyBlending) {
            PdfDictionary pp = new PdfDictionary();
            pp.put(PdfName.TYPE, PdfName.GROUP);
            pp.put(PdfName.S, PdfName.TRANSPARENCY);
            pp.put(PdfName.CS, PdfName.DEVICERGB);
            page.put(PdfName.GROUP, pp);
        }
        root.addPage(page);
        currentPageNumber++;
        return null;
    }

//    page events

/*
 * Page events are specific for iText, not for PDF.
 * Upon specific events (for instance when a page starts
 * or ends), the corresponding method in the page event
 * implementation that is added to the writer is invoked.
 */

    /** The <CODE>PdfPageEvent</CODE> for this document. */
    private PdfPageEvent pageEvent;

    /**
     * Sets the <CODE>PdfPageEvent</CODE> for this document.
     * @param event the <CODE>PdfPageEvent</CODE> for this document
     */

    public void setPageEvent(PdfPageEvent event) {
        if (event == null) this.pageEvent = null;
        else if (this.pageEvent == null) this.pageEvent = event;
        else if (this.pageEvent instanceof PdfPageEventForwarder) ((PdfPageEventForwarder)this.pageEvent).addPageEvent(event);
        else {
            PdfPageEventForwarder forward = new PdfPageEventForwarder();
            forward.addPageEvent(this.pageEvent);
            forward.addPageEvent(event);
            this.pageEvent = forward;
        }
    }

    /**
     * Gets the <CODE>PdfPageEvent</CODE> for this document or <CODE>null</CODE>
     * if none is set.
     * @return the <CODE>PdfPageEvent</CODE> for this document or <CODE>null</CODE>
     * if none is set
     */

    public PdfPageEvent getPageEvent() {
        return pageEvent;
    }

//    Open and Close methods + method that create the PDF

    /** A number referring to the previous Cross-Reference Table. */
    protected int prevxref = 0;

    /**
     * Signals that the <CODE>Document</CODE> has been opened and that
     * <CODE>Elements</CODE> can be added.
     * <P>
     * When this method is called, the PDF-document header is
     * written to the outputstream.
     * @see DocWriter#open()
     */
    public void open() {
        super.open();
        try {
            pdf_version.writeHeader(os);
            body = new PdfBody(this);
            if (pdfxConformance.isPdfX32002()) {
                PdfDictionary sec = new PdfDictionary();
                sec.put(PdfName.GAMMA, new PdfArray(new float[]{2.2f,2.2f,2.2f}));
                sec.put(PdfName.MATRIX, new PdfArray(new float[]{0.4124f,0.2126f,0.0193f,0.3576f,0.7152f,0.1192f,0.1805f,0.0722f,0.9505f}));
                sec.put(PdfName.WHITEPOINT, new PdfArray(new float[]{0.9505f,1f,1.089f}));
                PdfArray arr = new PdfArray(PdfName.CALRGB);
                arr.add(sec);
                setDefaultColorspace(PdfName.DEFAULTRGB, addToBody(arr).getIndirectReference());
            }
        }
        catch(IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
    }

    /**
     * Signals that the <CODE>Document</CODE> was closed and that no other
     * <CODE>Elements</CODE> will be added.
     * <P>
     * The pages-tree is built and written to the outputstream.
     * A Catalog is constructed, as well as an Info-object,
     * the reference table is composed and everything is written
     * to the outputstream embedded in a Trailer.
     * @see DocWriter#close()
     */
    @Override
    public void close() {
        if (open) {

            if ((currentPageNumber - 1) != pageReferences.size())
                // 2019-04-26: If you get this error, it could be that you are using OpenPDF or
                // another library such as flying-saucer's ITextRenderer in a non-threadsafe way.
                // ITextRenderer is not thread safe. So if you get this problem here, create a new
                // instance, rather than re-using it.
                // See: https://github.com/LibrePDF/OpenPDF/issues/164
                throw new RuntimeException("The page " + pageReferences.size() +
                " was requested but the document has only " + (currentPageNumber - 1) + " pages.");

            try {
                addSharedObjectsToBody();
                // add the root to the body
                PdfIndirectReference rootRef = root.writePageTree();
                // make the catalog-object and add it to the body
                PdfDictionary catalog = getCatalog(rootRef);
                // [C9] if there is XMP data to add: add it
                if (xmpMetadata != null) {
                    PdfStream xmp = new PdfStream(xmpMetadata);
                    xmp.put(PdfName.TYPE, PdfName.METADATA);
                    xmp.put(PdfName.SUBTYPE, PdfName.XML);
                    catalog.put(PdfName.METADATA, body.add(xmp).getIndirectReference());
                }
                // [C10] make pdfx conformant
                if (isPdfX()) {
                    pdfxConformance.completeInfoDictionary(getInfo());
                    pdfxConformance.completeExtraCatalog(getExtraCatalog());
                }
                // [C11] Output Intents
                if (extraCatalog != null) {
                    catalog.mergeDifferent(extraCatalog);
                }

                // add the Catalog to the body
                PdfIndirectObject indirectCatalog = addToBody(catalog, false);
                // add the info-object to the body
                PdfIndirectObject infoObj = addToBody(getInfo(), false);

                // [F1] encryption
                PdfIndirectReference encryption = null;
                PdfObject fileID = null;
                body.flushObjStm();
                if (getInfo().contains(PdfName.FILEID)) {
                    fileID = getInfo().get(PdfName.FILEID);
                } else {
                    fileID = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
                }

                // write the cross-reference table of the body
                body.writeCrossReferenceTable(os, indirectCatalog.getIndirectReference(),
                    infoObj.getIndirectReference(), encryption,  fileID, prevxref);

                os.write(getISOBytes("startxref\n"));
                os.write(getISOBytes(String.valueOf(body.offset())));
                os.write(getISOBytes("\n%%EOF\n"));
                super.close();
            }
            catch(IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
        }
    }

    protected void addSharedObjectsToBody() throws IOException {
        // [F3] add the fonts
        for (FontDetails details : documentFonts.values()) {
            details.writeFont(this);
        }
        // [F4] add the form XObjects
        for (Object[] objs : formXObjects.values()) {
            PdfTemplate template = (PdfTemplate) objs[1];
            if (template != null && template.getIndirectReference() instanceof PRIndirectReference)
                continue;
            if (template != null && template.getType() == PdfTemplate.TYPE_TEMPLATE) {
                addToBody(template.getFormXObject(compressionLevel), template.getIndirectReference());
            }
        }
        // [F5] add all the dependencies in the imported pages
        for (PdfReaderInstance pdfReaderInstance : importedPages.values()) {
            currentPdfReaderInstance = pdfReaderInstance;
            currentPdfReaderInstance.writeAllPages();
        }
        currentPdfReaderInstance = null;
        // [F6] add the spotcolors
        for (ColorDetails color : documentColors.values()) {
            addToBody(color.getSpotColor(this), color.getIndirectReference());
        }
        // [F7] add the pattern
        for (PdfPatternPainter pat : documentPatterns.keySet()) {
            addToBody(pat.getPattern(compressionLevel), pat.getIndirectReference());
        }
        // [F8] add the shading patterns
        for (PdfShadingPattern shadingPattern : documentShadingPatterns.keySet()) {
            shadingPattern.addToBody();
        }
        // [F9] add the shadings
        for (PdfShading shading : documentShadings.keySet()) {
            shading.addToBody();
        }
        // [F10] add the extgstate
        for (Map.Entry<PdfDictionary, PdfObject[]> entry : documentExtGState.entrySet()) {
            PdfDictionary gstate = entry.getKey();
            PdfObject[] obj = entry.getValue();
            addToBody(gstate, (PdfIndirectReference) obj[1]);
        }
        // [F11] add the properties
        for (Map.Entry<Object, PdfObject[]> entry : documentProperties.entrySet()) {
            Object prop = entry.getKey();
            PdfObject[] obj = entry.getValue();
            if (prop instanceof PdfLayerMembership) {
                PdfLayerMembership layer = (PdfLayerMembership) prop;
                addToBody(layer.getPdfObject(), layer.getRef());
            } else if ((prop instanceof PdfDictionary) && !(prop instanceof PdfLayer)) {
                addToBody((PdfDictionary) prop, (PdfIndirectReference) obj[1]);
            }
        }
        // [F13] add the OCG layers
        for (PdfOCG layer : documentOCG) {
            addToBody(layer.getPdfObject(), layer.getRef());
        }
    }

// Root data for the PDF document (used when composing the Catalog)

//  [C1] Outlines (bookmarks)

    //    [C2] PdfVersion interface
     /** possible PDF version (header) */
     public static final char VERSION_1_2 = '2';
     /** possible PDF version (header) */
     public static final char VERSION_1_3 = '3';
     /** possible PDF version (header) */
     public static final char VERSION_1_4 = '4';
     /** possible PDF version (header) */
     public static final char VERSION_1_5 = '5';
     /** possible PDF version (header) */
     public static final char VERSION_1_6 = '6';
     /** possible PDF version (header) */
     public static final char VERSION_1_7 = '7';

     /** possible PDF version (catalog) */
     public static final PdfName PDF_VERSION_1_2 = new PdfName("1.2");
     /** possible PDF version (catalog) */
     public static final PdfName PDF_VERSION_1_3 = new PdfName("1.3");
     /** possible PDF version (catalog) */
     public static final PdfName PDF_VERSION_1_4 = new PdfName("1.4");
     /** possible PDF version (catalog) */
     public static final PdfName PDF_VERSION_1_5 = new PdfName("1.5");
     /** possible PDF version (catalog) */
     public static final PdfName PDF_VERSION_1_6 = new PdfName("1.6");
     /** possible PDF version (catalog) */
     public static final PdfName PDF_VERSION_1_7 = new PdfName("1.7");

    /** Stores the version information for the header and the catalog. */
    protected PdfVersionImp pdf_version = new PdfVersionImp();

    public void setPdfVersion(char version) {
        pdf_version.setPdfVersion(version);
    }

    public void setAtLeastPdfVersion(char version) {
        pdf_version.setAtLeastPdfVersion(version);
    }

    public void setPdfVersion(PdfName version) {
        pdf_version.setPdfVersion(version);
    }

    /**
     * @since    2.1.6
     */
    public void addDeveloperExtension(PdfDeveloperExtension de) {
        pdf_version.addDeveloperExtension(de);
    }
    
    /**
     * Returns the version information.
     */
    PdfVersionImp getPdfVersion() {
        return pdf_version;
    }



    public void setOpenAction(String name) {
         pdf.setOpenAction(name);
     }

//  [C7] portable collections

    /**
     * Use this method to add the Collection dictionary.
     * @param collection a dictionary of type PdfCollection
     */
    public void setCollection(PdfCollection collection) {
        setAtLeastPdfVersion(VERSION_1_7);
        pdf.setCollection(collection);
    }



//  [C9] Metadata

    /** XMP Metadata for the document. */
    protected byte[] xmpMetadata = null;

    //  [C10] PDFX Conformance
    /** A PDF/X level. */
    public static final int PDFXNONE = 0;
    /** A PDF/X level. */
    public static final int PDFX1A2001 = 1;
    /** A PDF/X level. */
    public static final int PDFX32002 = 2;
    /** PDFA-1A level. */
    public static final int PDFA1A = 3;
    /** PDFA-1B level. */
    public static final int PDFA1B = 4;

    /** Stores the PDF/X level. */
    private PdfXConformanceImp pdfxConformance = new PdfXConformanceImp();

    public void setPDFXConformance(int pdfx) {
        if (pdfxConformance.getPDFXConformance() == pdfx)
            return;
        if (pdf.isOpen())
            throw new PdfXConformanceException(MessageLocalization.getComposedMessage("pdfx.conformance.can.only.be.set.before.opening.the.document"));
        if (pdfx == PDFA1A || pdfx == PDFA1B)
            setPdfVersion(VERSION_1_4);
        else if (pdfx != PDFXNONE)
            setPdfVersion(VERSION_1_3);
        pdfxConformance.setPDFXConformance(pdfx);
    }

    public int getPDFXConformance() {
        return pdfxConformance.getPDFXConformance();
    }

    public boolean isPdfX() {
        return pdfxConformance.isPdfX();
    }



    //  [F2] compression

    /** Holds value of property fullCompression. */
    protected boolean fullCompression = false;

    /**
     * Use this method to find out if 1.5 compression is on.
     * @return the 1.5 compression status
     */
    public boolean isFullCompression() {
        return this.fullCompression;
    }

    /**
     * The compression level of the content streams.
     * @since 2.1.3
     */
    protected int compressionLevel = PdfStream.DEFAULT_COMPRESSION;

    /**
     * Returns the compression level used for streams written by this writer.
     * @return the compression level (0 = best speed, 9 = best compression, -1 is default)
     * @since 2.1.3
     */
    public int getCompressionLevel() {
        return compressionLevel;
    }

    /**
     * Sets the compression level to be used for streams written by this writer.
     * @param compressionLevel a value between 0 (best speed) and 9 (best compression)
     * @since 2.1.3
     */
    public void setCompressionLevel(int compressionLevel) {
        if (compressionLevel < PdfStream.NO_COMPRESSION || compressionLevel > PdfStream.BEST_COMPRESSION)
            this.compressionLevel = PdfStream.DEFAULT_COMPRESSION;
        else
            this.compressionLevel = compressionLevel;
    }

//  [F3] adding fonts

    /** The fonts of this document */
    protected LinkedHashMap<BaseFont, FontDetails> documentFonts = new LinkedHashMap<>();

    /** The font number counter for the fonts in the document. */
    protected int fontNumber = 1;

    /**
     * Adds a <CODE>BaseFont</CODE> to the document but not to the page resources.
     * It is used for templates.
     * @param bf the <CODE>BaseFont</CODE> to add
     * @return an <CODE>Object[]</CODE> where position 0 is a <CODE>PdfName</CODE>
     * and position 1 is an <CODE>PdfIndirectReference</CODE>
     */

    FontDetails addSimple(BaseFont bf) {
        if (bf.getFontType() == BaseFont.FONT_TYPE_DOCUMENT) {
            return new FontDetails(new PdfName("F" + (fontNumber++)), ((DocumentFont)bf).getIndirectReference(), bf);
        }
        FontDetails ret = documentFonts.get(bf);
        if (ret == null) {
            PdfXConformanceImp.checkPDFXConformance(this, PdfXConformanceImp.PDFXKEY_FONT, bf);
            ret = new FontDetails(new PdfName("F" + (fontNumber++)), body.getPdfIndirectReference(), bf);
            documentFonts.put(bf, ret);
        }
        return ret;
    }

    //  [F4] adding (and releasing) form XObjects

    /** The form XObjects in this document. The key is the xref and the value
        is Object[]{PdfName, template}.*/
    protected LinkedHashMap<PdfIndirectReference, Object[]> formXObjects = new LinkedHashMap<>();

    /** The name counter for the form XObjects name. */
    protected int formXObjectsCounter = 1;

    /**
     * Adds a template to the document but not to the page resources.
     * @param template the template to add
     * @param forcedName the template name, rather than a generated one. Can be null
     * @return the <CODE>PdfName</CODE> for this template
     */

    PdfName addDirectTemplateSimple(PdfTemplate template, PdfName forcedName) {
        PdfIndirectReference ref = template.getIndirectReference();
        Object[] obj = formXObjects.get(ref);
        PdfName name = null;
        try {
            if (obj == null) {
                if (forcedName == null) {
                    name = new PdfName("Xf" + formXObjectsCounter);
                    ++formXObjectsCounter;
                }
                else
                    name = forcedName;
                if (template.getType() == PdfTemplate.TYPE_IMPORTED) {
                    // If we got here from PdfCopy we'll have to fill importedPages
                    PdfImportedPage ip = (PdfImportedPage)template;
                    PdfReader r = ip.getPdfReaderInstance().getReader();
                    if (!importedPages.containsKey(r)) {
                        importedPages.put(r, ip.getPdfReaderInstance());
                    }
                    template = null;
                }
                formXObjects.put(ref, new Object[]{name, template});
            }
            else
                name = (PdfName)obj[0];
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        return name;
    }

    //  [F5] adding pages imported form other PDF documents

    protected HashMap<PdfReader, PdfReaderInstance> importedPages = new HashMap<>();

    protected PdfReaderInstance currentPdfReaderInstance;

    protected int getNewObjectNumber(PdfReader reader, int number, int generation) {
        if (currentPdfReaderInstance == null && importedPages.get(reader) == null) {
            importedPages.put(reader, reader.getPdfReaderInstance(this));
        }
        currentPdfReaderInstance = importedPages.get(reader);
        int n = currentPdfReaderInstance.getNewObjectNumber(number, generation);
        currentPdfReaderInstance = null;
        return n;
    }

    //  [F6] spot colors

    /** The colors of this document */
    protected HashMap<PdfSpotColor, ColorDetails> documentColors = new HashMap<>();

    /** The color number counter for the colors in the document. */
    protected int colorNumber = 1;

    PdfName getColorspaceName() {
        return new PdfName("CS" + (colorNumber++));
    }

    /**
     * Adds a <CODE>SpotColor</CODE> to the document but not to the page resources.
     * @param spc the <CODE>SpotColor</CODE> to add
     * @return an <CODE>Object[]</CODE> where position 0 is a <CODE>PdfName</CODE>
     * and position 1 is an <CODE>PdfIndirectReference</CODE>
     */
    ColorDetails addSimple(PdfSpotColor spc) {
        ColorDetails ret = documentColors.get(spc);
        if (ret == null) {
            ret = new ColorDetails(getColorspaceName(), body.getPdfIndirectReference(), spc);
            documentColors.put(spc, ret);
        }
        return ret;
    }

//  [F7] document patterns

    /** The patterns of this document */
    protected HashMap<PdfPatternPainter, PdfName> documentPatterns = new HashMap<>();

    /** The pattern number counter for the colors in the document. */
    protected int patternNumber = 1;

    PdfName addSimplePattern(PdfPatternPainter painter) {
        PdfName name = documentPatterns.get(painter);
        try {
            if ( name == null ) {
                name = new PdfName("P" + patternNumber);
                ++patternNumber;
                documentPatterns.put(painter, name);
            }
        } catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        return name;
    }

//  [F8] shading patterns

    protected HashMap<PdfShadingPattern, Object> documentShadingPatterns = new HashMap<>();

    void addSimpleShadingPattern(PdfShadingPattern shading) {
        if (!documentShadingPatterns.containsKey(shading)) {
            shading.setName(patternNumber);
            ++patternNumber;
            documentShadingPatterns.put(shading, null);
            addSimpleShading(shading.getShading());
        }
    }

//  [F9] document shadings

    protected HashMap<PdfShading, Object> documentShadings = new HashMap<>();

    void addSimpleShading(PdfShading shading) {
        if (!documentShadings.containsKey(shading)) {
            documentShadings.put(shading, null);
            shading.setName(documentShadings.size());
        }
    }

// [F10] extended graphics state (for instance for transparency)

    protected HashMap<PdfDictionary, PdfObject[]> documentExtGState = new LinkedHashMap<>();

    PdfObject[] addSimpleExtGState(PdfDictionary gstate) {
        if (!documentExtGState.containsKey(gstate)) {
            PdfXConformanceImp.checkPDFXConformance(this, PdfXConformanceImp.PDFXKEY_GSTATE, gstate);
            documentExtGState.put(gstate, new PdfObject[]{new PdfName("GS" + (documentExtGState.size() + 1)), getPdfIndirectReference()});
        }
        return documentExtGState.get(gstate);
    }

//  [F11] adding properties (OCG, marked content)

    protected HashMap<Object, PdfObject[]> documentProperties = new HashMap<>();

    PdfObject[] addSimpleProperty(Object prop, PdfIndirectReference refi) {
        if (!documentProperties.containsKey(prop)) {
            if (prop instanceof PdfOCG)
                PdfXConformanceImp.checkPDFXConformance(this, PdfXConformanceImp.PDFXKEY_LAYER, null);
            documentProperties.put(prop, new PdfObject[]{new PdfName("Pr" + (documentProperties.size() + 1)), refi});
        }
        return documentProperties.get(prop);
    }

    boolean propertyExists(Object prop) {
        return documentProperties.containsKey(prop);
    }

//  [F12] tagged PDF

    protected boolean tagged = false;
    protected PdfStructureTreeRoot structureTreeRoot;

    /**
     * Check if the document is marked for tagging.
     * @return <CODE>true</CODE> if the document is marked for tagging
     */
    public boolean isTagged() {
        return tagged;
    }

    /**
     * Gets the structure tree root. If the document is not marked for tagging it will return <CODE>null</CODE>.
     * @return the structure tree root
     */
    public PdfStructureTreeRoot getStructureTreeRoot() {
        if (tagged && structureTreeRoot == null)
            structureTreeRoot = new PdfStructureTreeRoot(this);
        return structureTreeRoot;
    }

//  [F13] Optional Content Groups
    /** A hashSet containing all the PdfLayer objects. */
    protected Set<PdfOCG> documentOCG = new HashSet<>();
    /** An array list used to define the order of an OCG tree. */
    protected List<PdfOCG> documentOCGorder = new ArrayList<>();
    /** The OCProperties in a catalog dictionary. */
    protected PdfOCProperties OCProperties;
    /** The RBGroups array in an OCG dictionary */
    protected PdfArray OCGRadioGroup = new PdfArray();
    /**
     * The locked array in an OCG dictionary
     * @since   2.1.2
     */
    protected PdfArray OCGLocked = new PdfArray();

    private static void getOCGOrder(PdfArray order, PdfLayer layer) {
        if (!layer.isOnPanel())
            return;
        if (layer.getTitle() == null)
            order.add(layer.getRef());
        ArrayList<PdfLayer> children = layer.getChildren();
        if (children == null)
            return;
        PdfArray kids = new PdfArray();
        if (layer.getTitle() != null)
            kids.add(new PdfString(layer.getTitle(), PdfObject.TEXT_UNICODE));
        for (PdfLayer child : children) {
            getOCGOrder(kids, child);
        }
        if (kids.size() > 0)
            order.add(kids);
    }

    private void addASEvent(PdfName event, PdfName category) {
        PdfArray arr = new PdfArray();
        for (PdfOCG o : documentOCG) {
            PdfLayer layer = (PdfLayer) o;
            PdfDictionary usage = (PdfDictionary) layer.get(PdfName.USAGE);
            if (usage != null && usage.get(category) != null)
                arr.add(layer.getRef());
        }
        if (arr.size() == 0)
            return;
        PdfDictionary d = (PdfDictionary)OCProperties.get(PdfName.D);
        PdfArray arras = (PdfArray)d.get(PdfName.AS);
        if (arras == null) {
            arras = new PdfArray();
            d.put(PdfName.AS, arras);
        }
        PdfDictionary as = new PdfDictionary();
        as.put(PdfName.EVENT, event);
        as.put(PdfName.CATEGORY, new PdfArray(category));
        as.put(PdfName.OCGS, arr);
        arras.add(as);
    }

    /**
     * @since 2.1.2
     */
    protected void fillOCProperties(boolean erase) {
        if (OCProperties == null)
            OCProperties = new PdfOCProperties();
        if (erase) {
            OCProperties.remove(PdfName.OCGS);
            OCProperties.remove(PdfName.D);
        }
        if (OCProperties.get(PdfName.OCGS) == null) {
            PdfArray gr = new PdfArray();
            for (PdfOCG o : documentOCG) {
                PdfLayer layer = (PdfLayer) o;
                gr.add(layer.getRef());
            }
            OCProperties.put(PdfName.OCGS, gr);
        }
        if (OCProperties.get(PdfName.D) != null)
            return;

        List<PdfOCG> docOrder = documentOCGorder.stream()
                .filter(pdfOCG -> ((PdfLayer)pdfOCG).getParent() == null)
                .collect(Collectors.toList());

        PdfArray order = new PdfArray();
        for (PdfOCG o1 : docOrder) {
            PdfLayer layer = (PdfLayer) o1;
            getOCGOrder(order, layer);
        }
        PdfDictionary d = new PdfDictionary();
        OCProperties.put(PdfName.D, d);
        d.put(PdfName.ORDER, order);
        PdfArray gr = new PdfArray();
        for (PdfOCG o : documentOCG) {
            PdfLayer layer = (PdfLayer) o;
            if (!layer.isOn())
                gr.add(layer.getRef());
        }
        if (gr.size() > 0)
            d.put(PdfName.OFF, gr);
        if (OCGRadioGroup.size() > 0)
            d.put(PdfName.RBGROUPS, OCGRadioGroup);
        if (OCGLocked.size() > 0)
            d.put(PdfName.LOCKED, OCGLocked);
        addASEvent(PdfName.VIEW, PdfName.ZOOM);
        addASEvent(PdfName.VIEW, PdfName.VIEW);
        addASEvent(PdfName.PRINT, PdfName.PRINT);
        addASEvent(PdfName.EXPORT, PdfName.EXPORT);
        d.put(PdfName.LISTMODE, PdfName.VISIBLEPAGES);
    }

    void registerLayer(PdfOCG layer) {
        PdfXConformanceImp.checkPDFXConformance(this, PdfXConformanceImp.PDFXKEY_LAYER, null);
        if (layer instanceof PdfLayer) {
            PdfLayer la = (PdfLayer)layer;
            if (la.getTitle() == null) {
                if (!documentOCG.contains(layer)) {
                    documentOCG.add(layer);
                    documentOCGorder.add(layer);
                }
            }
            else {
                documentOCGorder.add(layer);
            }
        }
        else
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("only.pdflayer.is.accepted"));
    }

//  [U6] space char ratio

    /** The default space-char ratio. */
    public static final float SPACE_CHAR_RATIO_DEFAULT = 2.5f;
    /** Disable the inter-character spacing. */
    public static final float NO_SPACE_CHAR_RATIO = 10000000f;

    /**
     * Use this method to gets the space/character extra spacing ratio
     * for fully justified text.
     * @return the space/character extra spacing ratio
     */
    public float getSpaceCharRatio() {
        /**
         * The ratio between the extra word spacing and the extra character spacing.
         * Extra word spacing will grow <CODE>ratio</CODE> times more than extra character spacing.
         */
        return SPACE_CHAR_RATIO_DEFAULT;
    }

    //  [U7] run direction (doesn't actually do anything)

    /** Use the default run direction. */
    public static final int RUN_DIRECTION_DEFAULT = 0;
    /** Do not use bidirectional reordering. */
    public static final int RUN_DIRECTION_NO_BIDI = 1;

    /** Use bidirectional reordering with left-to-right
     * preferential run direction.
     */
    public static final int RUN_DIRECTION_LTR = 2;
    /** Use bidirectional reordering with right-to-left
     * preferential run direction.
     */
    public static final int RUN_DIRECTION_RTL = 3;

//  [U8] user units

     protected float userunit = 0f;
    /**
     * Use this method to get the user unit.
     * A user unit is a value that defines the default user space unit.
     * The minimum UserUnit is 1 (1 unit = 1/72 inch).
     * The maximum UserUnit is 75,000.
     * Note that this userunit only works starting with PDF1.6!
     * @return Returns the userunit.
     */
    public float getUserunit() {
        return userunit;
    }

// Miscellaneous topics

//  [M1] Color settings

    protected PdfDictionary defaultColorspace = new PdfDictionary();
    /**
     * Use this method to get the default colorspaces.
     * @return the default colorspaces
     */
    public PdfDictionary getDefaultColorspace() {
        return defaultColorspace;
    }

    /**
     * Use this method to sets the default colorspace that will be applied
     * to all the document. The colorspace is only applied if another colorspace
     * with the same name is not present in the content.
     * <p>
     * The colorspace is applied immediately when creating templates and
     * at the page end for the main document content.
     * @param key the name of the colorspace. It can be <CODE>PdfName.DEFAULTGRAY</CODE>, <CODE>PdfName.DEFAULTRGB</CODE>
     * or <CODE>PdfName.DEFAULTCMYK</CODE>
     * @param cs the colorspace. A <CODE>null</CODE> or <CODE>PdfNull</CODE> removes any colorspace with the same name
     */
    public void setDefaultColorspace(PdfName key, PdfObject cs) {
        if (cs == null || cs.isNull())
            defaultColorspace.remove(key);
        defaultColorspace.put(key, cs);
    }

//  [M2] spot patterns

    protected HashMap<ColorDetails, ColorDetails> documentSpotPatterns = new HashMap<>();
    protected ColorDetails patternColorspaceRGB;
    protected ColorDetails patternColorspaceGRAY;
    protected ColorDetails patternColorspaceCMYK;

    ColorDetails addSimplePatternColorspace(Color color) {
        int type = ExtendedColor.getType(color);
        if (type == ExtendedColor.TYPE_PATTERN || type == ExtendedColor.TYPE_SHADING)
            throw new RuntimeException(MessageLocalization.getComposedMessage("an.uncolored.tile.pattern.can.not.have.another.pattern.or.shading.as.color"));
        try {
            switch (type) {
                case ExtendedColor.TYPE_RGB:
                    if (patternColorspaceRGB == null) {
                        patternColorspaceRGB = new ColorDetails(getColorspaceName(), body.getPdfIndirectReference(), null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(PdfName.DEVICERGB);
                        addToBody(array, patternColorspaceRGB.getIndirectReference());
                    }
                    return patternColorspaceRGB;
                case ExtendedColor.TYPE_CMYK:
                    if (patternColorspaceCMYK == null) {
                        patternColorspaceCMYK = new ColorDetails(getColorspaceName(), body.getPdfIndirectReference(), null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(PdfName.DEVICECMYK);
                        addToBody(array, patternColorspaceCMYK.getIndirectReference());
                    }
                    return patternColorspaceCMYK;
                case ExtendedColor.TYPE_GRAY:
                    if (patternColorspaceGRAY == null) {
                        patternColorspaceGRAY = new ColorDetails(getColorspaceName(), body.getPdfIndirectReference(), null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(PdfName.DEVICEGRAY);
                        addToBody(array, patternColorspaceGRAY.getIndirectReference());
                    }
                    return patternColorspaceGRAY;
                case ExtendedColor.TYPE_SEPARATION: {
                    ColorDetails details = addSimple(((SpotColor)color).getPdfSpotColor());
                    ColorDetails patternDetails = documentSpotPatterns.get(details);
                    if (patternDetails == null) {
                        patternDetails = new ColorDetails(getColorspaceName(), body.getPdfIndirectReference(), null);
                        PdfArray array = new PdfArray(PdfName.PATTERN);
                        array.add(details.getIndirectReference());
                        addToBody(array, patternDetails.getIndirectReference());
                        documentSpotPatterns.put(details, patternDetails);
                    }
                    return patternDetails;
                }
                default:
                    throw new RuntimeException(MessageLocalization.getComposedMessage("invalid.color.type"));
            }
        }
        catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

//  [M3] Images

    /** Dictionary, containing all the images of the PDF document */
    protected PdfDictionary imageDictionary = new PdfDictionary();

    /** This is the list with all the images in the document. */
    private HashMap<Long, PdfName> images = new HashMap<>();

    /**
     * Use this method to adds an image to the document
     * but not to the page resources. It is used with
     * templates and <CODE>Document.add(Image)</CODE>.
     * Use this method only if you know what you're doing!
     * @param image the <CODE>Image</CODE> to add
     * @return the name of the image added
     * @throws PdfException on error
     * @throws DocumentException on error
     */
    public PdfName addDirectImageSimple(Image image) throws DocumentException {
        return addDirectImageSimple(image, null);
    }

    /**
     * Adds an image to the document but not to the page resources.
     * It is used with templates and <CODE>Document.add(Image)</CODE>.
     * Use this method only if you know what you're doing!
     * @param image the <CODE>Image</CODE> to add
     * @param fixedRef the reference to used. It may be <CODE>null</CODE>,
     * a <CODE>PdfIndirectReference</CODE> or a <CODE>PRIndirectReference</CODE>.
     * @return the name of the image added
     * @throws PdfException on error
     * @throws DocumentException on error
     */
    public PdfName addDirectImageSimple(Image image, PdfIndirectReference fixedRef) throws DocumentException {
        PdfName name;
        // if the images is already added, just retrieve the name
        if (images.containsKey(image.getMySerialId())) {
            name = images.get(image.getMySerialId());
        }
        // if it's a new image, add it to the document
        else {
            if (image.isImgTemplate()) {
                name = new PdfName("img" + images.size());
            }
            else {
                PdfIndirectReference dref = image.getDirectReference();
                if (dref != null) {
                    PdfName rname = new PdfName("img" + images.size());
                    images.put(image.getMySerialId(), rname);
                    imageDictionary.put(rname, dref);
                    return rname;
                }
                Image maskImage = image.getImageMask();
                PdfIndirectReference maskRef = null;
                if (maskImage != null) {
                    PdfName mname = images.get(maskImage.getMySerialId());
                    maskRef = getImageReference(mname);
                }
                PdfImage i = new PdfImage(image, "img" + images.size(), maskRef);
                if (image.hasICCProfile()) {
                    PdfICCBased icc = new PdfICCBased(image.getICCProfile(), image.getCompressionLevel());
                    PdfIndirectReference iccRef = add(icc);
                    PdfArray iccArray = new PdfArray();
                    iccArray.add(PdfName.ICCBASED);
                    iccArray.add(iccRef);
                    PdfArray colorspace = i.getAsArray(PdfName.COLORSPACE);
                    if (colorspace != null) {
                        if (colorspace.size() > 1 && PdfName.INDEXED.equals(colorspace.getPdfObject(0)))
                            colorspace.set(1, iccArray);
                        else
                            i.put(PdfName.COLORSPACE, iccArray);
                    }
                    else
                        i.put(PdfName.COLORSPACE, iccArray);
                }
                add(i, fixedRef);
                name = i.name();
            }
            images.put(image.getMySerialId(), name);
        }
        return name;
    }

    /**
     * Writes a <CODE>PdfImage</CODE> to the outputstream.
     *
     * @param pdfImage the image to be added
     * @return a <CODE>PdfIndirectReference</CODE> to the encapsulated image
     * @throws PdfException when a document isn't open yet, or has been closed
     */

    PdfIndirectReference add(PdfImage pdfImage, PdfIndirectReference fixedRef) throws PdfException {
        if (! imageDictionary.contains(pdfImage.name())) {
            PdfXConformanceImp.checkPDFXConformance(this, PdfXConformanceImp.PDFXKEY_IMAGE, pdfImage);
            if (fixedRef instanceof PRIndirectReference) {
                PRIndirectReference r2 = (PRIndirectReference)fixedRef;
                fixedRef = new PdfIndirectReference(0, getNewObjectNumber(r2.getReader(), r2.getNumber(), r2.getGeneration()));
            }
            try {
                if (fixedRef == null)
                    fixedRef = addToBody(pdfImage).getIndirectReference();
                else
                    addToBody(pdfImage, fixedRef);
            }
            catch(IOException ioe) {
                throw new ExceptionConverter(ioe);
            }
            imageDictionary.put(pdfImage.name(), fixedRef);
            return fixedRef;
        }
        return (PdfIndirectReference) imageDictionary.get(pdfImage.name());
    }

    /**
     * return the <CODE>PdfIndirectReference</CODE> to the image with a given name.
     *
     * @param name the name of the image
     * @return a <CODE>PdfIndirectReference</CODE>
     */

    PdfIndirectReference getImageReference(PdfName name) {
        return (PdfIndirectReference) imageDictionary.get(name);
    }

    protected PdfIndirectReference add(PdfICCBased icc) {
        PdfIndirectObject object;
        try {
            object = addToBody(icc);
        }
        catch(IOException ioe) {
            throw new ExceptionConverter(ioe);
        }
        return object.getIndirectReference();
    }

    //  [M4] Old table functionality; do we still need it?

    //  [F12] tagged PDF

    /**
     * Holds value of property RGBTranparency.
     */
    private boolean rgbTransparencyBlending;

    /**
     * Gets the transparency blending colorspace.
     * @return <code>true</code> if the transparency blending colorspace is RGB, <code>false</code>
     * if it is the default blending colorspace
     * @since 2.1.0
     */
    public boolean isRgbTransparencyBlending() {
        return this.rgbTransparencyBlending;
    }

}
