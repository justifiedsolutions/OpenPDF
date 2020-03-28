package com.justifiedsolutions.openpdf.text.pdf;

import com.justifiedsolutions.openpdf.text.pdf.PdfWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.justifiedsolutions.openpdf.text.Document;
import com.justifiedsolutions.openpdf.text.DocumentException;
import com.justifiedsolutions.openpdf.text.PageSize;

class PdfTestBase {

    static Document createTempPdf(String filename) throws DocumentException, IOException {
        // create a new file in target dir
        return createPdf(
                new FileOutputStream(
                        File.createTempFile(filename, ".pdf")));
    }

    static Document createPdf(OutputStream outputStream) throws DocumentException {
        // create a new document
        Document document = new Document(PageSize.A4);
        // generate file
        PdfWriter.getInstance(document, outputStream);
        return document;
    }

    static Document createPdf(String filename) throws FileNotFoundException {
        FileOutputStream outputStream = new FileOutputStream(filename);
        return createPdf(outputStream);
    }

}
