/*
 * Copyright 2003 by Paulo Soares.
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

import com.justifiedsolutions.openpdf.text.Document;
import com.justifiedsolutions.openpdf.text.Rectangle;
import com.justifiedsolutions.openpdf.text.error_messages.MessageLocalization;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

class PdfStamperImp extends PdfWriter {
    HashMap<PdfReader, IntHashtable> readers2intrefs = new HashMap<>();
    HashMap<PdfReader, RandomAccessFileOrArray> readers2file = new HashMap<>();
    RandomAccessFileOrArray file;
    PdfReader reader;
    IntHashtable myXref = new IntHashtable();
    /** Integer(page number) -> PageStamp */
    HashMap<PdfDictionary, PageStamp> pagesToContent = new HashMap<>();
    boolean closed = false;
    /** Holds value of property rotateContents. */
    private boolean rotateContents = true;
    protected int[] namePtr = {0};
    protected Set<String> partialFlattening = new HashSet<>();
    protected boolean useVp = false;
    protected Map<PdfTemplate, Object> fieldTemplates = new HashMap<>();
    protected boolean fieldsAdded = false;
    protected int sigFlags = 0;
    protected boolean append;
    protected IntHashtable marked;
    protected int initialXrefSize;
    private boolean includeFileID = true;
    private PdfObject overrideFileId = null;
    private Calendar modificationDate = null;

    void close(Map<String, String> moreInfo) throws IOException {
        if (closed)
            return;
        if (useVp) {
            markUsed(reader.getTrailer().get(PdfName.ROOT));
        }
        PdfDictionary catalog = reader.getCatalog();
        PdfDictionary pages = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.PAGES));
        pages.put(PdfName.ITXT, new PdfString(Document.getRelease()));
        markUsed(pages);
        closed = true;
        addSharedObjectsToBody();
        addFileAttachments();
        // OCG
        if (!documentOCG.isEmpty()) {
            fillOCProperties();
            PdfDictionary ocdict = catalog.getAsDict(PdfName.OCPROPERTIES);
            if (ocdict == null) {
                reader.getCatalog().put(PdfName.OCPROPERTIES, OCProperties);
            }
            else {
                ocdict.put(PdfName.OCGS, OCProperties.get(PdfName.OCGS));
                PdfDictionary ddict = ocdict.getAsDict(PdfName.D);
                if (ddict == null) {
                    ddict = new PdfDictionary();
                    ocdict.put(PdfName.D, ddict);
                }
                ddict.put(PdfName.ORDER, OCProperties.getAsDict(PdfName.D).get(PdfName.ORDER));
                ddict.put(PdfName.RBGROUPS, OCProperties.getAsDict(PdfName.D).get(PdfName.RBGROUPS));
                ddict.put(PdfName.OFF, OCProperties.getAsDict(PdfName.D).get(PdfName.OFF));
                ddict.put(PdfName.AS, OCProperties.getAsDict(PdfName.D).get(PdfName.AS));
            }
        }
        // metadata
        int skipInfo = -1;
        PRIndirectReference iInfo = (PRIndirectReference)reader.getTrailer().get(PdfName.INFO);
      
        PdfDictionary oldInfo = (PdfDictionary)PdfReader.getPdfObject(iInfo);
        String producer = null;
        if (iInfo != null) {
          skipInfo = iInfo.getNumber();
        }
        if (oldInfo != null && oldInfo.get(PdfName.PRODUCER) != null) {
          producer = oldInfo.getAsString(PdfName.PRODUCER).toUnicodeString();
        }

        // if we explicitly set Producer key
        if (moreInfo != null && moreInfo.containsKey("Producer")) {
          producer = moreInfo.get("Producer");
        }
          
        // XMP
        byte[] altMetadata = null;
        PdfObject xmpo = PdfReader.getPdfObject(catalog.get(PdfName.METADATA));
        if (xmpo != null && xmpo.isStream()) {
          altMetadata = PdfReader.getStreamBytesRaw((PRStream)xmpo);
          PdfReader.killIndirect(catalog.get(PdfName.METADATA));
        }
        PdfDate date = new PdfDate();

        try {
            file.reOpen();
            alterContents();
            int rootN = ((PRIndirectReference)reader.trailer.get(PdfName.ROOT)).getNumber();
            if (append) {
                int[] keys = marked.getKeys();
                for (int j : keys) {
                    PdfObject obj = reader.getPdfObjectRelease(j);
                    if (obj != null && skipInfo != j && j < initialXrefSize) {
                        addToBody(obj, j, j != rootN);
                    }
                }
                for (int k = initialXrefSize; k < reader.getXrefSize(); ++k) {
                    PdfObject obj = reader.getPdfObject(k);
                    if (obj != null) {
                        addToBody(obj, getNewObjectNumber(reader, k, 0));
                    }
                }
            }
            else {
                for (int k = 1; k < reader.getXrefSize(); ++k) {
                    PdfObject obj = reader.getPdfObjectRelease(k);
                    if (obj != null && skipInfo != k) {
                        addToBody(obj, getNewObjectNumber(reader, k, 0), k != rootN);
                    }
                }
            }
        }
        finally {
            try {
                file.close();
            }
            catch (Exception e) {
                // empty on purpose
            }
        }
        PdfObject fileID = null;
        if (includeFileID) {
            if (overrideFileId != null) {
                fileID = overrideFileId;
            } else {
                fileID = PdfEncryption.createInfoId(PdfEncryption.createDocumentId());
            }

        }
        PRIndirectReference iRoot = (PRIndirectReference)reader.trailer.get(PdfName.ROOT);
        PdfIndirectReference root = new PdfIndirectReference(0, getNewObjectNumber(reader, iRoot.getNumber(), 0));
        PdfIndirectReference info = null;
        PdfDictionary newInfo = new PdfDictionary();
        if (oldInfo != null) {
          for (PdfName key : oldInfo.getKeys()) {
            PdfObject value = PdfReader.getPdfObject(oldInfo.get(key));
            newInfo.put(key, value);
          }
        }
        
        newInfo.put(PdfName.MODDATE, date);
        if (producer != null) {
          newInfo.put(PdfName.PRODUCER, new PdfString(producer));
        }
        
        if (moreInfo != null) {
            for (Map.Entry<String, String> entry : moreInfo.entrySet()) {
                String key = entry.getKey();
                PdfName keyName = new PdfName(key);
                String value = entry.getValue();
                if (value == null) {
                    newInfo.remove(keyName);
                }
                else {
                    newInfo.put(keyName, new PdfString(value, PdfObject.TEXT_UNICODE));
                }
            }
        }


        if (append) {
            if (iInfo == null)
                info = addToBody(newInfo, false).getIndirectReference();
            else
                info = addToBody(newInfo, iInfo.getNumber(), false).getIndirectReference();
        }
        else {
            info = addToBody(newInfo, false).getIndirectReference();
        }
        // write the cross-reference table of the body
        body.writeCrossReferenceTable(os, root, info, fileID, prevxref);
        os.write(getISOBytes("startxref\n"));
        os.write(getISOBytes(String.valueOf(body.offset())));
        os.write(getISOBytes("\n%%EOF\n"));
        os.flush();
        if (isCloseStream())
            os.close();
        reader.close();
    }

    void applyRotation(PdfDictionary pageN, ByteBuffer out) {
        if (!rotateContents)
            return;
        Rectangle page = reader.getPageSizeWithRotation(pageN);
        int rotation = page.getRotation();
        switch (rotation) {
            case 90:
                out.append(PdfContents.ROTATE90);
                out.append(page.getTop());
                out.append(' ').append('0').append(PdfContents.ROTATEFINAL);
                break;
            case 180:
                out.append(PdfContents.ROTATE180);
                out.append(page.getRight());
                out.append(' ');
                out.append(page.getTop());
                out.append(PdfContents.ROTATEFINAL);
                break;
            case 270:
                out.append(PdfContents.ROTATE270);
                out.append('0').append(' ');
                out.append(page.getRight());
                out.append(PdfContents.ROTATEFINAL);
                break;
        }
    }

    void alterContents() throws IOException {
        for (Object o : pagesToContent.values()) {
            PageStamp ps = (PageStamp) o;
            PdfDictionary pageN = ps.pageN;
            markUsed(pageN);
            PdfArray ar = null;
            PdfObject content = PdfReader.getPdfObject(pageN.get(PdfName.CONTENTS), pageN);
            if (content == null) {
                ar = new PdfArray();
                pageN.put(PdfName.CONTENTS, ar);
            } else if (content.isArray()) {
                ar = (PdfArray) content;
                markUsed(ar);
            } else if (content.isStream()) {
                ar = new PdfArray();
                ar.add(pageN.get(PdfName.CONTENTS));
                pageN.put(PdfName.CONTENTS, ar);
            } else {
                ar = new PdfArray();
                pageN.put(PdfName.CONTENTS, ar);
            }
            ByteBuffer out = new ByteBuffer();
            if (ps.under != null) {
                out.append(PdfContents.SAVESTATE);
                applyRotation(pageN, out);
                out.append(ps.under.getInternalBuffer());
                out.append(PdfContents.RESTORESTATE);
            }
            if (ps.over != null)
                out.append(PdfContents.SAVESTATE);
            PdfStream stream = new PdfStream(out.toByteArray());
            stream.flateCompress(compressionLevel);
            ar.addFirst(addToBody(stream).getIndirectReference());
            out.reset();
            if (ps.over != null) {
                out.append(' ');
                out.append(PdfContents.RESTORESTATE);
                ByteBuffer buf = ps.over.getInternalBuffer();
                out.append(buf.getBuffer(), 0, ps.replacePoint);
                out.append(PdfContents.SAVESTATE);
                applyRotation(pageN, out);
                out.append(buf.getBuffer(), ps.replacePoint, buf.size() - ps.replacePoint);
                out.append(PdfContents.RESTORESTATE);
                stream = new PdfStream(out.toByteArray());
                stream.flateCompress(compressionLevel);
                ar.add(addToBody(stream).getIndirectReference());
            }
            alterResources(ps);
        }
    }

    void alterResources(PageStamp ps) {
        ps.pageN.put(PdfName.RESOURCES, ps.pageResources.getResources());
    }

    protected int getNewObjectNumber(PdfReader reader, int number, int generation) {
        IntHashtable ref = readers2intrefs.get(reader);
        if (ref != null) {
            int n = ref.get(number);
            if (n == 0) {
                n = getIndirectReferenceNumber();
                ref.put(number, n);
            }
            return n;
        }
        if (currentPdfReaderInstance == null) {
            if (append && number < initialXrefSize)
                return number;
            int n = myXref.get(number);
            if (n == 0) {
                n = getIndirectReferenceNumber();
                myXref.put(number, n);
            }
            return n;
        }
        else
            return currentPdfReaderInstance.getNewObjectNumber(number, generation);
    }

    /**
     * @param reader
     * @param openFile
     * @throws IOException
     */
    public void registerReader(PdfReader reader, boolean openFile) throws IOException {
        if (readers2intrefs.containsKey(reader))
            return;
        readers2intrefs.put(reader, new IntHashtable());
        if (openFile) {
            RandomAccessFileOrArray raf = reader.getSafeFile();
            readers2file.put(reader, raf);
            raf.reOpen();
        }
    }

    /**
     * @param reader
     */
    public void unRegisterReader(PdfReader reader) {
        if (!readers2intrefs.containsKey(reader))
            return;
        readers2intrefs.remove(reader);
        RandomAccessFileOrArray raf = readers2file.get(reader);
        if (raf == null)
            return;
        readers2file.remove(reader);
        try{raf.close();}catch(Exception e){}
    }

    static void findAllObjects(PdfReader reader, PdfObject obj, IntHashtable hits) {
        if (obj == null)
            return;
        switch (obj.type()) {
            case PdfObject.INDIRECT:
                PRIndirectReference iref = (PRIndirectReference)obj;
                if (reader != iref.getReader())
                    return;
                if (hits.containsKey(iref.getNumber()))
                    return;
                hits.put(iref.getNumber(), 1);
                findAllObjects(reader, PdfReader.getPdfObject(obj), hits);
                return;
            case PdfObject.ARRAY:
                PdfArray a = (PdfArray)obj;
                for (int k = 0; k < a.size(); ++k) {
                    findAllObjects(reader, a.getPdfObject(k), hits);
                }
                return;
            case PdfObject.DICTIONARY:
            case PdfObject.STREAM:
                PdfDictionary dic = (PdfDictionary)obj;
                for (PdfName name : dic.getKeys()) {
                    findAllObjects(reader, dic.get(name), hits);
                }
                return;
        }
    }

    PageStamp getPageStamp(int pageNum) {
        PdfDictionary pageN = reader.getPageN(pageNum);
        PageStamp ps = pagesToContent.get(pageN);
        if (ps == null) {
            ps = new PageStamp(this, reader, pageN);
            pagesToContent.put(pageN, ps);
        }
        return ps;
    }

    PdfContentByte getUnderContent(int pageNum) {
        if (pageNum < 1 || pageNum > reader.getNumberOfPages())
            return null;
        PageStamp ps = getPageStamp(pageNum);
        if (ps.under == null)
            ps.under = new StampContent(this, ps);
        return ps.under;
    }

    PdfContentByte getOverContent(int pageNum) {
        if (pageNum < 1 || pageNum > reader.getNumberOfPages())
            return null;
        PageStamp ps = getPageStamp(pageNum);
        if (ps.over == null)
            ps.over = new StampContent(this, ps);
        return ps.over;
    }

    /** Getter for property rotateContents.
     * @return Value of property rotateContents.
     *
     */
    boolean isRotateContents() {
        return this.rotateContents;
    }

    /** Setter for property rotateContents.
     * @param rotateContents New value of property rotateContents.
     *
     */
    void setRotateContents(boolean rotateContents) {
        this.rotateContents = rotateContents;
    }

    boolean isContentWritten() {
        return body.size() > 1;
    }

    void sweepKids(PdfObject obj) {
        PdfObject oo = PdfReader.killIndirect(obj);
        if (oo == null || !oo.isDictionary())
            return;
        PdfDictionary dic = (PdfDictionary)oo;
        PdfArray kids = (PdfArray)PdfReader.killIndirect(dic.get(PdfName.KIDS));
        if (kids == null)
            return;
        for (int k = 0; k < kids.size(); ++k) {
            sweepKids(kids.getPdfObject(k));
        }
    }

    /**
     * @see PdfWriter#getPageReference(int)
     */
    public PdfIndirectReference getPageReference(int page) {
        PdfIndirectReference ref = reader.getPageOrigRef(page);
        if (ref == null)
            throw new IllegalArgumentException(MessageLocalization.getComposedMessage("invalid.page.number.1", page));
        return ref;
    }

    private void outlineTravel(PRIndirectReference outline) {
        while (outline != null) {
            PdfDictionary outlineR = (PdfDictionary)PdfReader.getPdfObjectRelease(outline);
            PRIndirectReference first = (PRIndirectReference)outlineR.get(PdfName.FIRST);
            if (first != null) {
                outlineTravel(first);
            }
            PdfReader.killIndirect(outlineR.get(PdfName.DEST));
            PdfReader.killIndirect(outlineR.get(PdfName.A));
            PdfReader.killIndirect(outline);
            outline = (PRIndirectReference)outlineR.get(PdfName.NEXT);
        }
    }

    void deleteOutlines() {
        PdfDictionary catalog = reader.getCatalog();
        PRIndirectReference outlines = (PRIndirectReference)catalog.get(PdfName.OUTLINES);
        if (outlines == null)
            return;
        outlineTravel(outlines);
        PdfReader.killIndirect(outlines);
        catalog.remove(PdfName.OUTLINES);
        markUsed(catalog);
    }


    void addFileAttachments() throws IOException {
        Map<String, PdfIndirectReference> fs = pdf.getDocumentFileAttachment();
        if (fs.isEmpty())
            return;
        PdfDictionary catalog = reader.getCatalog();
        PdfDictionary names = (PdfDictionary)PdfReader.getPdfObject(catalog.get(PdfName.NAMES), catalog);
        if (names == null) {
            names = new PdfDictionary();
            catalog.put(PdfName.NAMES, names);
            markUsed(catalog);
        }
        markUsed(names);
        Map<String, PdfObject> old = PdfNameTree.readTree((PdfDictionary)PdfReader.getPdfObjectRelease(names.get(PdfName.EMBEDDEDFILES)));
        for (Map.Entry<String, PdfIndirectReference> entry : fs.entrySet()) {
            String name = entry.getKey();
            int k = 0;
            String nn = name;
            while (old.containsKey(nn)) {
                ++k;
                nn += " " + k;
            }
            old.put(nn, entry.getValue());
        }
        PdfDictionary tree = PdfNameTree.writeTree(old, this);
        // Remove old EmbeddedFiles object if preset
        PdfObject oldEmbeddedFiles = names.get(PdfName.EMBEDDEDFILES);
        if (oldEmbeddedFiles != null) {
            PdfReader.killIndirect(oldEmbeddedFiles);
        }

        // Add new EmbeddedFiles object
        names.put(PdfName.EMBEDDEDFILES, addToBody(tree).getIndirectReference());
    }

    /**
     * Set the signature flags.
     * @param f the flags. This flags are ORed with current ones
     */
    public void setSigFlags(int f) {
        sigFlags |= f;
    }


    /**
     * Always throws an <code>UnsupportedOperationException</code>.
     * @param seconds ignore
     */
    public void setDuration(int seconds) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page"));
    }

    /**
     * Always throws an <code>UnsupportedOperationException</code>.
     * @param transition ignore
     */
    public void setTransition(PdfTransition transition) {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.setpageaction.pdfname.actiontype.pdfaction.action.int.page"));
    }

    /**
     * Sets the display duration for the page (for presentations)
     * @param seconds   the number of seconds to display the page. A negative value removes the entry
     * @param page the page where the duration will be applied. The first page is 1
     */
    void setDuration(int seconds, int page) {
        PdfDictionary pg = reader.getPageN(page);
        if (seconds < 0)
            pg.remove(PdfName.DUR);
        else
            pg.put(PdfName.DUR, new PdfNumber(seconds));
        markUsed(pg);
    }

    /**
     * Sets the transition for the page
     * @param transition   the transition object. A <code>null</code> removes the transition
     * @param page the page where the transition will be applied. The first page is 1
     */
    void setTransition(PdfTransition transition, int page) {
        PdfDictionary pg = reader.getPageN(page);
        if (transition == null)
            pg.remove(PdfName.TRANS);
        else
            pg.put(PdfName.TRANS, transition.getTransitionDictionary());
        markUsed(pg);
    }

    protected void markUsed(PdfObject obj) {
        if (append && obj != null) {
            PRIndirectReference ref = null;
            if (obj.type() == PdfObject.INDIRECT)
                ref = (PRIndirectReference)obj;
            else
                ref = obj.getIndRef();
            if (ref != null)
                marked.put(ref.getNumber(), 1);
        }
    }

    protected void markUsed(int num) {
        if (append)
            marked.put(num, 1);
    }

    /**
     * Getter for property append.
     * @return Value of property append.
     */
    boolean isAppend() {
        return append;
    }


    public PdfContentByte getDirectContentUnder() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.getundercontent.or.pdfstamper.getovercontent"));
    }

    public PdfContentByte getDirectContent() {
        throw new UnsupportedOperationException(MessageLocalization.getComposedMessage("use.pdfstamper.getundercontent.or.pdfstamper.getovercontent"));
    }

    /**
     * Reads the OCProperties dictionary from the catalog of the existing document
     * and fills the documentOCG, documentOCGorder and OCGRadioGroup variables in PdfWriter.
     * Note that the original OCProperties of the existing document can contain more information.
     * @since    2.1.2
     */
    protected void readOCProperties() {
        if (!documentOCG.isEmpty()) {
            return;
        }
        PdfDictionary dict = reader.getCatalog().getAsDict(PdfName.OCPROPERTIES);
        if (dict == null) {
            return;
        }
        PdfArray ocgs = dict.getAsArray(PdfName.OCGS);
        PdfIndirectReference ref;
        PdfLayer layer;
        Map<String, PdfLayer> ocgmap = new HashMap<>();
        for (PdfObject pdfObject : ocgs.getElements()) {
            ref = (PdfIndirectReference)pdfObject;
            layer = new PdfLayer(null);
            layer.setRef(ref);
            layer.setOnPanel(false);
            layer.merge((PdfDictionary)PdfReader.getPdfObject(ref));
            ocgmap.put(ref.toString(), layer);
        }
        PdfDictionary d = dict.getAsDict(PdfName.D);
        PdfArray off = d.getAsArray(PdfName.OFF);
        if (off != null) {
            for (PdfObject pdfObject : off.getElements() ) {
                ref = (PdfIndirectReference)pdfObject;
                layer = ocgmap.get(ref.toString());
                layer.setOn(false);
            }
        }
        PdfArray order = d.getAsArray(PdfName.ORDER);
        if (order != null) {
            addOrder(null, order, ocgmap);
        }
        documentOCG.addAll(ocgmap.values());
        OCGRadioGroup = d.getAsArray(PdfName.RBGROUPS);
        OCGLocked = d.getAsArray(PdfName.LOCKED);
        if (OCGLocked == null)
            OCGLocked = new PdfArray();
    }

    /**
     * Recursive method to reconstruct the documentOCGorder variable in the writer.
     * @param    parent    a parent PdfLayer (can be null)
     * @param    arr        an array possibly containing children for the parent PdfLayer
     * @param    ocgmap    a HashMap with indirect reference Strings as keys and PdfLayer objects as values.
     * @since    2.1.2
     */
    private void addOrder(PdfLayer parent, PdfArray arr, Map ocgmap) {
        PdfObject obj;
        PdfLayer layer;
        for (int i = 0; i < arr.size(); i++) {
            obj = arr.getPdfObject(i);
            if (obj.isIndirect()) {
                layer = (PdfLayer)ocgmap.get(obj.toString());
                layer.setOnPanel(true);
                registerLayer(layer);
                if (parent != null) {
                    parent.addChild(layer);
                }
                if (arr.size() > i + 1 && arr.getPdfObject(i + 1).isArray()) {
                    i++;
                    addOrder(layer, (PdfArray)arr.getPdfObject(i), ocgmap);
                }
            }
            else if (obj.isArray()) {
                PdfArray sub = (PdfArray)obj;
                if (sub.isEmpty()) return;
                obj = sub.getPdfObject(0);
                if (obj.isString()) {
                    layer = new PdfLayer(obj.toString());
                    layer.setOnPanel(true);
                    registerLayer(layer);
                    if (parent != null) {
                        parent.addChild(layer);
                    }
                    PdfArray array = new PdfArray();
                    sub.getElements().forEach(array::add);
                    addOrder(layer, array, ocgmap);
                }
                else {
                    addOrder(parent, (PdfArray)obj, ocgmap);
                }
            }
        }
    }

    /**
     * Gets the PdfLayer objects in an existing document as a Map
     * with the names/titles of the layers as keys.
     * @return    a Map with all the PdfLayers in the document (and the name/title of the layer as key)
     * @since    2.1.2
     */
    public Map getPdfLayers() {
        if (documentOCG.isEmpty()) {
            readOCProperties();
        }
        Map<String, PdfLayer> map = new HashMap<>();
        PdfLayer layer;
        String key;
        for (Object o : documentOCG) {
            layer = (PdfLayer) o;
            if (layer.getTitle() == null) {
                key = layer.getAsString(PdfName.NAME).toString();
            } else {
                key = layer.getTitle();
            }
            if (map.containsKey(key)) {
                int seq = 2;
                String tmp = key + "(" + seq + ")";
                while (map.containsKey(tmp)) {
                    seq++;
                    tmp = key + "(" + seq + ")";
                }
                key = tmp;
            }
            map.put(key, layer);
        }
        return map;
    }

    static class PageStamp {

        PdfDictionary pageN;
        StampContent under;
        StampContent over;
        PageResources pageResources;
        int replacePoint = 0;

        PageStamp(PdfStamperImp stamper, PdfReader reader, PdfDictionary pageN) {
            this.pageN = pageN;
            pageResources = new PageResources();
            PdfDictionary resources = pageN.getAsDict(PdfName.RESOURCES);
            pageResources.setOriginalResources(resources, stamper.namePtr);
        }
    }

    /** These methods are used by PdfStamper to override some PDF properties when signing PDF files. */

    public boolean isIncludeFileID() {
        return includeFileID;
    }

    public void setIncludeFileID(boolean includeFileID) {
        this.includeFileID = includeFileID;
    }

    public PdfObject getOverrideFileId() {
        return overrideFileId;
    }

    public void setOverrideFileId(PdfObject overrideFileId) {
        this.overrideFileId = overrideFileId;
    }
}
